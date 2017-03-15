package app;

import java.awt.Desktop;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import server.Server;

/**
 * The entry point of the Simple Web Server (SWS).
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class SimpleWebServer {
	public static void main(String[] args) throws InterruptedException, IOException, URISyntaxException {
		
		//I call the property file here so it can be loaded and used
//		Properties prop = new Properties();
//		InputStream input = new FileInputStream("config.properties");
//		prop.load(input);
//		String rootDirectory = prop.getProperty("rootDirectory"); 
//		int port =  Integer.parseInt(prop.getProperty("portNumber"));
//		String pluginDirectory = prop.getProperty("pluginDirectory");

		// Create a run the server
		Server server = new Server();
		Thread runner = new Thread(server);
		runner.start();

		
		// TODO: Instead of just printing to the console, use proper logging mechanism.
		// SL4J/Log4J are some popular logging framework
//		System.out.format("Simple Web Server started at port %d and serving the %s directory ...%n", port, rootDirectory);
		
		// Wait for the server thread to terminate
		runner.join();
	}
}
