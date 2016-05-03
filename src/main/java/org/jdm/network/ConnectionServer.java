package org.jdm.network;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Logger;

public class ConnectionServer {
	/**
	 * 
	 */
	protected ServerSocket serverSocket;
	private HashMap<String, SocketConnection> connectionMap;
	private ServerThread serverThread;
	private SocketListener socketListener;

	public ConnectionServer(int port, SocketListener socketListener) {

		this.socketListener = socketListener;

		System.out.println("Binding to port [ " + port + " ].");

		try {

			serverSocket = new ServerSocket(port);

			serverThread = new ServerThread(serverSocket);
			serverThread.setName("ServerSocket [ " + port + " ]");

		} catch (Exception e) {

			System.err.println("Failed to bind to port [ " + port + " ].");
			throw new RuntimeException("Failed to bind to port.");
		}

		connectionMap = new HashMap<String, SocketConnection>();
	}

	public void setKeepAlive(boolean keepAlive) {

		serverThread.setKeepAlive(keepAlive);
	}

	public void setAcceptConnections(boolean accept) {

		serverThread.setAcceptConnections(accept);
	}

	/**
	 * 
	 */
	private class ServerThread extends Thread {

		private ServerSocket serverSocket;
		private boolean keepAlive = false;
		private boolean accept = true;

		public ServerThread(ServerSocket serverSocket) {

			this.serverSocket = serverSocket;
		}

		public void setAcceptConnections(boolean accept) {

			this.accept = accept;
		}

		public void setKeepAlive(boolean keepAlive) {

			this.keepAlive = keepAlive;
		}

		public void stopServer() {

			accept = false;
			for (String host : connectionMap.keySet()) {

				connectionMap.get(host).disconnect("server stopping");
			}
			interrupt();
		}

		public void run() {

			while (accept && !interrupted()) {

				try {

					Socket socket;
					while ((socket = serverSocket.accept()) != null) {

						SocketConnection socketConnection = new SocketConnection(socket, socketListener, keepAlive);

						connectionMap.put(socketConnection.getConnectionAddress(), socketConnection);
					}
				} catch (Exception e) {

					System.err.println("Get and error [" + e.getMessage() + " ]");
				}
			}
		}
	}

	public void stop() {

		// Stop the server thread
		serverThread.stopServer();

		try {

			serverSocket.close();
			serverSocket = null;
		} catch (Exception e) {
		}

		try {

			serverThread.join();
		} catch (Exception e) {
		}
	}

	public void start() {

		serverThread.start();
	}

	protected SocketConnection[] getOpenConnections() {

		return connectionMap.values().toArray(new SocketConnection[connectionMap.size()]);
	}

	protected int getOpenConnectionCount() {

		return connectionMap.size();
	}
}
