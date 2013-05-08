package za.ac.sun.cs.intlola.processing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

import com.google.gson.JsonObject;

import za.ac.sun.cs.intlola.IIntlolaCallback;
import za.ac.sun.cs.intlola.Intlola;
import za.ac.sun.cs.intlola.IntlolaError;
import za.ac.sun.cs.intlola.file.Const;

public class SessionCreator implements Callable<Object> {

	private OutputStream snd;
	private JsonObject params;
	private InputStream rcv;
	private Socket sock;
	private String address;
	private int port;
	private IIntlolaCallback<IntlolaError> callback;

	public SessionCreator(IIntlolaCallback<IntlolaError> callback, JsonObject params,
			String address, int port, Socket sock, OutputStream snd,
			InputStream rcv) {
		this.callback = callback;
		this.params = params;
		this.address = address;
		this.port = port;
		this.sock = sock;
		this.snd = snd;
		this.rcv = rcv;

	}

	@Override
	public Object call() throws Exception {
		IntlolaError err;
		if (!init()) {
			err = IntlolaError.CONN;
		} else {
			final byte[] buffer = new byte[1024];
			try {
				snd.write(params.toString().getBytes());
				snd.flush();
				rcv.read(buffer);
				final String received = new String(buffer);
				if (received.startsWith(Const.OK)) {
					err = IntlolaError.SUCCESS;
				} else {
					Intlola.log(null, received);
					err = IntlolaError.LOGIN;
				}
			} catch (final IOException e) {
				Intlola.log(e, "Login error");
				err = IntlolaError.LOGIN;
			}
		}
		callback.returnResult(err);
		System.out.println("returned err");
		return null;
	}

	private boolean init() {
		try {
			sock = new Socket(address, port);
			snd = sock.getOutputStream();
			rcv = sock.getInputStream();
			return true;
		} catch (final IOException e) {
			Intlola.log(e, "No server detected");
			return false;
		}
	}

}
