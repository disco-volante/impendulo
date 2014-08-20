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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Calendar;

import za.ac.sun.cs.intlola.file.Const;
import za.ac.sun.cs.intlola.processing.json.Assignment;
import za.ac.sun.cs.intlola.processing.json.Project;
import za.ac.sun.cs.intlola.processing.json.ProjectInfo;
import za.ac.sun.cs.intlola.processing.json.Submission;
import za.ac.sun.cs.intlola.processing.paths.IPaths;
import za.ac.sun.cs.intlola.util.IO;
import za.ac.sun.cs.intlola.util.IntlolaError;
import za.ac.sun.cs.intlola.util.InvalidArgumentException;

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
public abstract class RemoteProcessor extends Processor {

	private InetSocketAddress address;

	protected InputStream rcv = null;
	protected OutputStream snd = null;
	protected Socket sock = null;
	private String username;

	private ProjectInfo[] projectInfos;

	public RemoteProcessor(final IPaths paths) throws IOException {
		super(paths);
	}

	public String getAddress() {
		return address.getHostName();
	}

	public int getPort() {
		return address.getPort();
	}

	public String getUsername() {
		return username;
	}

	public ProjectInfo[] getProjects() {
		return projectInfos;
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
	 * @param string
	 * @param port
	 * @return
	 */
	public IntlolaError login(String request, String username,
			final String password, final String address, final int port) {
		setFields(username, address, port);
		if (!init()) {
			return IntlolaError.CONNECTION
					.specific("Could not initialise connection on: "
							+ this.address.toString());
		}
		final JsonObject params = new JsonObject();
		params.addProperty(Const.REQUEST, request);
		params.addProperty(Const.USER, this.username);
		params.addProperty(Const.PASSWORD, password);
		params.addProperty(Const.MODE, getMode().toString());
		try {
			IO.writeJson(snd, params);
			String json = IO.read(rcv);
			Gson gson = new Gson();
			try {
				projectInfos = gson.fromJson(json,
						new ProjectInfo[0].getClass());
				return IntlolaError.SUCCESS;
			} catch (JsonSyntaxException e) {
				if (request.equals(Const.LOGIN)) {
					return IntlolaError.LOGIN
							.specific("Login failed, invalid username or password.");
				} else {
					return IntlolaError.LOGIN
							.specific("Registration failed, user already exists.");
				}
			}
		} catch (final IOException e) {
			return IntlolaError.LOGIN.specific("Login failed.");
		}
	}

	private void setFields(final String username, final String address,
			final int port) {
		this.username = username;
		this.address = new InetSocketAddress(address, port);
	}

	/**
	 * continueSubmission allows the user to continue with an old submission.
	 * 
	 * @param submission
	 * @param assignment
	 * @return
	 */
	public IntlolaError continueSubmission(Project project,
			Submission submission) {

		try {
			IO.setExtension(project.Lang);
		} catch (InvalidArgumentException iae) {
			return IntlolaError.FILE.specific(iae.getMessage());
		}
		final JsonObject params = new JsonObject();
		params.addProperty(Const.REQUEST, Const.SUBMISSION_CONTINUE);
		params.addProperty(Const.SUBMISSION_ID, submission.Id);
		try {
			IO.writeJson(snd, params);
			final String received = IO.read(rcv);
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
	public IntlolaError createSubmission(Project project, Assignment assignment) {
		try {
			IO.setExtension(project.Lang);
		} catch (InvalidArgumentException iae) {
			return IntlolaError.FILE.specific(iae.getMessage());
		}
		final JsonObject params = new JsonObject();
		params.addProperty(Const.REQUEST, Const.SUBMISSION_NEW);
		params.addProperty(Const.ASSIGNMENT_ID, assignment.Id);
		params.addProperty(Const.PROJECT_ID, project.Id);
		params.addProperty(Const.TIME, Calendar.getInstance().getTimeInMillis());
		try {
			IO.writeJson(snd, params);
			final String received = IO.read(rcv);
			if (received.startsWith(Const.OK)) {
				return IntlolaError.SUCCESS;
			} else {
				return IntlolaError.LOGIN
						.specific("Could not create submission.");
			}
		} catch (final IOException e) {
			return IntlolaError.LOGIN.specific("Could not create submission.");
		}
	}
}
