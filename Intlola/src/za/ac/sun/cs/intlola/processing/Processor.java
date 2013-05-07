package za.ac.sun.cs.intlola.processing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import za.ac.sun.cs.intlola.Intlola;
import za.ac.sun.cs.intlola.IntlolaError;
import za.ac.sun.cs.intlola.IntlolaMode;
import za.ac.sun.cs.intlola.file.ArchiveFile;
import za.ac.sun.cs.intlola.file.Const;
import za.ac.sun.cs.intlola.file.IntlolaFile;

import com.google.gson.JsonObject;

public class Processor {

	protected IntlolaMode mode;

	private String username;

	private String project;

	private String address;

	private int port;
	private OutputStream snd = null;
	private Socket sock = null;
	private InputStream rcv = null;

	private SendQueue queue;

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
		queue = new SendQueue();
	}

	public String getProject() {
		return project;
	}

	public String getUsername() {
		return username;
	}

	public IntlolaError login(String username, final String password,
			String project, IntlolaMode mode, String address, int port) {
		// Set fields to values specified by user.
		setFields(username, project, mode, address, port);
		if (!init()) {
			return IntlolaError.CONN;
		}
		if (!mode.isRemote()) {
			username += ":" + password;
			return IntlolaError.SUCCESS;
		}
		final byte[] buffer = new byte[1024];
		try {
			final JsonObject params = new JsonObject();
			params.addProperty(Const.REQ, Const.LOGIN);
			params.addProperty(Const.UNAME, username);
			params.addProperty(Const.PWORD, password);
			params.addProperty(Const.PROJECT, project);
			params.addProperty(Const.MODE, mode.toString());
			params.addProperty(Const.LANG, Const.JAVA);
			snd.write(params.toString().getBytes());
			snd.flush();
			rcv.read(buffer);
			final String received = new String(buffer);
			if (received.startsWith(Const.OK)) {
				return IntlolaError.SUCCESS;
			} else {
				Intlola.log(null, received);
			}
		} catch (final IOException e) {
			Intlola.log(e, "Login error");
		}
		return IntlolaError.LOGIN;
	}

	private void setFields(String username, String project, IntlolaMode mode,
			String address, int port) {
		this.username = username;
		this.project = project;
		this.mode = mode;
		this.address = address;
		this.port = port;
	}

	public void logout() {
		if (mode.isRemote()) {
			queue.execute(new Quiter(sock, snd, rcv));
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
				Intlola.log(e, "No server detected");
				ret = false;
			}
		}
		return ret;
	}

	public void sendFile(final IntlolaFile file) {
		queue.execute(new Sender(file, snd, rcv));
	}

	

	public void handleArchive(final String location, final String zipLoc) {
		final String filename = zipLoc + File.separator + getProject() + "_"
				+ System.currentTimeMillis() + ".zip";
		queue.execute(new Archiver(location, filename));
		if (mode.isRemote()) {
			sendFile(new ArchiveFile(filename));
		}
		/*
		 * Utils.createZip(location, filename); if (mode.isRemote()) { try {
		 * sendFile(new ArchiveFile(filename)).join(); } catch
		 * (InterruptedException e) { Intlola.log(e,
		 * "Interrupted sending of archive: ", location); } }
		 */
		logout();
	}

	public String getAddress() {
		return address;
	}

	public IntlolaMode getMode() {
		return mode;
	}

	public int getPort() {
		return port;
	}

}
