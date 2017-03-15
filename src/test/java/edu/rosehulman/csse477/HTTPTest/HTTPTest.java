package edu.rosehulman.csse477.HTTPTest;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import outdatedRequests.DeleteRequestHandler;
import outdatedRequests.GetRequestHandler;
import outdatedRequests.HeadRequestHandler;
import outdatedRequests.PostRequestHandler;
import outdatedRequests.PutRequestHandler;
import protocol.HttpRequest;
import protocol.HttpResponse;
import server.Server;

public class HTTPTest {
	HttpRequest request;
	Server server;
	File testFile;
	
	@Before
	public void setUp() throws Exception {
		testFile = new File("rootDirectory/test.txt");
		testFile.createNewFile();

		server = new Server();
	}

	
	@Test public void testGET() throws Exception {
		String requestString = "GET test.txt HTTP/1.1 \n Host: localhost \n Author: Tester \n filename: test.txt \n\n";
		InputStream stream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
		request = HttpRequest.read(stream);
		GetRequestHandler grh = new GetRequestHandler();
		HttpResponse response = null;
		response = grh.handle(request, response, server);

		assertEquals(200, response.getStatus());
    }
	
	@Test public void testGETNotFound() throws Exception {
		String requestString = "GET /nothere.txt HTTP/1.1 \n Host: localhost \n Author: Tester \n filename: nothere.txt \n\n";
		InputStream stream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
		request = HttpRequest.read(stream);
		GetRequestHandler grh = new GetRequestHandler();
		HttpResponse response = null;
		response = grh.handle(request, response, server);

		assertEquals(404, response.getStatus());
    }
	
	@Test public void testHEAD() throws Exception {
		String requestString = "HEAD /test.txt HTTP/1.1 \n Host: localhost \n Author: Tester \n filename: test.txt \n\n";
		InputStream stream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
		request = HttpRequest.read(stream);
		HeadRequestHandler hrh = new HeadRequestHandler();
		HttpResponse response = null;
		response = hrh.handle(request, response, server);

		assertEquals(200, response.getStatus());
    }
	
	@Test public void testHEADNotFound() throws Exception {
		String requestString = "HEAD /nothere.txt HTTP/1.1 \n Host: localhost \n Author: Tester \n filename: nothere.txt \n\n";
		InputStream stream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
		request = HttpRequest.read(stream);
		HeadRequestHandler hrh = new HeadRequestHandler();
		HttpResponse response = null;
		response = hrh.handle(request, response, server);

		assertEquals(404, response.getStatus());
    }
	
	@Test public void testPUT() throws Exception {
		String requestString = "PUT / HTTP/1.1 \n Host: localhost \n Author: Tester \n Content-Length: 7 \n filename: test.txt \n\nputting";
		InputStream stream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
		request = HttpRequest.read(stream);
		PutRequestHandler prh = new PutRequestHandler();
		HttpResponse response = null;
		response = prh.handle(request, response, server);

		assertEquals(200, response.getStatus());
    }
	
	@Test public void testPOSTthenPUT() throws Exception {
		String requestString = "POST / HTTP/1.1 \n Host: localhost \n Author: Tester \n Content-Length: 7 \n filename: test.txt \n\nposting";
		InputStream stream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
		request = HttpRequest.read(stream);
		PostRequestHandler prh = new PostRequestHandler();
		HttpResponse response = null;
		response = prh.handle(request, response, server);
		
		assertEquals(200, response.getStatus());
		
		requestString = "PUT / HTTP/1.1 \n Host: localhost \n Author: Tester \n Content-Length: 7 \n filename: test.txt \n\nputting";
		stream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
		request = HttpRequest.read(stream);
		PutRequestHandler pprh = new PutRequestHandler();
		response = null;
		response = prh.handle(request, response, server);

		assertEquals(200, response.getStatus());
    }
	
	@Test public void testPUTNewFile() throws Exception {
		String requestString = "PUT / HTTP/1.1 \n Host: localhost \n Author: Tester \n Content-Length: 13 \n filename: newput.txt \n\nputting again";
		InputStream stream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
		request = HttpRequest.read(stream);
		PutRequestHandler prh = new PutRequestHandler();
		HttpResponse response = null;
		response = prh.handle(request, response, server);
		
		assertEquals(200, response.getStatus());
	}
	
	@Test public void testPOST() throws Exception {
		String requestString = "POST / HTTP/1.1 \n Host: localhost \n Author: Tester \n Content-Length: 7 \n filename: test.txt \n\nposting";
		InputStream stream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
		request = HttpRequest.read(stream);
		PostRequestHandler prh = new PostRequestHandler();
		HttpResponse response = null;
		response = prh.handle(request, response, server);
		
		assertEquals(200, response.getStatus());
	}
	
	@Test public void testPOSTNewFile() throws Exception {
		String requestString = "POST / HTTP/1.1 \n Host: localhost \n Author: Tester \n Content-Length: 7 \n filename: newpost.txt \n\nposting";
		InputStream stream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
		request = HttpRequest.read(stream);
		PostRequestHandler prh = new PostRequestHandler();
		HttpResponse response = null;
		response = prh.handle(request, response, server);

		assertEquals(200, response.getStatus());
    }
	
	@Test public void testDELETE() throws Exception {
		String requestString = "DELETE / HTTP/1.1 \n Host: localhost \n Author: Tester \n filename: test.txt \n\n";
		InputStream stream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
		request = HttpRequest.read(stream);
		DeleteRequestHandler drh = new DeleteRequestHandler();
		HttpResponse response = null;
		response = drh.handle(request, response, server);

		assertEquals(200, response.getStatus());
		assertEquals(false, testFile.exists());
    }
	
	@Test public void testDELETENotFound() throws Exception {
		String requestString = "DELETE / HTTP/1.1 \n Host: localhost \n Author: Tester \n filename: nothing.txt \n\n";
		InputStream stream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
		request = HttpRequest.read(stream);
		DeleteRequestHandler drh = new DeleteRequestHandler();
		HttpResponse response = null;
		response = drh.handle(request, response, server);

		assertEquals(404, response.getStatus());
    }
	
	@After
	public void tearDown() throws Exception {
		server.stop();
		File put = new File("rootDirectory/newput.txt");
		if (put.exists()) {
			Files.delete(put.toPath());
		}
		
		File post = new File("rootDirectory/newpost.txt");
		if (post.exists()) {
			Files.delete(post.toPath());
		}
		
		if (testFile.exists()) {
			Files.delete(testFile.toPath());
		}
	}

}
