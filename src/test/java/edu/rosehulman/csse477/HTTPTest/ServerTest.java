package edu.rosehulman.csse477.HTTPTest;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.Server;

public class ServerTest {
	Server testServer;

	@Before
	public void setUp() throws Exception {
		// get the root Directory & port number from the config file

		testServer = new Server();
		Thread runner = new Thread(testServer);
		runner.start();
	}

	@Test
	public void testGetRootDirectory() {
		assertEquals("rootDirectory/", testServer.getRootDirectory());
	}

	@Test
	public void testLoadJarAnnotations() throws ClassNotFoundException, IOException {
		String pathToJar = "jars/servletplugin.jar";
		File file = new File(pathToJar);
		if (file.exists()) {
			testServer.loadJarAnnotation(pathToJar);
			assertEquals(
					"[public protocol.HttpResponse ServletPlugin.doGet(protocol.HttpRequest,protocol.HttpResponse,server.Server)]",
					testServer.getMethodMap().values().toString());
		}
	}

	@Test
	public void testIsStopped() {
		testServer.stop();
		assertEquals(true, testServer.isStopped());
	}

	@After
	public void tearDown() throws Exception {
	}

}
