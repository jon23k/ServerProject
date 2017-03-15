package edu.rosehulman.csse477.HTTPTest;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import protocol.HttpRequest;

public class HttpRequestTest {

	@Test
	public void testGetHeader() throws Exception {
		String requestString = "GET /nothere.txt HTTP/1.1 \n Host: localhost \n Author: Tester \n filename: nothere.txt \n\n";
		InputStream stream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
		HttpRequest request = HttpRequest.read(stream);
		
		assertEquals("{filename=nothere.txt, author=Tester, host=localhost}", request.getHeader().toString());
	}
	
	@Test
	public void testGetUri() throws Exception {
		String requestString = "GET /nothere.txt HTTP/1.1 \n Host: localhost \n Author: Tester \n filename: nothere.txt \n\n";
		InputStream stream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
		HttpRequest request = HttpRequest.read(stream);
		
		assertEquals("/nothere.txt", request.getUri());
	}
	
	@Test
	public void testGetVersion() throws Exception {
		String requestString = "GET /nothere.txt HTTP/1.1 \n Host: localhost \n Author: Tester \n filename: nothere.txt \n\n";
		InputStream stream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
		HttpRequest request = HttpRequest.read(stream);
		
		assertEquals("HTTP/1.1", request.getVersion());
	}
	
	@Test
	public void testGetMethod() throws Exception {
		String requestString = "GET /nothere.txt HTTP/1.1 \n Host: localhost \n Author: Tester \n filename: nothere.txt \n\n";
		InputStream stream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
		HttpRequest request = HttpRequest.read(stream);
		
		assertEquals("GET", request.getMethod());
	}
	
	@Test
	public void testToString() throws Exception {
		String requestString = "GET /nothere.txt HTTP/1.1 \n Host: localhost \n Author: Tester \n filename: nothere.txt \n\n";
		InputStream stream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
		HttpRequest request = HttpRequest.read(stream);

		assertEquals("----------- Header ----------------\n"+
		"GET /nothere.txt HTTP/1.1\n"+
				"filename: nothere.txt\n"+
		"author: Tester\n"+
				"host: localhost\n"+
		"------------- Body ---------------\n"+
				"----------------------------------\n", request.toString());
	}
	
	@Test
	public void testRead() throws Exception {
		String requestString = "GET /nothere.txt HTTP/1.1 \n Host: localhost \n Author: Tester \n filename: nothere.txt \n\n";
		InputStream stream = new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8));
		HttpRequest request = HttpRequest.read(stream);
		
		String newRequestString = "GET /nothere.txt HTTP/1.1 \n Host: localhost \n Author: Tester \n filename: nothere.txt \n\n";
		InputStream newStream = new ByteArrayInputStream(newRequestString.getBytes(StandardCharsets.UTF_8));
		
		assertEquals(request.toString(), HttpRequest.read(newStream).toString());
	}

}
