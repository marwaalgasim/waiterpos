package ua.cn.yet.waiter.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/**
 * Class that checks if program instance is running. If not, then socket is
 * created, preventing another instance from opening socket.
 * 
 * Idea is taken from
 * http://www.rbgrn.net/content/43-java-single-application-instance
 * 
 * @author Yuriy Tkach
 */
public class InstanceLock {

	/** Randomly chosen, but static, high socket number */
	public static final int SINGLE_INSTANCE_NETWORK_SOCKET = 44331;

	/** Socket that is opened */
	private static ServerSocket socket = null;

	/**
	 * Creating instance lock by opening socket
	 * 
	 * @return True if successful or lock creation error
	 */
	public static boolean registerInstanceLock() {
		// returnValueOnError should be true if lenient (allows app to run on
		// network error) or false if strict.
		boolean returnValueOnError = true;
		// try to open network socket
		// if success, listen to socket for new instance message, return true
		// if unable to open, connect to existing and send new instance message,
		// return false
		try {
			socket = new ServerSocket(SINGLE_INSTANCE_NETWORK_SOCKET, 10,
					InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			return returnValueOnError;
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * Releasing lock by closing socket
	 */
	public void releaseInstanceLock() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				// Doing nothing
			}
		}
	}

}
