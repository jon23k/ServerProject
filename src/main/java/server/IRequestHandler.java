package server;

import protocol.HttpRequest;
import protocol.HttpResponse;

public interface IRequestHandler {
	
	public HttpResponse handle(HttpRequest request, HttpResponse response, Server server);

}
