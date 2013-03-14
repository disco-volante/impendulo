package za.ac.sun.cs.intlola;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import za.ac.sun.cs.intlola.preferences.PreferenceConstants;

import com.google.gson.JsonObject;

public class IntlolaSender {

	protected SendMode mode;

	protected String uname = PreferenceConstants.DEFAULT;

	protected String passwd = PreferenceConstants.PASSWORD;

	protected String project = "NONE";

	private String token;

	private OutputStream snd = null;
	private Socket sock = null;
	private InputStream rcv = null;

	protected String address;

	protected int port;

	public IntlolaSender(String uname, String passwd, String project,
			SendMode mode, String address, int port) {
		this.uname = uname;
		this.passwd = passwd;
		this.project = project;
		this.mode = mode;
		this.address = address;
		this.port = port;
	}

	public IntlolaSender() {
	}

	private void login() {
		byte[] buffer = new byte[1024];
		int read = 0;
		try {
			openConnection();
			JsonObject params = new JsonObject();
			params.addProperty("TYPE", "LOGIN");
			params.addProperty("USERNAME", uname);
			params.addProperty("PASSWORD", passwd);
			params.addProperty("PROJECT", project);
			params.addProperty("MODE", mode.toString());
			snd.write(params.toString().getBytes());
			read = rcv.read(buffer);
			snd.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				closeConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		if (read > 0) {
			String received = new String(Arrays.copyOfRange(buffer, 0, read));
			if (received.startsWith("TOKEN:")) {
				token = received.substring(received.indexOf(':') + 1);
			}
		}
	}

	private void closeConnection() throws IOException {
		if (snd != null)
			snd.close();
		if (rcv != null)
			rcv.close();
		if (sock != null)
			sock.close();
	}

	private void openConnection() throws IOException {
		sock = new Socket(address, port);
		snd = sock.getOutputStream();
		rcv = sock.getInputStream();
	}

	public void send(SendMode check, String filename) {
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

	private void sendFile(String fileName) {
		byte[] buffer = new byte[1024];
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
			JsonObject params = new JsonObject();
			params.addProperty("TYPE", "SEND");
			params.addProperty("TOKEN", token);
			params.addProperty("FILENAME", sendName);
			snd.write(params.toString().getBytes());
			rcv.read(buffer);
			if (!new String(buffer).startsWith("ACCEPT")) {
				closeConnection();
				return;
			}
			int count;
			fis = new FileInputStream(fileName);
			while ((count = fis.read(buffer)) >= 0) {
				snd.write(buffer, 0, count);
			}
			snd.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				closeConnection();
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	private void zipDir() {
		byte[] buffer = new byte[1024];
		try {
			openConnection();
			JsonObject params = new JsonObject();
			params.addProperty("TYPE", "ZIP");
			params.addProperty("TOKEN", token);
			snd.write(params.toString().getBytes());
			rcv.read(buffer);
			snd.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				closeConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private void logout() {
		try {
			openConnection();
			JsonObject params = new JsonObject();
			params.addProperty("TYPE", "LOGOUT");
			params.addProperty("TOKEN", token);
			snd.write(params.toString().getBytes());
			snd.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				closeConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
