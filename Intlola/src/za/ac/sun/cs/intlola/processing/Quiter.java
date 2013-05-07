package za.ac.sun.cs.intlola.processing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import za.ac.sun.cs.intlola.Intlola;
import za.ac.sun.cs.intlola.file.Const;

import com.google.gson.JsonObject;

public class Quiter implements Runnable {
	private OutputStream snd;
	private InputStream rcv;
	private Socket sock;

	Quiter(Socket sock, OutputStream snd, InputStream rcv) {
		this.sock = sock;
		this.snd = snd;
		this.rcv = rcv;
	}

	@Override
	public void run() {
		try {
			final JsonObject params = new JsonObject();
			params.addProperty(Const.REQ, Const.LOGOUT);
			snd.write(params.toString().getBytes());
			snd.flush();
			final byte[] buffer = new byte[1024];
			rcv.read(buffer);
			final String received = new String(buffer);
			if (!received.startsWith(Const.OK)) {
				Intlola.log(null, received);
			}
		} catch (final IOException e) {
			Intlola.log(e);
		} finally {
			try {
				closeConnection();
			} catch (final IOException e) {
				Intlola.log(e, "Logout error");
			}
		}
	}

	private void closeConnection() throws IOException {
		if (snd != null) {
			snd.close();
		}
		if (rcv != null) {
			rcv.close();
		}
		if (sock != null) {
			sock.close();
		}
	}
}
