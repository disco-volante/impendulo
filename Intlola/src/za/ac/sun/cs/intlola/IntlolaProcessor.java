package za.ac.sun.cs.intlola;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import za.ac.sun.cs.intlola.file.ArchiveFile;
import za.ac.sun.cs.intlola.file.Const;
import za.ac.sun.cs.intlola.file.IntlolaFile;

import com.google.gson.JsonObject;

public class IntlolaProcessor {

	protected final IntlolaMode mode;

	private String uname;

	private final String project;

	private final String address;

	private final int port;
	private OutputStream snd = null;
	private Socket sock = null;
	private InputStream rcv = null;
	private boolean loggedIn;

	public IntlolaProcessor(final String uname, final String project,
			final IntlolaMode mode, final String address, final int port) {
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
		if (!mode.isRemote()) {
			uname += ":"+password;
		}
		if (username != null && !uname.equals(username)) {
			uname = username;
		}
		final byte[] buffer = new byte[1024];
		try {
			final JsonObject params = new JsonObject();
			params.addProperty(Const.REQ, Const.LOGIN);
			params.addProperty(Const.UNAME, uname);
			params.addProperty(Const.PWORD, password);
			params.addProperty(Const.PROJECT, project);
			params.addProperty(Const.MODE, mode.toString());
			params.addProperty(Const.LANG, Const.JAVA);
			snd.write(params.toString().getBytes());
			snd.flush();
			rcv.read(buffer);
			final String received = new String(buffer);
			if (received.startsWith(Const.OK)) {
				loggedIn = true;
			} else {
				System.err.println(received);
			}
		} catch (final IOException e) {
			e.printStackTrace();
			Intlola.log(e);
		}
	}

	public void logout() {
		try {
			final JsonObject params = new JsonObject();
			params.addProperty(Const.REQ, Const.LOGOUT);
			snd.write(params.toString().getBytes());
			snd.flush();
			final byte[] buffer = new byte[1024];
			rcv.read(buffer);
			final String received = new String(buffer);
			if (!received.startsWith(Const.OK)) {
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

	public boolean init() {
		boolean ret = true;
		if (mode.isRemote()) {
			try {
				sock = new Socket(address, port);
				snd = sock.getOutputStream();
				rcv = sock.getInputStream();
			} catch (final IOException e) {
				e.printStackTrace();
				ret = false;
			}
		}
		return ret;
	}

	public void sendFile(final IntlolaFile file) {
		final byte[] readBuffer = new byte[2048];
		final byte[] writeBuffer = new byte[2048];
		FileInputStream fis = null;
		try {
			JsonObject fjson = file.toJSON();
			fjson.addProperty(Const.REQ, Const.SEND);
			snd.write(fjson.toString().getBytes());
			snd.flush();
			System.out.println("sent: " + fjson.toString());
			rcv.read(readBuffer);
			String received = new String(readBuffer);
			if (received.startsWith(Const.OK)) {
				if (file.hasContents()) {
					int count;
					Intlola.log(null, file.toJSON());
					fis = new FileInputStream(file.getPath());
					while ((count = fis.read(writeBuffer)) >= 0) {
						snd.write(writeBuffer, 0, count);
					}
				}
				snd.write(Const.EOF.getBytes());
				snd.flush();
				System.out.println("sent: file");
				rcv.read(readBuffer);
				received = new String(readBuffer);
				if (!received.startsWith(Const.OK)) {
					System.out.println(received);
				}
				System.out.println("done");
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

	public void handleArchive(final File dirfile) {
		if (!mode.isRemote()) {
			final String creds = dirfile + File.separator + "credentials.txt";
			Utils.saveString(uname, creds);
		}
		final String filename = getProject() + ".zip";
		Utils.saveArchive(dirfile, filename);
		if (mode.isRemote()) {
			sendFile(new ArchiveFile(filename));
		}
	}

}
