package za.ac.sun.cs.intlola.processing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import za.ac.sun.cs.intlola.Intlola;
import za.ac.sun.cs.intlola.file.ArchiveFile;
import za.ac.sun.cs.intlola.file.Const;
import za.ac.sun.cs.intlola.file.FileUtils;
import za.ac.sun.cs.intlola.file.IntlolaFile;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class Processor {

	private InetSocketAddress address;

	private final ExecutorService executor;

	protected IntlolaMode mode;

	private InputStream rcv = null;
	private OutputStream snd = null;
	private Socket sock = null;

	private String username;

	private Project[] availableProjects;
	private Project currentProject;
	private Map<Project, ArrayList<Submission>> history;
	private Submission currentSubmission;

	/**
	 * Construct processor with default values.
	 * 
	 * @param username
	 * @param project
	 * @param mode
	 * @param address
	 * @param port
	 */
	public Processor(final String username, final IntlolaMode mode,
			final String address, final int port) {
		setFields(username, mode, address, port);
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

	public Project getCurrentProject() {
		return currentProject;
	}

	public String getUsername() {
		return username;
	}

	public Project[] getAvailableProjects() {
		return availableProjects;
	}

	public Map<Project, ArrayList<Submission>> getHistory() {
		return history;
	}

	public void handleArchive(final String location, final String zipName) {
		executor.execute(new ArchiveBuilder(location, zipName));
		if (mode.isRemote()) {
			sendFile(new ArchiveFile(zipName));
			FileUtils.delete(zipName);
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
			final String address, final int port) {
		this.username = username;
		this.address = new InetSocketAddress(address, port);
		final JsonObject params = new JsonObject();
		params.addProperty(Const.REQ, Const.LOGIN);
		params.addProperty(Const.USER, username);
		params.addProperty(Const.PASSWORD, password);
		params.addProperty(Const.MODE, mode.toString());
		if (!init()) {
			return IntlolaError.CONNECTION
					.specific("Could not initialise connection on: "
							+ address.toString());
		} else {
			final byte[] buffer = new byte[1024];
			try {
				snd.write(params.toString().getBytes());
				snd.write(Const.EOF);
				snd.flush();
				int count = rcv.read(buffer);
				final String received = new String(buffer, 0, count);
				Gson gson = new Gson();
				availableProjects = gson.fromJson(received,
						new Project[1].getClass());
				return IntlolaError.SUCCESS;
			} catch (final IOException e) {
				Intlola.log(e, "Login error");
				return IntlolaError.LOGIN
						.specific("Login attempt failed with: "
								+ e.getMessage());
			} catch (JsonSyntaxException e) {
				Intlola.log(e, "Login error");
				return IntlolaError.LOGIN
						.specific("Login attempt failed with: "
								+ e.getMessage());
			}
		}
	}

	public void logout() {
		if (mode.isRemote()) {
			executor.execute(new SessionEnder(sock, snd, rcv));
		}
		executor.shutdown();
	}

	public void sendFile(final IntlolaFile file) {
		executor.execute(new FileSender(file, sock, snd, rcv));
	}

	private void setFields(final String username, final IntlolaMode mode,
			final String address, final int port) {
		this.username = username;
		this.mode = mode;
		this.address = new InetSocketAddress(address, port);
	}

	@SuppressWarnings("unchecked")
	public boolean loadHistory(String storePath) {
		String histPath = storePath + File.separator + "history.ser";
		try {
			history = (Map<Project, ArrayList<Submission>>) FileUtils
					.deserialize(histPath);
			return true;
		} catch (ClassNotFoundException e) {
			Intlola.log(e);
		} catch (IOException e) {
			Intlola.log(e);
		}
		history = new HashMap<Project, ArrayList<Submission>>();
		return false;
	}

	public void saveHistory(String storePath) {
		String histPath = storePath + File.separator + "history.ser";
		ArrayList<Submission> proj = history.get(currentProject);
		if (proj == null) {
			history.put(currentProject, new ArrayList<Submission>());
			proj = history.get(currentProject);
		}
		if (!proj.contains(currentSubmission)) {
			proj.add(currentSubmission);
		}
		try {
			FileUtils.serialize(histPath, history);
		} catch (IOException e) {
			Intlola.log(e);
		}
	}

	public IntlolaError continueSubmission(Submission submission,
			Project project) {
		currentSubmission = submission;
		currentProject = project;
		if (mode.isRemote()) {
			final JsonObject params = new JsonObject();
			params.addProperty(Const.REQ, Const.SUBMISSION_CONTINUE);
			params.addProperty(Const.SUBMISSION_ID, currentSubmission.Id);
			final byte[] buffer = new byte[1024];
			try {
				snd.write(params.toString().getBytes());
				snd.write(Const.EOF);
				snd.flush();
				int count = rcv.read(buffer);
				final String received = new String(buffer, 0, count);
				if (received.startsWith(Const.OK)) {
					return IntlolaError.SUCCESS;
				} else {
					Intlola.log(null, "Submission continuation error: "
							+ received);
					return IntlolaError.LOGIN
							.specific("Submission continuation attempt failed with: "
									+ received);
				}
			} catch (final IOException e) {
				Intlola.log(e, "Submission continuation error");
				return IntlolaError.LOGIN
						.specific("Submission continuation attempt failed with: "
								+ e.getMessage());
			} catch (final JsonSyntaxException e) {
				Intlola.log(e, "Submission continuation error");
				return IntlolaError.LOGIN
						.specific("Submission continuation attempt failed with: "
								+ e.getMessage());
			}
		} else {
			return IntlolaError.SUCCESS;
		}
	}

	public IntlolaError createSubmission(Project project) {
		currentProject = project;
		if (mode.isRemote()) {
			final JsonObject params = new JsonObject();
			params.addProperty(Const.REQ, Const.SUBMISSION_NEW);
			params.addProperty(Const.PROJECT_ID, currentProject.Id);
			final byte[] buffer = new byte[1024];
			try {
				snd.write(params.toString().getBytes());
				snd.write(Const.EOF);
				snd.flush();
				int count = rcv.read(buffer);
				final String received = new String(buffer, 0, count);
				Gson gson = new Gson();
				currentSubmission = gson.fromJson(received,
						new Submission().getClass());
				return IntlolaError.SUCCESS;
			} catch (final IOException e) {
				Intlola.log(e, "Submission creation error");
				return IntlolaError.LOGIN
						.specific("Submission creation attempt failed with: "
								+ e.getMessage());
			} catch (final JsonSyntaxException e) {
				Intlola.log(e, "Submission creation error");
				return IntlolaError.LOGIN
						.specific("Submission creation attempt failed with: "
								+ e.getMessage());
			}
		} else {
			return IntlolaError.SUCCESS;
		}
	}

	public void setMode(IntlolaMode mode) {
		this.mode = mode;
	}
}
