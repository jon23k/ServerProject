/*
 * HttpResponse.java
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
 
package protocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a response object for HTTP.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class HttpResponse {
	private String version;
	private int status;
	private String phrase;
	private Map<String, String> header;
	private File file;

	
	/**
	 * Constructs a HttpResponse object using supplied parameter
	 * 
	 * @param version The http version.
	 * @param status The response status.
	 * @param phrase The response status phrase.
	 * @param header The header field map.
	 * @param file The file to be sent.
	 */
	public HttpResponse(HttpResponseBuilder builder) {
		this.version = builder.version;
		this.status = builder.status;
		this.phrase = builder.phrase;
		this.header = builder.header;
		this.file = builder.file;
	}

	/**
	 * Gets the version of the HTTP.
	 * 
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Gets the status code of the response object.
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Gets the status phrase of the response object.
	 * 
	 * @return the phrase
	 */
	public String getPhrase() {
		return phrase;
	}
	
	/**
	 * The file to be sent.
	 * 
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Returns the header fields associated with the response object.
	 * @return the header
	 */
	public Map<String, String> getHeader() {
		// Lets return the unmodifable view of the header map
		return Collections.unmodifiableMap(header);
	}

	/**
	 * Maps a key to value in the header map.
	 * @param key A key, e.g. "Host"
	 * @param value A value, e.g. "www.rose-hulman.edu"
	 */
	public void put(String key, String value) {
		this.header.put(key, value);
	}
	
	/**
	 * Writes the data of the http response object to the output stream.
	 * 
	 * @param outStream The output stream
	 * @throws Exception
	 */
	public void write(OutputStream outStream) throws Exception {
		BufferedOutputStream out = new BufferedOutputStream(outStream, Protocol.CHUNK_LENGTH);

		// First status line
		String line = this.version + Protocol.SPACE + this.status + Protocol.SPACE + this.phrase + Protocol.CRLF;
		out.write(line.getBytes());
		
		// Write header fields if there is something to write in header field
		if(header != null && !header.isEmpty()) {
			for(Map.Entry<String, String> entry : header.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				
				// Write each header field line
				line = key + Protocol.SEPERATOR + Protocol.SPACE + value + Protocol.CRLF;
				out.write(line.getBytes());
			}
		}

		// Write a blank line
		out.write(Protocol.CRLF.getBytes());

		// We are reading a file
		if(this.getStatus() == Protocol.OK_CODE && file != null) {
			// Process text documents
			FileInputStream fileInStream = new FileInputStream(file);
			BufferedInputStream inStream = new BufferedInputStream(fileInStream, Protocol.CHUNK_LENGTH);
			
			byte[] buffer = new byte[Protocol.CHUNK_LENGTH];
			int bytesRead = 0;
			// While there is some bytes to read from file, read each chunk and send to the socket out stream
			while((bytesRead = inStream.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			// Close the file input stream, we are done reading
			inStream.close();
		}
		
		// Flush the data so that outStream sends everything through the socket 
		out.flush();
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("----------------------------------\n");
		buffer.append(this.version);
		buffer.append(Protocol.SPACE);
		buffer.append(this.status);
		buffer.append(Protocol.SPACE);
		buffer.append(this.phrase);
		buffer.append(Protocol.LF);
		
		for(Map.Entry<String, String> entry : this.header.entrySet()) {
			buffer.append(entry.getKey());
			buffer.append(Protocol.SEPERATOR);
			buffer.append(Protocol.SPACE);
			buffer.append(entry.getValue());
			buffer.append(Protocol.LF);
		}
		
		buffer.append(Protocol.LF);
		if(file != null) {
			buffer.append("Data: ");
			buffer.append(this.file.getAbsolutePath());
		}
		buffer.append("\n----------------------------------\n");
		return buffer.toString();
	}
	
	public static class HttpResponseBuilder {

		private File file;
		private Map<String, String> header;
		private String phrase;
		private int status;
		private String version;
		private String connection;
		private String fileLength;
		private String fileName;
		private String fileType;
		
		public HttpResponseBuilder (String connection) {
			this.connection = connection;
		}
		
		public HttpResponseBuilder status(int status) {
			this.status = status;
			return this;
		}
		
		public HttpResponseBuilder phrase(String phrase) {
			this.phrase = phrase;
			return this;
		}
		
		public HttpResponseBuilder file(File file) {
			this.file = file;
			return this;
		}
		
		public HttpResponseBuilder version(String version) {
			this.version = version;
			return this;
		}
		
		public HttpResponseBuilder contentLength(String length) {
			this.fileLength = length;
			return this;
		}
		
		public HttpResponseBuilder fileName(String name) {
			this.fileName = name;
			return this;
		}

		public HttpResponseBuilder fileType(String type) {
			this.fileType = type;
			return this;
		}
		
		public void validate(HttpResponse response) {
			response.put("Access-Control-Allow-Origin", "*");
	        response.put("Access-Control-Allow-Methods", "HEAD, GET, DELETE, POST, PUT");
			// Lets add the connection to the header
			response.put(Protocol.CONNECTION, this.connection);
			
			// Lets add current date
			Date date = Calendar.getInstance().getTime();
			response.put(Protocol.DATE, date.toString());
			
			// Lets add server info
			response.put(Protocol.Server, Protocol.getServerInfo());

			// Lets add extra header with provider info
			response.put(Protocol.PROVIDER, Protocol.AUTHOR);
			
			// for a HEAD request
			if (this.fileName != null 
					&& this.fileLength != null 
					&& this.fileType != null) {
				response.put(Protocol.FILE_TYPE, fileType);
				response.put(Protocol.FILE_NAME, fileName);
				response.put(Protocol.FILE_SIZE, fileLength);
			}
			
			if (file != null && this.status == 200) {
				
				// Lets add last modified date for the file
				long timeSinceEpoch = file.lastModified();
				Date modifiedTime = new Date(timeSinceEpoch);
				response.put(Protocol.LAST_MODIFIED, modifiedTime.toString());
				
				// Lets get content length in bytes
				long length = file.length();
				response.put(Protocol.CONTENT_LENGTH, length + "");
				
				// Lets get MIME type for the file
				FileNameMap fileNameMap = URLConnection.getFileNameMap();
				String mime = fileNameMap.getContentTypeFor(file.getName());
				// The fileNameMap cannot find mime type for all of the documents, e.g. doc, odt, etc.
				// So we will not add this field if we cannot figure out what a mime type is for the file.
				// Let browser do this job by itself.
				if(mime != null) { 
					response.put(Protocol.CONTENT_TYPE, mime);
				}
			}
		}
		
		public HttpResponse build() {
			this.header = new HashMap<String, String>();
			HttpResponse response = new HttpResponse(this);
			validate(response);
			return response;
		}

	}
	
}
