package org.jdm.network;

/**
 * 
 * @author Matthew Clark
 *
 */
public interface SocketListener {

	/**
	 * Called when a client connection has been established
	 * 
	 * @param connection
	 */
	Object onConnect(SocketConnection connection);

	/**
	 * Called when the client has made a request, terminated by 2x newline
	 * 
	 * @param connection
	 * @param requestString
	 * @return
	 */
	Object onDataReceived(SocketConnection connection, String[] requestString);

	/**
	 * Called when a client connection has been terminated
	 * 
	 * @param connection
	 * @param reason
	 */
	void onDisconnect(SocketConnection connection, String reason);
}