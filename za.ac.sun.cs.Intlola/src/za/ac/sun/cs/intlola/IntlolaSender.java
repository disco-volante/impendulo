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
	private static final String ADDRESS = "localhost";

	private static final int PORT = 9998;

	protected SendMode mode;

	protected String uname = PreferenceConstants.DEFAULT;

	protected String passwd = PreferenceConstants.PASSWORD;

	protected String project = "NONE";

	private String token;

	public IntlolaSender(String uname, String passwd, String project,
			SendMode mode) {
		this.uname = uname;
		this.passwd = passwd;
		this.project = project;
		this.mode = mode;
	}

	public IntlolaSender() {
	}

	private void login() {
		OutputStream snd = null;
		Socket sock = null;
		InputStream rcv = null;
		byte[] buffer = new byte[1024];
		int read = 0;
		try {
			sock = new Socket(ADDRESS, PORT);
			snd = sock.getOutputStream();
			JsonObject params = new JsonObject();
			params.addProperty("TYPE", "LOGIN");
			params.addProperty("USERNAME", uname);
			params.addProperty("PASSWORD", passwd);
			params.addProperty("PROJECT", project);
			snd.write(params.toString().getBytes());
			rcv = sock.getInputStream();
			read = rcv.read(buffer);
			snd.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				snd.close();
				sock.close();
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

	public void send(SendMode check, String filename, String sendName) {
		if (token == null) {
			login();
		}
		if (token != null) {
			if (check.equals(SendMode.ONSTOP) && mode.equals(SendMode.ONSAVE)) {
				zipDir(filename);
				logout();
			} else if (check.equals(mode)) {
				sendFile(filename, sendName);
				if(mode.equals(SendMode.ONSTOP)){
					logout();
				}
			}
		}

	}
	
	public void send(SendMode check, String filename) {
		send(check, filename, null);
	}

	private void sendFile(String filename, String sendName) {
		byte[] buffer = new byte[1024];
		OutputStream snd = null;
		FileInputStream fis = null;
		Socket sock = null;
		InputStream rcv = null;
		try {
			sock = new Socket(ADDRESS, PORT);
			snd = sock.getOutputStream();
			if (sendName == null) {
				sendName = filename.substring(filename
						.lastIndexOf(File.separator) + 1);
			}
			JsonObject params = new JsonObject();
			params.addProperty("TYPE", "SEND");
			params.addProperty("TOKEN", token);
			params.addProperty("FILENAME", sendName);
			snd.write(params.toString().getBytes());
			rcv = sock.getInputStream();
			rcv.read(buffer);
			if (!new String(buffer).startsWith("ACCEPT")) {
				rcv.close();
				snd.close();
				sock.close();
				return;
			}
			int count;
			fis = new FileInputStream(filename);
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

	private void zipDir(String sendName) {
		byte[] buffer = new byte[1024];
		OutputStream snd = null;
		Socket sock = null;
		InputStream rcv = null;
		try {
			sock = new Socket(ADDRESS, PORT);
			snd = sock.getOutputStream();
			if (sendName.contains(File.separator)) {
				sendName = sendName.substring(sendName
						.lastIndexOf(File.separator) + 1);
			}
			JsonObject params = new JsonObject();
			params.addProperty("TYPE", "ZIP");
			params.addProperty("TOKEN", token);
			params.addProperty("FILENAME", sendName);
			snd.write(params.toString().getBytes());
			rcv = sock.getInputStream();
			rcv.read(buffer);
			snd.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				snd.close();
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
	private void logout() {
		OutputStream snd = null;
		Socket sock = null;
		try {
			sock = new Socket(ADDRESS, PORT);
			snd = sock.getOutputStream();
			JsonObject params = new JsonObject();
			params.addProperty("TYPE", "LOGOUT");
			params.addProperty("TOKEN", token);
			snd.write(params.toString().getBytes());
			snd.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				snd.close();
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
