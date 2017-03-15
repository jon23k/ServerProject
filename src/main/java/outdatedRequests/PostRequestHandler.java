package outdatedRequests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.Protocol;
import server.IRequestHandler;
import server.Server;

public class PostRequestHandler implements IRequestHandler {

	@Override
	public HttpResponse handle(HttpRequest request, HttpResponse response, Server server) {
		// TODO Auto-generated method stub

		// Handling POST request here
		// Get relative URI path from request
		String uri = request.getUri();
		// Get root directory path from server
		String rootDirectory = server.getRootDirectory();
		// Combine them together to form absolute file path
		File file = new File(rootDirectory + uri + "/" + request.getHeader().get("filename"));
		// Check if the file exists
		if (file.exists()) {
			try (FileWriter fw = new FileWriter(file, true);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter out = new PrintWriter(bw)) {
				out.println(request.getBody());
			} catch (IOException e) {
				e.printStackTrace();
			}
			// Lets create 200 OK response
			response = new HttpResponse.HttpResponseBuilder(Protocol.CLOSE).status(Protocol.OK_CODE)
					.phrase(Protocol.OK_TEXT).version(Protocol.VERSION).file(file).build();
		} else {
			Server.accessLogger.info("PostRequestHander: creating directory: " + file.getAbsolutePath());
			file.getParentFile().mkdirs();

			try {
				// make a new file at the location of what the request's
				// filename was supposed to be
				PrintWriter writer = new PrintWriter(file, "UTF-8");
				writer.print(request.getBody());
				writer.close();
			} catch (IOException e) {
				// do something
			}
			// Lets create 200 OK response
			response = new HttpResponse.HttpResponseBuilder(Protocol.CLOSE).status(Protocol.OK_CODE)
					.phrase(Protocol.OK_TEXT).version(Protocol.VERSION).file(file).build();
		}
		return response;

	}

}
