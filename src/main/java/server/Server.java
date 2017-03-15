/*
 * Server.java
 * Oct 7, 2012
 *
 * Simple Web Server (SWS) for CSSE 477
 * 
 * Copyright (C) 2012 Chandan Raj Rupakheti
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 * 
 */

package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

//import javax.xml.ws.handler.Handler;

import org.apache.log4j.Logger;

import watcher.MainWatch;

/**
 * This represents a welcoming server for the incoming TCP request from a HTTP
 * client such as a web browser.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class Server implements Runnable {
	private String rootDirectory;
	private String pluginDirectory;
	private int port;
	private boolean stop;
	private ServerSocket welcomeSocket;
	private Map<String, IRequestHandler> pluginMap;

	public static final Logger errorLogger = Logger.getLogger("errorLogger");;
	public static final Logger accessLogger = Logger.getLogger("reportsLogger");

	private Map<String, Method> requestToMethod;
	private Map<Method, Object> methodToObject;

	/**
	 * @param rootDirectory
	 * @param port
	 */
	public Server() {
		Properties prop = new Properties();
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream input = null;
		input = this.getClass().getResourceAsStream("/config.properties");
		try {
			prop.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.rootDirectory = prop.getProperty("rootDirectory"); 
		this.port =  Integer.parseInt(prop.getProperty("portNumber"));
		this.pluginDirectory = prop.getProperty("pluginDirectory");
		
		this.stop = false;
		
		new File(rootDirectory).mkdir();
		new File(pluginDirectory).mkdir();

		this.requestToMethod = new HashMap<String, Method>();
		this.methodToObject = new HashMap<Method, Object>();

	}

	/**
	 * Gets the root directory for this web server.
	 * 
	 * @return the rootDirectory
	 */
	public String getRootDirectory() {
		return this.rootDirectory;
	}

	/**
	 * Gets the port number for this web server.
	 * 
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * The entry method for the main server thread that accepts incoming TCP
	 * connection request and creates a {@link ConnectionHandler} for the
	 * request.
	 */
	public void run() {
		Server.accessLogger.info("Trying to run server...\n");
		try {
			this.welcomeSocket = new ServerSocket(port);
			Server.accessLogger.info("Server has started\n");

			// load initial jars
			populateHandlerHash();

			// Now keep welcoming new connections until stop flag is set to true

			// Makes a watcher to watch our "jars" directory for new and current
			// jars
			MainWatch watcher = new MainWatch(Paths.get(this.pluginDirectory));
			new Thread(watcher).start();
			while (true) {

				List<File> jarsToAdd = new ArrayList<File>();
				jarsToAdd.addAll(watcher.getJarsToAdd());
				for (File f : jarsToAdd) {
					loadJarAnnotation(this.pluginDirectory + "/" + f.getName());
					Server.accessLogger.info("Watcher updated! loading jar annotation for: " + f.getName());
				}

				List<File> jarsToRemove = new ArrayList<File>();
				jarsToRemove.addAll(watcher.getJarsToRemove());
				for (File f : jarsToRemove) {
					removeJarReferences(this.pluginDirectory + "/" + f.getName());
					Server.accessLogger.info("Watcher updated! removing jar references for: " + f.getName());

				}
				// Listen for incoming socket connection
				// This method block until somebody makes a request
				Socket connectionSocket = this.welcomeSocket.accept();

				// Come out of the loop if the stop flag is set
				if (this.stop)
					break;

				// Create a handler for this incoming connection and start the
				// handler in a new thread
				ConnectionHandler handler = new ConnectionHandler(this, connectionSocket);
				new Thread(handler).start();

			}
			this.welcomeSocket.close();
		} catch (Exception e) {
			Server.errorLogger.error(e, e);
			// e.printStackTrace();
		}
	}

	/**
	 * Stops the server from listening further.
	 */
	public synchronized void stop() {
		if (this.stop)
			return;

		// Set the stop flag to be true
		this.stop = true;
		try {
			// This will force welcomeSocket to come out of the blocked accept()
			// method
			// in the main loop of the start() method
			Socket socket = new Socket(InetAddress.getLocalHost(), port);

			// We do not have any other job for this socket so just close it
			socket.close();
		} catch (Exception e) {
			this.errorLogger.error(e.getMessage());
		}
	}

	/**
	 * Checks if the server is stopped or not.
	 * 
	 * @return
	 */
	public boolean isStopped() {
		if (this.welcomeSocket != null)
			return this.welcomeSocket.isClosed();
		return true;
	}

	public Map<String, IRequestHandler> getPluginMap() {
		// TODO Auto-generated method stub
		return this.pluginMap;
	}

	public Map<String, Method> getMethodMap() {
		return this.requestToMethod;
	}

	private String parseAnnotations(Method m) {
		Annotation annotation = m.getAnnotation(HandlerAnnotation.class);
		if (annotation != null) {
			Server.accessLogger.info("annotation not null");
			Server.accessLogger.info("annotation type: " + annotation.annotationType());
		}
		if (annotation instanceof HandlerAnnotation) {
			Server.accessLogger.info("annotation is instance of HandlerAnnotation");
			HandlerAnnotation myAnnotation = (HandlerAnnotation) annotation;
			System.out.println("request Type: " + myAnnotation.requestType());
			System.out.println("path: " + myAnnotation.path());
			
			String path = myAnnotation.path();
			int first = path.indexOf('/');
			path = path.substring(0,first);
			
			path = path.substring(0, first);
			path= "/" + path;
			
			
			
			return myAnnotation.requestType() + ":" + myAnnotation.path();
		}

		return null;

	}

	//
	public void loadJarAnnotation(String pathToJar) throws IOException, ClassNotFoundException {
		JarFile jarFile = new JarFile(pathToJar);
		Enumeration<JarEntry> en = jarFile.entries();

		URL[] urls = { new URL("jar:file:" + pathToJar + "!/") };
		URLClassLoader cl = URLClassLoader.newInstance(urls);

		while (en.hasMoreElements()) {
			JarEntry je = en.nextElement();
			if (je.isDirectory() || !je.getName().endsWith(".class")) {
				continue;
			}

			String className = je.getName().substring(0, je.getName().length() - 6);
			className = className.replace('/', '.');
			Class c = cl.loadClass(className);
			Object object = null;
			try {
				object = c.newInstance();
				Server.accessLogger.info("new instance works");
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Found class: " + c.getName());
			for (Method m : c.getMethods()) {
				String key = parseAnnotations(m);
				if (key != null) {
					this.requestToMethod.put(parseAnnotations(m), m);
					Server.accessLogger
							.info("Added " + parseAnnotations(m) + "to requestToMethod for method: " + m.getName());

					if (object != null) {
						this.methodToObject.put(m, object);
						Server.accessLogger
								.info("Added " + parseAnnotations(m) + "to requestToMethod for method: " + m.getName());
					} else {
						Server.errorLogger.error("Object is null");
					}

				}
			}
			System.out.println("");
		}

		// Manifest jarFest = jarFile.getManifest();
		// Attributes atrb = jarFest.getMainAttributes();
		// for (Entry entry : atrb.entrySet()) {
		// System.out.println(entry.getKey() + " : " + entry.getValue());
		// }
	}

	public void removeJarReferences(String pathToJar) throws IOException, ClassNotFoundException {
		JarFile jarFile = new JarFile(pathToJar);
		Enumeration<JarEntry> en = jarFile.entries();

		URL[] urls = { new URL("jar:file:" + pathToJar + "!/") };
		URLClassLoader cl = URLClassLoader.newInstance(urls);

		while (en.hasMoreElements()) {
			JarEntry je = en.nextElement();
			if (je.isDirectory() || !je.getName().endsWith(".class")) {
				continue;
			}

			String className = je.getName().substring(0, je.getName().length() - 6);
			className = className.replace('/', '.');
			Class c = cl.loadClass(className);
			Object object = null;
			try {
				object = c.newInstance();
				Server.accessLogger.info("new instance works");
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Found class: " + c.getName());
			for (Method m : c.getMethods()) {
				String key = parseAnnotations(m);
				if (key != null) {
					this.requestToMethod.remove(parseAnnotations(m));
					Server.accessLogger
							.info("Remove " + parseAnnotations(m) + "from requestToMethod for method: " + m.getName());

					if (object != null) {
						this.methodToObject.remove(m);
						Server.accessLogger.info(
								"Removed " + parseAnnotations(m) + "from requestToMethod for method: " + m.getName());
					} else {
						Server.errorLogger.error("Object is null");
					}

				}
			}
			System.out.println("");
		}

		// Manifest jarFest = jarFile.getManifest();
		// Attributes atrb = jarFest.getMainAttributes();
		// for (Entry entry : atrb.entrySet()) {
		// System.out.println(entry.getKey() + " : " + entry.getValue());
		// }
	}

	private void populateHandlerHash() {
		// read in all the annotations, map them to the methods in form GET:URI
		// -> Method
		try {
			File path = new File(this.pluginDirectory);
			// Server.accessLogger.info("Found jars folder");
			if (path.exists()) {
				File[] files = path.listFiles();
				for (int i = 0; i < files.length; i++) {
					int extensionNumber = files[i].getName().indexOf('.');
					String extension = files[i].getName().substring(extensionNumber);
					if (files[i].isFile() && extension.equalsIgnoreCase(".jar")) { // Only
																					// takes
																					// branch
																					// on
																					// jar
																					// files
						Server.accessLogger.info("Found a jar");
						// Called for each jar in the jars folder.
						loadJarAnnotation(this.pluginDirectory + "/" + files[i].getName());
					}
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Map<Method, Object> getObjectMap() {
		return this.methodToObject;
	}

	public void removeMethod(Method m) {
		this.methodToObject.remove(m);
	}

}
