package za.ac.sun.cs.intlola.processing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import za.ac.sun.cs.intlola.Intlola;
import za.ac.sun.cs.intlola.file.ArchiveFile;
import za.ac.sun.cs.intlola.file.Const;
import za.ac.sun.cs.intlola.file.FileUtils;
import za.ac.sun.cs.intlola.file.IntlolaFile;

import com.google.gson.JsonObject;

public class Processor {

	private InetSocketAddress		address;

	private final ExecutorService	executor;

	protected IntlolaMode			mode;

	private String					project;

	private InputStream				rcv		= null;
	private OutputStream			snd		= null;
	private Socket					sock	= null;

	private String					username;

	/**
	 * Construct processor with default values.
	 * 
	 * @param username
	 * @param project
	 * @param mode
	 * @param address
	 * @param port
	 */
	public Processor(final String username, final String project,
			final IntlolaMode mode, final String address, final int port) {
		setFields(username, project, mode, address, port);
		// Only one thread worker. Interaction with server should be sequential.
		executor = Executors.newFixedThreadPool(1);
	}

	public String getAddress() {
		return address.getHostName();
	}

	public IntlolaMode getMode() {
		return mode;
	}

	public int getPort() {
		return address.getPort();
	}

	public String getProject() {
		return project;
	}

	public String getUsername() {
		return username;
	}

	public void handleArchive(final String location, final String zipLoc) {
		final String filename = zipLoc + File.separator + getProject() + "_"
				+ System.currentTimeMillis() + ".zip";
		executor.execute(new ArchiveBuilder(location, filename));
		if (mode.isRemote()) {
			sendFile(new ArchiveFile(filename));
			FileUtils.delete(filename);
		}
		logout();
	}

	private boolean init() {
		try {
			sock = new Socket();
			sock.connect(address, 5000);
			snd = sock.getOutputStream();
			rcv = sock.getInputStream();
			return true;
		} catch (final IOException e) {
			Intlola.log(e, "No server detected");
			return false;
		}
	}

	public IntlolaError login(String username, final String password,
			final String project, final IntlolaMode mode, final String address,
			final int port) {
		// Set fields to values specified by user.
		setFields(username, project, mode, address, port);
		if (mode.isRemote()) {
			final JsonObject params = new JsonObject();
			params.addProperty(Const.REQ, Const.LOGIN);
			params.addProperty(Const.UNAME, username);
			params.addProperty(Const.PWORD, password);
			params.addProperty(Const.PROJECT, project);
			params.addProperty(Const.MODE, mode.toString());
			params.addProperty(Const.LANG, Const.JAVA);
			if (!init()) {
				return IntlolaError.CONNECTION;
			} else {
				final byte[] buffer = new byte[1024];
				try {
					snd.write(params.toString().getBytes());
					snd.flush();
					rcv.read(buffer);
					final String received = new String(buffer);
					if (received.startsWith(Const.OK)) {
						return IntlolaError.SUCCESS;
					} else {
						Intlola.log(null, received);
						return IntlolaError.LOGIN;
					}
				} catch (final IOException e) {
					Intlola.log(e, "Login error");
					return IntlolaError.LOGIN;
				}
			}
		} else {
			username += ":" + password;
			return IntlolaError.SUCCESS;
		}
	}

	public void logout() {
		if (!mode.isRemote()) {
			return;
		}
		executor.execute(new SessionEnder(sock, snd, rcv));
		executor.shutdown();
	}

	public void sendFile(final IntlolaFile file) {
		executor.execute(new FileSender(file, sock, snd, rcv));
	}

	private void setFields(final String username, final String project,
			final IntlolaMode mode, final String address, final int port) {
		this.username = username;
		this.project = project;
		this.mode = mode;
		this.address = new InetSocketAddress(address, port);
	}

}
