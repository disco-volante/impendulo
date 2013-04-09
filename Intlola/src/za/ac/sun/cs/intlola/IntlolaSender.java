package za.ac.sun.cs.intlola;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.google.gson.JsonObject;

public class IntlolaSender {

	protected final SendMode mode;

	private String uname;

	private final String project;

	private final String address;
	private static final String OK = "OK";
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

	public String getUsername() {
		return uname;
	}

	public void login(final String username, String password) {
		if(username != null && !uname.equals(username)){
			uname = username;
		}
		final byte[] buffer = new byte[1024];
		try {
			final JsonObject params = new JsonObject();
			params.addProperty("TYPE", "LOGIN");
			params.addProperty("USERNAME", uname);
			params.addProperty("PASSWORD", password);
			params.addProperty("PROJECT", project);
			params.addProperty("MODE", mode.toString());
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
			params.addProperty("TYPE", "LOGOUT");
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
		} catch (IOException e) {
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}

	public void sendFile(final String fileName, FileType ftype) {
		byte[] readBuffer = new byte[2048];
		byte[] writeBuffer = new byte[2048];
		FileInputStream fis = null;
		try {
			String sendName;
			if (fileName.contains(File.separator)) {
				sendName = fileName.substring(fileName
						.lastIndexOf(File.separator) + 1);
			} else {
				sendName = fileName;
			}
			final JsonObject params = new JsonObject();
			params.addProperty("TYPE", "SEND");
			params.addProperty("FILENAME", sendName);
			params.addProperty("FILETYPE", ftype.toString());
			snd.write(params.toString().getBytes());
			snd.flush();
			rcv.read(readBuffer);
			String received = new String(readBuffer);
			if (received.startsWith(OK)) {
				int count;
				fis = new FileInputStream(fileName);
				while ((count = fis.read(writeBuffer)) >= 0) {
					snd.write(writeBuffer, 0, count);
				}
				snd.write("EOF".getBytes());
			} else {
				System.out.println(received);
			}
			snd.flush();
			rcv.read(readBuffer);
			received = new String(readBuffer);
			if (!received.startsWith(OK)) {
				System.out.println(received);
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

	public boolean loggedIn() {
		return loggedIn;
	}

}
