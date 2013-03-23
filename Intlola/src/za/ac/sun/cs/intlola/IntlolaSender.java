package za.ac.sun.cs.intlola;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import com.google.gson.JsonObject;

public class IntlolaSender {

	private SendMode mode;

	private String uname, passwd, project, token, address;
	private int port;
	private OutputStream snd = null;
	private Socket sock = null;
	private InputStream rcv = null;

	public IntlolaSender() {
	}

	public IntlolaSender(final String uname, final String passwd,
			final String project, final SendMode mode, final String address,
			final int port) {
		this.uname = uname;
		this.passwd = passwd;
		this.project = project;
		this.mode = mode;
		this.address = address;
		this.port = port;
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

	public String getProject() {
		return project;
	}

	private void login() {
		final byte[] buffer = new byte[1024];
		int read = 0;
		try {
			openConnection();
			final JsonObject params = new JsonObject();
			params.addProperty("TYPE", "LOGIN");
			params.addProperty("USERNAME", uname);
			params.addProperty("PASSWORD", passwd);
			params.addProperty("PROJECT", getProject());
			params.addProperty("MODE", mode.toString());
			snd.write(params.toString().getBytes());
			read = rcv.read(buffer);
			snd.flush();
		} catch (final IOException e) {
			Intlola.log(e);
		} finally {
			try {
				closeConnection();
			} catch (final IOException e) {
				Intlola.log(e);
			}

		}
		if (read > 0) {
			final String received = new String(Arrays.copyOfRange(buffer, 0,
					read));
			if (received.startsWith("TOKEN:")) {
				token = received.substring(received.indexOf(':') + 1);
			}
		}
	}

	private void logout() {
		try {
			openConnection();
			final JsonObject params = new JsonObject();
			params.addProperty("TYPE", "LOGOUT");
			params.addProperty("TOKEN", token);
			snd.write(params.toString().getBytes());
			snd.flush();
		} catch (final IOException e) {
			Intlola.log(e);
		} finally {
			try {
				closeConnection();
			} catch (final IOException e) {
				Intlola.log(e);
			}

		}
	}

	private void openConnection() throws IOException {
		sock = new Socket(address, port);
		snd = sock.getOutputStream();
		rcv = sock.getInputStream();
	}

	public void send(final SendMode check, final String filename) {
		if (token == null) {
			login();
		}
		if (token != null) {
			if (check.equals(SendMode.ONSTOP) && mode.equals(SendMode.ONSAVE)) {
				zipDir();
				logout();
			} else if (check.equals(mode)) {
				sendFile(filename);
				if (mode.equals(SendMode.ONSTOP)) {
					logout();
				}
			}
		}

	}

	private void sendFile(final String fileName) {
		final byte[] buffer = new byte[1024];
		FileInputStream fis = null;
		try {
			openConnection();
			String sendName;
			if (fileName.contains(File.separator)) {
				sendName = fileName.substring(fileName
						.lastIndexOf(File.separator) + 1);
			} else {
				sendName = fileName;
			}
			final JsonObject params = new JsonObject();
			params.addProperty("TYPE", "SEND");
			params.addProperty("TOKEN", token);
			params.addProperty("FILENAME", sendName);
			snd.write(params.toString().getBytes());
			rcv.read(buffer);
			if (new String(buffer).startsWith("ACCEPT")) {
				int count;
				fis = new FileInputStream(fileName);
				while ((count = fis.read(buffer)) >= 0) {
					snd.write(buffer, 0, count);
				}
				snd.flush();
			}
		} catch (final IOException e) {
			Intlola.log(e);
		} finally {
			try {
				closeConnection();
				if (fis != null) {
					fis.close();
				}
			} catch (final IOException e) {
				Intlola.log(e);
			}

		}

	}

	private void zipDir() {
		final byte[] buffer = new byte[1024];
		try {
			openConnection();
			final JsonObject params = new JsonObject();
			params.addProperty("TYPE", "ZIP");
			params.addProperty("TOKEN", token);
			snd.write(params.toString().getBytes());
			rcv.read(buffer);
			snd.flush();
		} catch (final IOException e) {
			Intlola.log(e);
		} finally {
			try {
				closeConnection();
			} catch (final IOException e) {
				Intlola.log(e);
			}

		}
	}

}
