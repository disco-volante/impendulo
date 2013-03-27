package za.ac.sun.cs.intlola;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.google.gson.JsonObject;

public class IntlolaSender {

	private final SendMode mode;

	private final String uname, project, address;

	private String token;

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

	public boolean login(final String password) {
		boolean ret = false;
		final byte[] buffer = new byte[1024];
		int read = 0;
		try {
			openConnection();
			final String format = mode.equals(SendMode.ONSAVE) ? "UNCOMPRESSED"
					: "ZIP";
			final JsonObject params = new JsonObject();
			params.addProperty("TYPE", "LOGIN");
			params.addProperty("USERNAME", uname);
			params.addProperty("PASSWORD", password);
			params.addProperty("PROJECT", project);
			params.addProperty("FORMAT", format);
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
				ret = true;
				loggedIn = true;
			}
		}
		return ret;
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
		if (loggedIn) {
			if (check.equals(SendMode.ONSTOP) && mode.equals(SendMode.ONSAVE)) {
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

	public void sendTests(final String testFile) {
		final byte[] buffer = new byte[1024];
		FileInputStream fis = null;
		try {
			openConnection();
			final JsonObject params = new JsonObject();
			params.addProperty("TYPE", "TESTS");
			params.addProperty("PROJECT", getProject());
			snd.write(params.toString().getBytes());
			rcv.read(buffer);
			if (new String(buffer).startsWith("ACCEPT")) {
				int count;
				fis = new FileInputStream(testFile);
				while ((count = fis.read(buffer)) >= 0) {
					snd.write(buffer, 0, count);
				}
			}
			snd.flush();
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

	protected String sh1(final String password) {
		String ret = null;
		try {
			final MessageDigest cript = MessageDigest.getInstance("SHA-1");
			cript.reset();
			cript.update(password.getBytes("utf8"));
			final byte[] mdbytes = cript.digest();
			final StringBuffer hexString = new StringBuffer();
			for (final byte mdbyte : mdbytes) {
				hexString.append(Integer.toHexString(0xFF & mdbyte));
			}
			ret = hexString.toString();
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;

	}

}
