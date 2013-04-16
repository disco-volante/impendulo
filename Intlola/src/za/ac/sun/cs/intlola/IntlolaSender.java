package za.ac.sun.cs.intlola;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import za.ac.sun.cs.intlola.file.IntlolaFile;

import com.google.gson.JsonObject;

public class IntlolaSender {

	protected final SendMode mode;

	private String uname;

	private final String project;

	private final String address;
	private static final String OK = "ok";
	private static final String EOF = "eof";
	private static final String SEND = "send";
	private static final String LOGIN = "begin";
	private static final String LOGOUT = "end";
	private static final String REQ = "req";
	private static final String UNAME = "uname";
	private static final String PWORD = "pword";
	private static final String PROJECT = "project";
	private static final String MODE = "mode";

	private final int port;
	private OutputStream snd = null;
	private Socket sock = null;
	private InputStream rcv = null;
	private boolean loggedIn;

	public IntlolaSender(final String uname, final String project,
			final SendMode mode, final String address, final int port) {
		this.uname = uname;
		this.project = project;
		this.mode = mode;
		this.address = address;
		this.port = port;
		loggedIn = false;
	}

	public String getProject() {
		return project;
	}

	public String getUsername() {
		return uname;
	}

	public boolean loggedIn() {
		return loggedIn;
	}

	public void login(final String username, final String password) {
		if (username != null && !uname.equals(username)) {
			uname = username;
		}
		final byte[] buffer = new byte[1024];
		try {
			final JsonObject params = new JsonObject();
			params.addProperty(REQ, LOGIN);
			params.addProperty(UNAME, uname);
			params.addProperty(PWORD, password);
			params.addProperty(PROJECT, project);
			params.addProperty(MODE, mode.toString());
			snd.write(params.toString().getBytes());
			snd.flush();
			rcv.read(buffer);
			final String received = new String(buffer);
			if (received.startsWith(OK)) {
				loggedIn = true;
			} else {
				System.out.println(received);
			}
		} catch (final IOException e) {
			e.printStackTrace();
			Intlola.log(e);
		}
	}

	public void logout() {
		try {
			final JsonObject params = new JsonObject();
			params.addProperty(REQ, LOGOUT);
			snd.write(params.toString().getBytes());
			snd.flush();
			final byte[] buffer = new byte[1024];
			rcv.read(buffer);
			final String received = new String(buffer);
			if (!received.startsWith(OK)) {
				System.out.println(received);
			}
		} catch (final IOException e) {
			e.printStackTrace();
			Intlola.log(e);
		} finally {
			try {
				closeConnection();
			} catch (final IOException e) {
				e.printStackTrace();
				Intlola.log(e);
			}

		}
	}

	public boolean openConnection() {
		boolean ret = true;
		try {
			sock = new Socket(address, port);
			snd = sock.getOutputStream();
			rcv = sock.getInputStream();
		} catch (final IOException e) {
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}

	public void sendFile(final IntlolaFile file) {
		final byte[] readBuffer = new byte[2048];
		final byte[] writeBuffer = new byte[2048];
		FileInputStream fis = null;
		try {
			JsonObject fjson = file.toJSON();
			fjson.addProperty(REQ, SEND);
			snd.write(fjson.toString().getBytes());
			snd.flush();
			rcv.read(readBuffer);
			String received = new String(readBuffer);
			if (received.startsWith(OK)) {
				if (file.hasContents()) {
					int count;
					fis = new FileInputStream(file.getPath());
					while ((count = fis.read(writeBuffer)) >= 0) {
						snd.write(writeBuffer, 0, count);
					}
				}
				snd.write(EOF.getBytes());
				snd.flush();
				rcv.read(readBuffer);
				received = new String(readBuffer);
				if (!received.startsWith(OK)) {
					System.out.println(received);
				}
			}

		} catch (final IOException e) {
			e.printStackTrace();
			Intlola.log(e);
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (final IOException e) {
				e.printStackTrace();
				Intlola.log(e);
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
