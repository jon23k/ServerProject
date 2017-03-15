package edu.rosehulman.csse477.HTTPTest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


import protocol.HttpResponse;
import protocol.Protocol;

public class HttpResponseTest {

	@Test
	public void testStatus() {
		HttpResponse response = new HttpResponse.HttpResponseBuilder(Protocol.CLOSE).
				status(Protocol.OK_CODE).
				phrase(Protocol.OK_TEXT).
				version(Protocol.VERSION).
				build();
		assertEquals(200, response.getStatus());
	}
	
	@Test
	public void testPhrase() {
		HttpResponse response = new HttpResponse.HttpResponseBuilder(Protocol.CLOSE).
				status(Protocol.OK_CODE).
				phrase(Protocol.OK_TEXT).
				version(Protocol.VERSION).
				build();
		assertEquals("OK", response.getPhrase());
	}
	
	@Test
	public void testVersion() {
		HttpResponse response = new HttpResponse.HttpResponseBuilder(Protocol.CLOSE).
				status(Protocol.OK_CODE).
				phrase(Protocol.OK_TEXT).
				version(Protocol.VERSION).
				build();
		assertEquals("HTTP/1.1", response.getVersion());
	}
	
	@Test
	public void testFile() {
		HttpResponse response = new HttpResponse.HttpResponseBuilder(Protocol.CLOSE).
				status(Protocol.OK_CODE).
				phrase(Protocol.OK_TEXT).
				version(Protocol.VERSION).
				build();
		assertEquals(null, response.getFile());
	}

}
