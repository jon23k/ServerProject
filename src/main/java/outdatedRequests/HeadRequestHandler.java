package outdatedRequests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.Protocol;
import server.IRequestHandler;
import server.Server;

public class HeadRequestHandler implements IRequestHandler {

	@Override
	public HttpResponse handle(HttpRequest request, HttpResponse response, Server server) {
		// Handling HEAD request here
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
				String location = rootDirectory + uri + System.getProperty("file.separator") + Protocol.DEFAULT_FILE;
				file = new File(location);
				if(file.exists()) {
					// Lets create 200 OK response
//					response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
					String length = Long.toString(file.length());
					int i = file.getName().indexOf('.');
					String fileType = file.getName().substring(i);
					response = new HttpResponse.HttpResponseBuilder(Protocol.CLOSE).
							status(Protocol.OK_CODE).
							phrase(Protocol.OK_TEXT).
							version(Protocol.VERSION).
							contentLength(length).
							fileName(file.getName()).
							fileType(fileType).
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
			else { // Its a file
				// Lets create 200 OK response
//				response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
				String length = Long.toString(file.length());
				int i = file.getName().indexOf('.');
				String fileType = file.getName().substring(i);
				response = new HttpResponse.HttpResponseBuilder(Protocol.CLOSE).
						status(Protocol.OK_CODE).
						phrase(Protocol.OK_TEXT).
						version(Protocol.VERSION).
						contentLength(length).
						fileName(file.getName()).
						fileType(fileType).
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
