package za.ac.sun.cs.intlola.processing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import za.ac.sun.cs.intlola.file.Const;

import com.google.gson.JsonObject;

public class SessionEnder implements Runnable {
	private final InputStream rcv;
	private final OutputStream snd;
	private final Socket sock;

	SessionEnder(final Socket sock, final OutputStream snd,
			final InputStream rcv) {
		this.sock = sock;
		this.snd = snd;
		this.rcv = rcv;
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

	@Override
	public void run() {
		try {
			final JsonObject params = new JsonObject();
			params.addProperty(Const.REQ, Const.LOGOUT);
			IOUtils.writeJson(snd, params);
			final String received = IOUtils.read(rcv);
			if (!received.startsWith(Const.OK)) {
				System.err.println(received);
			}
		} catch (final IOException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				closeConnection();
			} catch (final IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}
}
