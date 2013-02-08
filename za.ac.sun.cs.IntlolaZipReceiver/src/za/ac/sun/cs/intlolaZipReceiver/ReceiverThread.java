package za.ac.sun.cs.intlolaZipReceiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReceiverThread extends Thread {

	private Socket socket = null;

	public ReceiverThread(Socket socket) {
		super("ReceiverThread");
		this.socket = socket;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			while (true) {
				String message = in.readLine();
				if (message.equals("quit")) {
					Receiver.listening = false;
					break;
				}
				@SuppressWarnings("unused")
				long fileSize = Long.valueOf(in.readLine());
			}
			in.close();
			socket.close();
		} catch (IOException e) {
			System.err.println("IO error: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

}
