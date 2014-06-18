/*******************************************************************************
 * Copyright (c) 2003-2014 Albert Pérez and RoboRumble contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
package net.sf.robocode.roborumble.netengine;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;


/**
 * Utility class for downloading files from the net and copying files.
 * 
 * @author Flemming N. Larsen (original)
 */
public class FileTransfer {

	private final static int DEFAULT_CONNECTION_TIMEOUT = 10000; // 10 seconds
	private final static int DEFAULT_READ_TIMEOUT = 10000; // 10 seconds
	private final static int DEFAULT_SESSION_TIMEOUT = 10000; // 10 seconds

	private static int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
	private static int readTimeout = DEFAULT_READ_TIMEOUT;
	private static int sessionTimeout = DEFAULT_SESSION_TIMEOUT;
	
	/**
	 * Represents the download status returned when downloading files.
	 */
	public enum DownloadStatus {
		OK, // The download was succesful
		COULD_NOT_CONNECT, // Connection problem
		FILE_NOT_FOUND, // The file to download was not found
	}


	/**
	 * Daemon worker thread containing a 'finish' flag for waiting and notifying when the thread has finished it's job.
	 */
	private static class WorkerThread extends Thread {

		final Object monitor = new Object();

		volatile boolean isFinished;

		public WorkerThread(String name) {
			super(name);
			setDaemon(true);
		}

		void notifyFinish() {
			// Notify that this thread is finish
			synchronized (monitor) {
				isFinished = true;
				monitor.notifyAll();
			}
		}
	}

