package outdatedRequests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.Protocol;
import server.IRequestHandler;
import server.Server;

public class DeleteRequestHandler implements IRequestHandler{

	@Override
	public HttpResponse handle(HttpRequest request, HttpResponse response, Server server) {
		// Handling DELETE request here
		// Get relative URI path from request
		String uri = request.getUri();
		// Get root directory path from server
		String rootDirectory = server.getRootDirectory();
		// Combine them together to form absolute file path
		File file = new File(rootDirectory + uri);
		// Check if the file exists
		if(file.exists()) {
			if(file.isDirectory()) {
				// Look for default index.html file in a directory
				String location = rootDirectory + request.getHeader().get("filename");
				file = new File(location);
				if (file.exists()) {
					try {
						Files.delete(file.toPath());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// Lets create 200 OK response
//					response = HttpResponseFactory.create200OK(null, Protocol.CLOSE);
					response = new HttpResponse.HttpResponseBuilder(Protocol.CLOSE).
							status(Protocol.OK_CODE).
							phrase(Protocol.OK_TEXT).
							version(Protocol.VERSION).
							build();
				}
				else {
					// File does not exist so lets create 404 file not found code
//					response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
					response = new HttpResponse.HttpResponseBuilder(Protocol.CONNECTION).
							status(Protocol.NOT_FOUND_CODE).
							phrase(Protocol.NOT_FOUND_TEXT).
							version(Protocol.VERSION).
							build();
				}
			}
			else {
				try {
					Files.delete(file.toPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Lets create 200 OK response
//				response = HttpResponseFactory.create200OK(null, Protocol.CLOSE);
				response = new HttpResponse.HttpResponseBuilder(Protocol.CLOSE).
						status(Protocol.OK_CODE).
						phrase(Protocol.OK_TEXT).
						version(Protocol.VERSION).
						build();
			}
		}
		else {
			// File does not exist so lets create 404 file not found code
//			response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
			response = new HttpResponse.HttpResponseBuilder(Protocol.CONNECTION).
					status(Protocol.NOT_FOUND_CODE).
					phrase(Protocol.NOT_FOUND_TEXT).
					version(Protocol.VERSION).
					build();
		}	
		return response;
	}

}
