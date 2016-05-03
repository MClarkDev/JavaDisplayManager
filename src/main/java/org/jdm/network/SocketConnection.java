package org.jdm.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * 
 * @author Matthew Clark
 *
 */
public class SocketConnection {

	/**
	 * 
	 */
	private final Socket socket;
	private SocketListener socketListener;

	private String address;
	private String connectionAddress;

	private boolean keepAlive = false;

	/**
	 * 
	 */
	private PrintWriter printWriter;
	private OutputStream outputStream;

	/**
	 * 
	 * @param socket
	 * @param onDataReceivedListener
	 */
	public SocketConnection(final Socket socket, final SocketListener onDataReceivedListener, boolean keepAlive) {

		this.socket = socket;
		this.socketListener = onDataReceivedListener;

		this.address = socket.getInetAddress().getHostAddress();
		this.connectionAddress = address + ":" + socket.getPort();

		this.keepAlive = keepAlive;

		try {

			outputStream = socket.getOutputStream();
			printWriter = new PrintWriter(outputStream, true);

			// Start a thread listening for incoming data
			Thread socketThread = new SocketThread(socket, socketListener, keepAlive);
			socketThread.start();

			if (onDataReceivedListener != null) {

				onDataReceivedListener.onConnect(this);
			}
		} catch (Exception e) {

			disconnect("non-connect");
		}
	}

	/**
	 * 
	 */
	public void disconnect(String reason) {

		try {

			if (printWriter != null) {

				if (!reason.equals("graceful") && !reason.equals("done")) {

					printWriter.print("disconnect: " + reason);
				}

				printWriter.close();
				printWriter = null;
			}
		} catch (Exception e) {
		}

		try {

			if (socket != null) {

				socket.close();
			}
		} catch (Exception e) {
		}

		if (socketListener != null) {
			socketListener.onDisconnect(this, reason);
		}
	}

	public String getAddress() {

		return address;
	}

	/**
	 * 
	 * @return
	 */
	public String getConnectionAddress() {

		return connectionAddress;
	}

	public class SocketThread extends Thread {
		private Socket socket;
		private SocketListener callback;
		private boolean keepAlive = false;

		public SocketThread(Socket socket, SocketListener callback, boolean keepAlive) {

			this.socket = socket;
			this.callback = callback;
			this.keepAlive = keepAlive;
		}

		public void run() {

			setName("Connection to: " + socket.getInetAddress().toString() + ":" + socket.getPort() + " -> "
					+ socket.getLocalPort());

			String inputLine;
			BufferedReader bufferedReader = null;
			InputStreamReader streamReader = null;

			try {
				// setup the input stream
				streamReader = new InputStreamReader(socket.getInputStream());
				bufferedReader = new BufferedReader(streamReader);

				ArrayList<String> clientRequest = new ArrayList<String>();

				// block waiting for data from client
				while ((inputLine = bufferedReader.readLine()) != null) {

					// check for contents
					if (inputLine.length() > 0) {

						// add the line to the request
						clientRequest.add(inputLine);

					} else {

						// start processing on empty line
						if (callback != null) {

							String[] request = clientRequest.toArray(new String[clientRequest.size()]);

							// Send to the callback
							Object response = callback.onDataReceived(SocketConnection.this, request);

							// Print the response
							if (response != null) {

								if (response instanceof String) {

									// if single string
									printWriter.println((String) response);

								} else if (response instanceof String[]) {

									// if string array
									for (String line : (String[]) response) {

										printWriter.println(line);
									}
								} else if (response instanceof byte[]) {

									// if byte array
									outputStream.write((byte[]) response);
									outputStream.flush();
								} else if (response instanceof byte[][]) {

									// if array of byte arrays
									for (byte[] bytes : (byte[][]) response) {

										outputStream.write((byte[]) bytes);
										outputStream.write('\n');
									}
								}
								outputStream.flush();
							}

							// Close if not keepalive
							if (!keepAlive) {

								disconnect("done");
								return;
							}

							// Clear the request
							clientRequest.clear();
						}
					}
				}

				disconnect("graceful");
			} catch (Exception e) {

				disconnect("broken pipe");
			} finally {

				try {
					if (bufferedReader != null) {

						bufferedReader.close();
						bufferedReader = null;
					}
				} catch (Exception e) {
				}

				try {
					if (bufferedReader != null) {

						streamReader.close();
						streamReader = null;
					}
				} catch (Exception e) {
				}
			}
			return;
		}
	}

}
