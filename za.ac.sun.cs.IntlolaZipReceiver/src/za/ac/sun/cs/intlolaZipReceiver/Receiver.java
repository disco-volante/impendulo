package za.ac.sun.cs.intlolaZipReceiver;

import java.io.IOException;
import java.net.ServerSocket;

public class Receiver {

	private static ServerSocket serverSocket = null;

	public static boolean listening = true;

	private static void openSocket() {
        try {
            serverSocket = new ServerSocket(4444);
        } catch (IOException e) {
			System.err.println("IO error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void closeSocket() {
    	try {
	    	serverSocket.close();
		} catch (IOException e) {
			System.err.println("IO error: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
    }

	public static void main(String[] args) {
		openSocket();
        while (listening) {
        	try {
        		new ReceiverThread(serverSocket.accept()).start();
        	}
        	catch (IOException e) {
        		e.printStackTrace();
        	}
        }
		closeSocket();
	}

}
