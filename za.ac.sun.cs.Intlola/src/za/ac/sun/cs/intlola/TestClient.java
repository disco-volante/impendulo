package za.ac.sun.cs.intlola;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class TestClient {
	static class SendThread implements Runnable {

		@Override
		public void run() {
			String fname = "intlola.zip";
			byte[] buffer = new byte[1024];
			OutputStream snd = null;
			FileInputStream fis = null;
			Socket sock = null;
			InputStream rcv = null;
			try {
				sock = new Socket("localhost", 9998);
				snd = sock.getOutputStream();
				snd.write(("CONNECT:" + fname).getBytes());
				rcv = sock.getInputStream();
				rcv.read(buffer);
				if(!new String(buffer).startsWith("ACCEPT")){
					rcv.close();
					snd.close();
					sock.close();
					return;
				}
				int count;
				fis = new FileInputStream(fname);		
				while ((count = fis.read(buffer)) >= 0) {
					snd.write(buffer, 0, count);
				}
				snd.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					rcv.close();
					fis.close();
					snd.close();
					sock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

		
	}

	public static void main(String argv[]) {
		ArrayList<Thread> threads = new ArrayList<Thread>();
		for (int i = 0; i < 1000; i++) {
			threads.add(new Thread(new SendThread()));
		}
		for (Thread th : threads) {
			th.start();
		}
	}
}