	/*
	 * Returns a session id for keeping a session on a HTTP site.
	 *
	 * @param url is the url of the HTTP site.
	 *
	 * @return a session id for keeping a session on a HTTP site or null if no session is available.
	 */
	public static String getSessionId(String url) {
		HttpURLConnection conn = null;

		try {
			// Open connection
			conn = FileTransfer.connectToHttpInputConnection(new URL(url));
			if (conn == null) {
				throw new IOException("Could not open connection to '" + url + "'");
			}

			// Get a session id if available
			final GetSessionIdThread sessionIdThread = new GetSessionIdThread(conn);

			sessionIdThread.start();

			// Wait for the session id
			synchronized (sessionIdThread.monitor) {
				while (!sessionIdThread.isFinished) {
					try {
						sessionIdThread.monitor.wait(sessionTimeout);
						sessionIdThread.interrupt();
					} catch (InterruptedException e) {
						// Immediately reasserts the exception by interrupting the caller thread itself
						Thread.currentThread().interrupt();

						return null;
					}
				}
			}

			// Return the session id
			return sessionIdThread.sessionId;

		} catch (final IOException e) {
			return null;
		} finally {
			// Make sure the connection is disconnected.
			// This will cause threads using the connection to throw an exception
			// and thereby terminate if they were hanging.
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	/**
	 * Worker thread used for getting the session id of an already open HTTP connection.
	 */
	private final static class GetSessionIdThread extends WorkerThread {

		// The resulting session id to read out
		String sessionId;

		final HttpURLConnection conn;

		GetSessionIdThread(HttpURLConnection conn) {
			super("FileTransfer: Get session ID");
			this.conn = conn;
		}

		@Override
		public void run() {
			try {
				// Get the cookie value
				final String cookieVal = conn.getHeaderField("Set-Cookie");

				// Extract the session id from the cookie value
				if (cookieVal != null) {
					sessionId = cookieVal.substring(0, cookieVal.indexOf(";"));
				}
			} catch (final Exception e) {
				sessionId = null;
			}
			// Notify that this thread is finish
			notifyFinish();
		}
	}

	/**
	 * Copies a file into another file.
	 *
	 * @param srcFile is the filename of the source file to copy.
	 * @param destFile is the filename of the destination file to copy the file into.
	 * @return true if the file was copied; false otherwise
	 */
	public static boolean copy(String srcFile, String destFile) {
		FileInputStream in = null;
		FileOutputStream out = null;

		try {
			if (srcFile.equals(destFile)) {
				throw new IOException("You cannot copy a file onto itself");
			}
			final byte[] buf = new byte[4096];

			in = new FileInputStream(srcFile);
			out = new FileOutputStream(destFile);

			while (in.available() > 0) {
				out.write(buf, 0, in.read(buf, 0, buf.length));
			}
		} catch (final IOException e) {
			return false;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	/**
	 * Opens and connects to a {@link java.net.HttpURLConnection} for input only, and where the connection timeout and
	 * read timeout are controlled by properties.
	 * 
	 * @param url is the URL to open a connection to.
	 * @return a HttpURLConnection intended for reading input only.
	 * @throws IOException if an I/O exception occurs.
	 */
	public static HttpURLConnection connectToHttpInputConnection(URL url) throws IOException {
		return connectToHttpInputConnection(url, null);
	}

	/**
	 * Opens and connects to a {@link java.net.HttpURLConnection} for input only, and where the connection timeout and
	 * read timeout are controlled by properties.
	 * 
	 * @param url is the URL to open a connection to.
	 * @param sessionId is a optional session id.
	 * @return a HttpURLConnection intended for reading input only.
	 * @throws IOException if an I/O exception occurs.
	 */
	public static HttpURLConnection connectToHttpInputConnection(URL url, String sessionId) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) openURLConnection(url, false); // not for output

		conn.setRequestMethod("GET");
		if (sessionId != null) {
			conn.setRequestProperty("Cookie", sessionId);
		}
		conn.connect();
		return conn;
	}

	/**
	 * Opens a {link {@link java.net.URLConnection} for output (and input) where the connection timeout and read timeout
	 * are controlled by properties.
	 *
	 * @param url is the URL to open.
	 * @return a URLConnection for output.
	 * @throws IOException if an I/O exception occurs.
	 */
	public static URLConnection openOutputURLConnection(URL url) throws IOException {
		return openURLConnection(url, true); // for output
	}

	/**
	 * Convenient method used for getting an input stream from an URLConnection.
	 * This method checks if a GZIPInputStream or InflaterInputStream should be used to wrap the input stream from the
	 * URLConnection depending on the content encoding.
	 * 
	 * @param conn is the URLConnection
	 * @return an input stream from the URLConnection, which can be a GZIPInputStream or InflaterInputStream.
	 * @throws IOException if an I/O exception occurs.
	 */
	public static InputStream getInputStream(URLConnection conn) throws IOException {
		// Get input stream
		InputStream in = conn.getInputStream();

		// Get the encoding returned by the server
		String encoding = conn.getContentEncoding();
		
		// Check if we need to use a gzip or inflater input stream depending on the content encoding  
		if ("gzip".equalsIgnoreCase(encoding)) {
			in = new GZIPInputStream(in);
		} else if ("deflate".equalsIgnoreCase(encoding)) {
			in = new InflaterInputStream(in);
		}
		return in;
	}

	/**
	 * Convenient method used for getting an output stream from an URLConnection.
	 * This method checks if a GZIPOutputStream or DeflaterOutputStream should be used to wrap the output stream from
	 * the URLConnection depending on the content encoding.
	 * 
	 * @param conn is the URLConnection
	 * @return an output stream from the URLConnection, which can be a GZIPOutputStream or DeflaterOutputStream.
	 * @throws IOException if an I/O exception occurs.
	 */
	public static OutputStream getOutputStream(URLConnection conn) throws IOException {
		// Get output stream
		OutputStream out = conn.getOutputStream();

		// // Get the encoding returned by the server
		// String encoding = conn.getContentEncoding();
		//
		// // Check if we need to use a gzip or inflater input stream depending on the content encoding  
		// if ("gzip".equalsIgnoreCase(encoding)) {
		// out = new GZIPOutputStream(out);
		// } else if ("deflate".equalsIgnoreCase(encoding)) {
		// out = new DeflaterOutputStream(out);
		// }
		return out;
	}

	/**
	 * Opens a {link {@link java.net.URLConnection} for input and optional output where the connection timeout and read
	 * timeout are controlled by properties.
	 *
	 * @param url is the URL to open.
	 * @param isOutput is a flag specifying if the opened connection is for output.
	 * @return a URLConnection.
	 * @throws IOException if an I/O exception occurs.
	 */
	public static URLConnection openURLConnection(URL url, boolean isOutput) throws IOException {
		URLConnection conn = url.openConnection();

		conn.setDoInput(true);
		conn.setDoOutput(isOutput);

		conn.setConnectTimeout(connectionTimeout);
		conn.setReadTimeout(readTimeout);

		if (!isOutput) {
			// Allow both GZip and Deflate (ZLib) encodings
			conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("User-Agent", "RoboRumble@Home - gzip, deflate");
		}
		return conn;
	}

}
