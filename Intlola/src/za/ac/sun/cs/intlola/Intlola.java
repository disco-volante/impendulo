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

package za.ac.sun.cs.intlola;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import za.ac.sun.cs.intlola.controller.Controller;
import za.ac.sun.cs.intlola.controller.ControllerFactory;
import za.ac.sun.cs.intlola.processing.paths.DefaultPaths;
import za.ac.sun.cs.intlola.util.InvalidModeException;
import za.ac.sun.cs.intlola.util.Plugin;

/**
 * Intlola is the actual plugin. It is responsible initiating a recording
 * session, capturing user details and coordinating the entire process of
 * interacting with Impendulo.
 * 
 * @author godfried
 * 
 */
public class Intlola extends AbstractUIPlugin implements IStartup {
	private static Intlola plugin;
	public static final String PLUGIN_ID = "za.ac.sun.cs.intlola";
	private static final QualifiedName RECORD_KEY = new QualifiedName(
			"intlola", "record");

	private static final Boolean RECORD_ON = new Boolean(true);
	private static boolean recording = false;

	public static Intlola getActive() {
		return Intlola.plugin;
	}

	private static String getLogMessage(final Exception e, final Object[] msgs) {
		String logMsg = "";
		for (Object msg : msgs) {
			if (msg == null) {
				msg = "NULL";
			}
			logMsg += " " + msg.toString();
		}
		if (e != null) {
			logMsg += e.getMessage();
		}
		return logMsg;
	}

	public static boolean isRecording() {
		return recording;
	}

	/**
	 * Determines whether Intlola is currently recording the given project.
	 * 
	 * @param project
	 * @return
	 */
	public static boolean projectRecording(IProject project) {
		try {
			Boolean status = (Boolean) project.getSessionProperty(RECORD_KEY);
			return status != null && status.equals(RECORD_ON);
		} catch (CoreException e) {
			Intlola.log(e);
		}
		return false;
	}

	/**
	 * log is Intlola's logging mechanism.
	 * 
	 * @param e
	 * @param msgs
	 */
	public static void log(final Exception e, final Object... msgs) {
		if (getActive() != null) {
			getActive().getLog().log(
					new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK,
							getLogMessage(e, msgs), e));
		} else {
			System.err.println(getLogMessage(e, msgs));
		}
	}

	/**
	 * startRecord initiates a recording session for a given project.
	 * 
	 * @param project
	 * @param shell
	 */
	public static void startRecord(final IProject project, final Shell shell) {
		try {
			getActive().setup(shell, project);
			project.setSessionProperty(RECORD_KEY, true);
			recording = true;
			Plugin.touchAll(project);
			Intlola.log(null, "Intlola record started", project.getName());
		} catch (IOException e) {
			Intlola.log(e);
		} catch (LoginException e) {
			Intlola.log(e);
		} catch (final CoreException e) {
			Intlola.log(e);
		} catch (InvalidModeException e) {
			Intlola.log(e);
		}
	}

	/**
	 * stopRecord ends recording for a given project.
	 * 
	 * @param project
	 * @param shell
	 */
	public static void stopRecord(final IProject project, final Shell shell) {
		getActive().controller.end(shell);
		try {
			project.setSessionProperty(Intlola.RECORD_KEY, null);
		} catch (final CoreException e) {
			Intlola.log(e);
		}
		recording = false;
		Intlola.log(null, "Intlola record stopping", project.getName());
	}

	private IResourceChangeListener changeListener = null;

	private boolean listenersAdded = false;

	private Controller controller;

	public Intlola() {
		Intlola.plugin = this;
	}

	@Override
	public void earlyStartup() {
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		if (!listenersAdded) {
			DebugPlugin.getDefault().getLaunchManager()
					.addLaunchListener(new Monitor());
			changeListener = new Listener();
			Plugin.getWorkspace().addResourceChangeListener(changeListener,
					IResourceChangeEvent.POST_CHANGE);
			listenersAdded = true;
		}
	}

	/**
	 * setup logs the user in and begins a new recording session.
	 * 
	 * @param shell
	 * @throws LoginException
	 * @throws InvalidModeException
	 * @throws IOException
	 */
	private void setup(Shell shell, IProject project) throws LoginException,
			InvalidModeException, IOException {
		controller = ControllerFactory.create(shell, getPreferenceStore());
		controller.start(shell, new DefaultPaths(project));
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		Intlola.plugin = null;
		super.stop(context);
	}

	public static void processChanges(String path, int launched)
			throws IOException {
		getActive().controller.getProcessor().processChanges(path, launched);
	}

}
