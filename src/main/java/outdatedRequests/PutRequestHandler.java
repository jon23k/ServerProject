package outdatedRequests;

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

public class PutRequestHandler implements IRequestHandler {

	@Override
	public HttpResponse handle(HttpRequest request, HttpResponse response, Server server) {
		// Handling PUT request here
		// Get relative URI path from request
		String uri = request.getUri();
		// Get root directory path from server
		String rootDirectory = server.getRootDirectory();
		// Combine them together to form absolute file path
		File file = new File(rootDirectory + uri + "/" + request.getHeader().get("filename"));
		// Check if the file exists
		if (file.exists()) {
			PrintWriter writer;
			FileWriter fw;
			try {
				fw = new FileWriter(file, false);
				writer = new PrintWriter(fw, false);
				writer.flush();
				writer.println(request.getBody());
				writer.close();
				fw.close();
			} catch (IOException e) {
				// File does not exist so lets create 404 file not found code
				response = new HttpResponse.HttpResponseBuilder(Protocol.CONNECTION).status(Protocol.NOT_FOUND_CODE)
						.phrase(Protocol.NOT_FOUND_TEXT).version(Protocol.VERSION).build();
				e.printStackTrace();
			}

			// Lets create 200 OK response
			response = new HttpResponse.HttpResponseBuilder(Protocol.CLOSE).status(Protocol.OK_CODE)
					.phrase(Protocol.OK_TEXT).version(Protocol.VERSION).file(file).build();
		} else {
			// make a new file at the location of what the request's filename
			// was supposed to be
			file.getParentFile().mkdirs();
			PrintWriter writer;
			FileWriter fw;
			try {
				fw = new FileWriter(file, false);
				writer = new PrintWriter(fw, false);
				writer.flush();
				writer.print(request.getBody());
				writer.close();
				fw.close();
			} catch (IOException e) {
				// File does not exist so lets create 404 file not found code
				response = new HttpResponse.HttpResponseBuilder(Protocol.CONNECTION).status(Protocol.NOT_FOUND_CODE)
						.phrase(Protocol.NOT_FOUND_TEXT).version(Protocol.VERSION).build();
				e.printStackTrace();
			}
			// Lets create 200 OK response
			response = new HttpResponse.HttpResponseBuilder(Protocol.CLOSE).status(Protocol.OK_CODE)
					.phrase(Protocol.OK_TEXT).version(Protocol.VERSION).file(file).build();
		}
		return response;
	}

}
