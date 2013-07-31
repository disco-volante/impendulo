package za.ac.sun.cs.intlola.processing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import za.ac.sun.cs.intlola.file.ArchiveFile;
import za.ac.sun.cs.intlola.file.Const;
import za.ac.sun.cs.intlola.file.FileUtils;
import za.ac.sun.cs.intlola.file.IndividualFile;
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
	private int fileCounter = 0;
	private String username;

	private Project[] availableProjects;
	private Project currentProject;
	private Map<Project, ArrayList<Submission>> history;
	private Submission currentSubmission;

	private String storePath;

	private String archivePath;

	private String id;

	/**
	 * Construct processor with default values.
	 * 
	 * @param username
	 * @param project
	 * @param mode
	 * @param address
	 * @param port
	 */
	public Processor(final IntlolaMode mode, final String storePath) {
		this.id = String.valueOf(System.nanoTime());
		this.mode = mode;
		this.storePath = storePath;
		executor = Executors.newFixedThreadPool(1);
		if (mode.isArchive()) {
			archivePath = FileUtils.joinPath(storePath, "archive"+id);
			new File(archivePath).mkdirs();
		}
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

	public void handleLocalArchive(final String zipName) {
		executor.execute(new ArchiveBuilder(archivePath, zipName));
		executor.shutdown();
	}

	private boolean init() {
		try {
			sock = new Socket();
			sock.connect(address, 5000);
			snd = sock.getOutputStream();
			rcv = sock.getInputStream();
			return true;
		} catch (final IOException e) {
			return false;
		}
	}

	public IntlolaError login(String username, final String password,
			final String address, final int port) {
		setFields(username, address, port);
		final JsonObject params = new JsonObject();
		params.addProperty(Const.REQ, Const.LOGIN);
		params.addProperty(Const.USER, this.username);
		params.addProperty(Const.PASSWORD, password);
		params.addProperty(Const.MODE, mode.toString());
		if (!init()) {
			return IntlolaError.CONNECTION
					.specific("Could not initialise connection on: "
							+ this.address.toString());
		} else {
			final byte[] buffer = new byte[1024];
			try {
				snd.write(params.toString().getBytes());
				snd.write(Const.EOF);
				snd.flush();
				int count = rcv.read(buffer);
				final String received = new String(buffer, 0, count);
				Gson gson = new Gson();
				try {
					availableProjects = gson.fromJson(received,
							new Project[1].getClass());
					return IntlolaError.SUCCESS;
				} catch (JsonSyntaxException e) {
					return IntlolaError.LOGIN
							.specific("Login attempt failed with: " + received);
				}
			} catch (final IOException e) {
				return IntlolaError.LOGIN
						.specific("Login attempt failed with: "
								+ e.getMessage());
			}
		}
	}

	public void logout() {
		if (mode.equals(IntlolaMode.ARCHIVE_REMOTE)) {
			String zipName = FileUtils.joinPath(storePath, id+".zip");
			executor.execute(new ArchiveBuilder(archivePath, zipName));
			sendFile(new ArchiveFile(zipName));
			FileUtils.delete(zipName);
		}
		executor.execute(new SessionEnder(sock, snd, rcv));
		executor.shutdown();
	}

	public void sendFile(final IntlolaFile file) {
		executor.execute(new FileSender(file, sock, snd, rcv));
	}

	private void setFields(final String username, final String address,
			final int port) {
		this.username = username;
		this.address = new InetSocketAddress(address, port);
	}

	@SuppressWarnings("unchecked")
	public boolean loadHistory() {
		String histPath = FileUtils.joinPath(storePath, "history.ser");
		try {
			history = (Map<Project, ArrayList<Submission>>) FileUtils
					.deserialize(histPath);
			return true;
		} catch (ClassNotFoundException e) {
		} catch (IOException e) {
		}
		history = new HashMap<Project, ArrayList<Submission>>();
		return false;
	}

	public void saveHistory() throws IOException {
		String histPath = FileUtils.joinPath(storePath, "history.ser");
		ArrayList<Submission> proj = history.get(currentProject);
		if (proj == null) {
			history.put(currentProject, new ArrayList<Submission>());
			proj = history.get(currentProject);
		}
		if (!proj.contains(currentSubmission)) {
			proj.add(currentSubmission);
		}
		FileUtils.serialize(histPath, history);
	}

	public IntlolaError continueSubmission(Submission submission,
			Project project) {
		currentSubmission = submission;
		currentProject = project;
		if (!mode.isRemote()) {
			return IntlolaError.SUCCESS;
		}
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
			try {
				setFileCounter(new Gson().fromJson(received, int.class));
				return IntlolaError.SUCCESS;
			} catch (final JsonSyntaxException e) {
				return IntlolaError.LOGIN
						.specific("Submission continuation attempt failed with: "
								+ received);
			}
		} catch (final IOException e) {
			return IntlolaError.LOGIN
					.specific("Submission continuation attempt failed with: "
							+ e.getMessage());
		}
	}

	public IntlolaError createSubmission(Project project) {
		currentProject = project;
		if (!mode.isRemote()) {
			return IntlolaError.SUCCESS;
		}
		final JsonObject params = new JsonObject();
		params.addProperty(Const.REQ, Const.SUBMISSION_NEW);
		params.addProperty(Const.PROJECT_ID, currentProject.Id);
		params.addProperty(Const.TIME, Calendar.getInstance().getTimeInMillis());
		final byte[] buffer = new byte[1024];
		try {
			snd.write(params.toString().getBytes());
			snd.write(Const.EOF);
			snd.flush();
			int count = rcv.read(buffer);
			final String received = new String(buffer, 0, count);
			Gson gson = new Gson();
			try {
				currentSubmission = gson.fromJson(received,
						new Submission().getClass());
				return IntlolaError.SUCCESS;
			} catch (final JsonSyntaxException e) {
				return IntlolaError.LOGIN
						.specific("Submission creation attempt failed with: "
								+ received);
			}
		} catch (final IOException e) {
			return IntlolaError.LOGIN
					.specific("Submission creation attempt failed with: "
							+ e.getMessage());
		}
	}

	public void setMode(IntlolaMode mode) throws InvalidModeException {
		if (mode.equals(IntlolaMode.ARCHIVE_LOCAL)
				|| mode.equals(IntlolaMode.ARCHIVE_REMOTE)
				|| mode.equals(IntlolaMode.FILE_REMOTE)) {
			this.mode = mode;
		} else {
			throw new InvalidModeException(mode);
		}
	}

	public void processChanges(final String path, final boolean isFile,
			final int kind) throws IOException {
		final char kindSuffix = FileUtils.getKind(kind);
		final int num = fileCounter++;
		if (getMode().isArchive()) {
			final String name = FileUtils.joinPath(archivePath, FileUtils
					.encodeName(path, System.nanoTime(), num, kindSuffix));
			if (isFile) {
				FileUtils.copy(path, name);
			} else {
				FileUtils.touch(name);
			}
		} else if (getMode().isRemote()) {
			sendFile(new IndividualFile(path, kindSuffix, num, isFile));
		}
	}

	public int getFileCounter() {
		return fileCounter;
	}

	public void setFileCounter(int fileCounter) {
		this.fileCounter = fileCounter;
	}
}
