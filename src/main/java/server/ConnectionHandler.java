package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import outdatedRequests.DeleteRequestHandler;
import outdatedRequests.GetRequestHandler;
import outdatedRequests.HeadRequestHandler;
import outdatedRequests.PostRequestHandler;
import outdatedRequests.PutRequestHandler;
import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.Protocol;
import protocol.ProtocolException;


/**
 * This class is responsible for handling a incoming request by creating a
 * {@link HttpRequest} object and sending the appropriate response be creating a
 * {@link HttpResponse} object. It implements {@link Runnable} to be used in
 * multi-threaded environment.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class ConnectionHandler implements Runnable {
	private Server server;
	private Socket socket;

	private Map<String, IRequestHandler> defaultHandlers;
	private Map<String, IRequestHandler> pluginHandlers;

	public ConnectionHandler(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
		this.defaultHandlers = new HashMap<String, IRequestHandler>();
		this.pluginHandlers = server.getPluginMap();

//		IRequestHandler getRequest = new GetRequestHandler();
//		IRequestHandler putRequest = new PutRequestHandler();
//		IRequestHandler postRequest = new PostRequestHandler();
//		IRequestHandler headRequest = new HeadRequestHandler();
//		IRequestHandler deleteRequest = new DeleteRequestHandler();
		
		
//		IRequestHandler getRequest = new DefaultGet();
//		IRequestHandler putRequest = new DefaultPut();
//		IRequestHandler postRequest = new DefaultPost();
//		IRequestHandler headRequest = new DefaultHeader();
//		IRequestHandler deleteRequest = new DefaultDelete();
//
//		defaultHandlers.put(Protocol.GET, getRequest);
//		defaultHandlers.put(Protocol.PUT, putRequest);
//		defaultHandlers.put(Protocol.POST, postRequest);
//		defaultHandlers.put(Protocol.HEAD, headRequest);
//		defaultHandlers.put(Protocol.DELETE, deleteRequest);
		


	}

	/**
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * The entry point for connection handler. It first parses incoming request
	 * and creates a {@link HttpRequest} object, then it creates an appropriate
	 * {@link HttpResponse} object and sends the response back to the client
	 * (web browser).
	 */
	public void run() {
		InputStream inStream = null;
		OutputStream outStream = null;

		try {
			inStream = this.socket.getInputStream();
			outStream = this.socket.getOutputStream();
		} catch (Exception e) {
			// Cannot do anything if we have exception reading input or output
			// stream
			// May be have text to log this for further analysis?
			e.printStackTrace();
			return;
		}

		// At this point we have the input and output stream of the socket
		// Now lets create a HttpRequest object
		HttpRequest request = null;
		HttpResponse response = null;
		try {
			request = HttpRequest.read(inStream);
			System.out.println(request);
		} catch (ProtocolException pe) {
			// We have some sort of protocol exception. Get its status code and
			// create response
			// We know only two kind of exception is possible inside
			// fromInputStream
			// Protocol.BAD_REQUEST_CODE and Protocol.NOT_SUPPORTED_CODE
			int status = pe.getStatus();
			if (status == Protocol.BAD_REQUEST_CODE) {
				response = new HttpResponse.HttpResponseBuilder(Protocol.CONNECTION).
						status(Protocol.BAD_REQUEST_CODE).
						phrase(Protocol.BAD_REQUEST_TEXT).
						version(Protocol.VERSION).
						build();
			}
			// TODO: Handle version not supported code as well
		} catch (Exception e) {
			e.printStackTrace();
			// For any other error, we will create bad request response as well
			response = new HttpResponse.HttpResponseBuilder(Protocol.CONNECTION).
					status(Protocol.BAD_REQUEST_CODE).
					phrase(Protocol.BAD_REQUEST_TEXT).
					version(Protocol.VERSION).
					build();
		}

		if (response != null) {
			// Means there was an error, now write the response object to the
			// socket
			try {
				response.write(outStream);
				// System.out.println(response);
			} catch (Exception e) {
				// We will ignore this exception
				e.printStackTrace();
			}

			return;
		}
		System.out.println("uri is: " + request.getUri());
		String uri = request.getUri();
		int first = uri.indexOf('/');
		uri = uri.substring(first+1);
		int last = uri.indexOf('/');
		String plugin = uri.substring(0,last+1);
		
//		int pluginEnds = uri.indexOf('/');
//		String pluginName = uri.substring(0, pluginEnds);
//
//		String key = request.getMethod().toUpperCase() + ":" + pluginName;
		
		String key = request.getMethod().toUpperCase() + ":" + plugin;
		Server.accessLogger.info("Key is: " + key);
		Server.accessLogger.info("Available keys are: " + server.getMethodMap().keySet());
		Server.accessLogger.info("Available keys are: " + server.getObjectMap().keySet());

		if (server.getMethodMap().get(key) != null) {
			Server.accessLogger.info("Request found in MethodMap");
			try {
				Object object = server.getObjectMap().get(server.getMethodMap().get(key));
				response = (HttpResponse) server.getMethodMap().get(key).invoke(object, request, response, server);
				Server.accessLogger.info("called the method");

			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else { //use default
			Server.accessLogger.info("Request not found in MethodMap. Using default implementation.");
			//response = defaultHandlers.get(request.getMethod().toUpperCase()).handle(request, response, server);
			//response = (HttpResponse) server.getMethodMap().get(request.getMethod().toUpperCase() + ":" + "default")inStream.invoke(object))
		}

		// DONE: So far response could be null for protocol version mismatch.
		// So this is a temporary patch for that problem and should be removed
		// after a response object is created for protocol version mismatch.
		if (response == null) {
			Server.errorLogger.error("Response is null, sending a 505 Not Supported");
			response = new HttpResponse.HttpResponseBuilder(Protocol.CONNECTION).
					status(Protocol.NOT_SUPPORTED_CODE).
					phrase(Protocol.NOT_SUPPORTED_TEXT).
					version(Protocol.VERSION).
					build();
		}

		try {
			// Write response and we are all done so close the socket
			response.write(outStream);
			// System.out.println(response);
			socket.close();
		} catch (Exception e) {
			// We will ignore this exception
			e.printStackTrace();
		}
	}

}
