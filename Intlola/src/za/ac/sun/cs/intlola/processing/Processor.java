//Copyright (c) 2013, The Impendulo Authors
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without modification,
//are permitted provided that the following conditions are met:
//
//  Redistributions of source code must retain the above copyright notice, this
//  list of conditions and the following disclaimer.
//
//  Redistributions in binary form must reproduce the above copyright notice, this
//  list of conditions and the following disclaimer in the documentation and/or
//  other materials provided with the distribution.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
//ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
//ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
//(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
//LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
//ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

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
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import za.ac.sun.cs.intlola.file.ArchiveFile;
import za.ac.sun.cs.intlola.file.Const;
import za.ac.sun.cs.intlola.file.IndividualFile;
import za.ac.sun.cs.intlola.file.IntlolaFile;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

/**
 * Processor interacts with Impendulo over TCP. It logs the user in to
 * Impendulo, retrieves available projects, sends files and logs them out.
 * 
 * @author godfried
 * 
 */
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

	private String storePath;

	private String archivePath;

	/**
	 * Construct processor with default values.
	 * 
	 * @param username
	 * @param project
	 * @param mode
	 * @param address
	 * @param port
	 * @throws InvalidModeException
	 */
	public Processor(final IntlolaMode mode, final String storePath)
			throws InvalidModeException {
		this.storePath = IOUtils.joinPath(storePath, UUID.randomUUID()
				.toString());
		setMode(mode);
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

	/**
	 * handleLocalArchive shuts down the processor if the snapshots are being
	 * stored locally.
	 * 
	 * @param zipName
	 */
	public void handleLocalArchive(final String zipName) {
		executor.execute(new ArchiveBuilder(archivePath, zipName));
		executor.shutdown();
	}

	/**
	 * init creates the socket over which communication will take place.
	 * 
	 * @return
	 */
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

	/**
	 * login attempts to log the user in to Impendulo with the provided details.
	 * If successful, it will also receive a list of the available projects
	 * which the user can attempt.
	 * 
	 * @param username
	 * @param password
	 * @param address
	 * @param port
	 * @return
	 */
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
			try {
				IOUtils.writeJson(snd, params);
				String projects = IOUtils.read(rcv);
				Gson gson = new Gson();
				try {
					availableProjects = gson.fromJson(projects.toString(),
							new Project[1].getClass());
					return IntlolaError.SUCCESS;
				} catch (JsonSyntaxException e) {
					return IntlolaError.LOGIN
							.specific("Login attempt failed, invalid username or password.");
				}
			} catch (final IOException e) {
				return IntlolaError.LOGIN.specific("Login attempt failed.");
			}
		}
	}

	/**
	 * logout ends communication with Impendulo and shuts down the processor.
	 */
	public void logout() {
		if (mode.equals(IntlolaMode.ARCHIVE_REMOTE)) {
			String zipName = IOUtils.joinPath(storePath, "intlola.zip");
			executor.execute(new ArchiveBuilder(archivePath, zipName));
			sendFile(new ArchiveFile(zipName));
			IOUtils.delete(zipName);
		}
		executor.execute(new SessionEnder(sock, snd, rcv));
		executor.shutdown();
	}

	/**
	 * sendFile sends a file asynchronously to Impendulo.
	 * 
	 * @param file
	 */
	public void sendFile(final IntlolaFile file) {
		executor.execute(new FileSender(file, sock, snd, rcv));
	}

	private void setFields(final String username, final String address,
			final int port) {
		this.username = username;
		this.address = new InetSocketAddress(address, port);
	}

	/**
	 * loadHistory loads previous submissions the user has worked on.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean loadHistory() {
		String histPath = IOUtils.joinPath(storePath, "history.ser");
		try {
			history = (Map<Project, ArrayList<Submission>>) IOUtils
					.deserialize(histPath);
			return true;
		} catch (ClassNotFoundException e) {
		} catch (IOException e) {
		}
		history = new HashMap<Project, ArrayList<Submission>>();
		return false;
	}

	/**
	 * saveHistory stores submissions this user has worked on including the
	 * current submission.
	 * 
	 * @throws IOException
	 */
	public void saveHistory() throws IOException {
		String histPath = IOUtils.joinPath(storePath, "history.ser");
		ArrayList<Submission> proj = history.get(currentProject);
		if (proj == null) {
			history.put(currentProject, new ArrayList<Submission>());
			proj = history.get(currentProject);
		}
		if (!proj.contains(currentSubmission)) {
			proj.add(currentSubmission);
		}
		IOUtils.serialize(histPath, history);
	}

	/**
	 * continueSubmission allows the user to continue with an old submission.
	 * 
	 * @param submission
	 * @param project
	 * @return
	 */
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
		try {
			IOUtils.writeJson(snd, params);
			final String received = IOUtils.read(rcv);
			if (received.startsWith(Const.OK)) {
				return IntlolaError.SUCCESS;
			} else {
				return IntlolaError.LOGIN
						.specific("Could not continue submission.");
			}
		} catch (final IOException e) {
			return IntlolaError.LOGIN
					.specific("Could not continue submission.");
		}
	}

	/**
	 * createSubmission creates a new submission for the user.
	 * 
	 * @param project
	 * @return
	 */
	public IntlolaError createSubmission(Project project) {
		currentProject = project;
		if (!mode.isRemote()) {
			return IntlolaError.SUCCESS;
		}
		final JsonObject params = new JsonObject();
		params.addProperty(Const.REQ, Const.SUBMISSION_NEW);
		params.addProperty(Const.PROJECT_ID, currentProject.Id);
		params.addProperty(Const.TIME, Calendar.getInstance().getTimeInMillis());
		try {
			IOUtils.writeJson(snd, params);
			final String received = IOUtils.read(rcv);
			Gson gson = new Gson();
			try {
				currentSubmission = gson.fromJson(received,
						new Submission().getClass());
				return IntlolaError.SUCCESS;
			} catch (final JsonSyntaxException e) {
				return IntlolaError.LOGIN
						.specific("Could not create submission.");
			}
		} catch (final IOException e) {
			return IntlolaError.LOGIN.specific("Could not create submission.");
		}
	}

	public void setMode(IntlolaMode mode) throws InvalidModeException {
		if (mode == null || mode.equals(IntlolaMode.NONE)) {
			throw new InvalidModeException(mode);
		}
		this.mode = mode;
		if (mode.isArchive()) {
			archivePath = IOUtils.joinPath(storePath, "archive");
			new File(archivePath).mkdirs();
		}
	}

	/**
	 * processChanges processes a new snapshot and then either sends it or
	 * stores it if it is valid.
	 * 
	 * @param path
	 * @param sendContents
	 * @param kind
	 * @throws IOException
	 */
	public void processChanges(final String path, final int kind)
			throws IOException {
		char kindSuffix = IOUtils.getKind(kind);
		if (!IOUtils.shouldSend(kindSuffix, path)) {
			return;
		}
		boolean isSrc= IOUtils.isSrc(path);
		if (getMode().isArchive()) {
			final String name = IOUtils.joinPath(archivePath, IOUtils
					.encodeName(path, Calendar.getInstance().getTimeInMillis(),
							kindSuffix));
			if (isSrc) {
				IOUtils.copy(path, name);
			} else {
				IOUtils.touch(name);
			}
		} else if (getMode().isRemote()) {
			sendFile(new IndividualFile(path, kindSuffix, isSrc));
		}
	}
}
