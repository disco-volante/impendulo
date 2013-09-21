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

import java.io.File;
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import za.ac.sun.cs.intlola.gui.LoginDialog;
import za.ac.sun.cs.intlola.gui.ModeDialog;
import za.ac.sun.cs.intlola.gui.SubmissionDialog;
import za.ac.sun.cs.intlola.preferences.PreferenceConstants;
import za.ac.sun.cs.intlola.processing.IOUtils;
import za.ac.sun.cs.intlola.processing.IntlolaError;
import za.ac.sun.cs.intlola.processing.IntlolaMode;
import za.ac.sun.cs.intlola.processing.InvalidModeException;
import za.ac.sun.cs.intlola.processing.Processor;

public class Intlola extends AbstractUIPlugin implements IStartup {
	private static Intlola plugin;
	protected static final String PLUGIN_ID = "za.ac.sun.cs.intlola";
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

	public static boolean projectRecording(IProject project) {
		try {
			Boolean status = (Boolean) project.getSessionProperty(RECORD_KEY);
			return status != null && status.equals(RECORD_ON);
		} catch (CoreException e) {
			Intlola.log(e);
			;
		}
		return false;
	}

	public static void log(final Exception e, final Object... msgs) {
		if (getActive() != null) {
			getActive().getLog().log(
					new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK,
							getLogMessage(e, msgs), e));
		} else {
			System.err.println(getLogMessage(e, msgs));
		}
	}

	public static void startRecord(final IProject project, final Shell shell) {
		try {
			String storepath = getActive().calcStorePath(project);
			getActive().setProcessor(storepath);
			getActive().setup(shell);
			project.setSessionProperty(RECORD_KEY, true);
			recording = true;
			PluginUtils.touchAll(project);
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

	public static void stopRecord(final IProject project, final Shell shell) {
		getActive().stop(shell);
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

	private Processor proc;

	private String storePath;

	public Intlola() {
		Intlola.plugin = this;
	}

	@Override
	public void earlyStartup() {
	}

	public Processor getProcessor() {
		return proc;
	}

	private void setProcessor(String storePath) throws InvalidModeException {
		final IntlolaMode mode = IntlolaMode.getMode(getPreferenceStore()
				.getString(PreferenceConstants.P_MODE));
		proc = new Processor(mode, storePath);
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		if (!listenersAdded) {
			DebugPlugin.getDefault().getLaunchManager()
					.addLaunchListener(new IntlolaMonitor());
			changeListener = new IntlolaListener();
			PluginUtils.getWorkspace().addResourceChangeListener(
					changeListener, IResourceChangeEvent.POST_CHANGE);
			listenersAdded = true;
		}
	}

	private void setup(Shell shell) throws LoginException, InvalidModeException {
		proc.setMode(chooseMode(shell));
		if (proc.getMode().isRemote()) {
			if (!login(shell)) {
				throw new LoginException("Could not login to server.");
			} else {
				getPreferenceStore().setValue(PreferenceConstants.P_ADDRESS,
						proc.getAddress());
				getPreferenceStore().setValue(PreferenceConstants.P_MODE,
						proc.getMode().toString());
				getPreferenceStore().setValue(PreferenceConstants.P_PORT,
						proc.getPort());
				getPreferenceStore().setValue(PreferenceConstants.P_UNAME,
						proc.getUsername());
			}
			if (!startSubmission(shell)) {
				throw new LoginException("Could not start submission.");
			}
		}
	}

	private IntlolaMode chooseMode(Shell shell) {
		final ModeDialog dialog = new ModeDialog(shell, proc.getMode());
		final int code = dialog.open();
		if (code == Window.OK) {
			return dialog.getMode();
		} else {
			return IntlolaMode.NONE;
		}
	}

	private boolean login(final Shell shell) {
		final String uname = getPreferenceStore().getString(
				PreferenceConstants.P_UNAME);
		final String address = getPreferenceStore().getString(
				PreferenceConstants.P_ADDRESS);
		final int port = getPreferenceStore()
				.getInt(PreferenceConstants.P_PORT);
		final LoginDialog dialog = new LoginDialog(shell, "Intlola login",
				uname, address, port);
		IntlolaError err = IntlolaError.DEFAULT;
		while (!err.equals(IntlolaError.SUCCESS)) {
			final int code = dialog.open(err);
			if (code == Window.OK) {
				err = proc.login(dialog.getUserName(), dialog.getPassword(),
						dialog.getAddress(), dialog.getPort());
			} else {
				break;
			}
		}
		return err.equals(IntlolaError.SUCCESS);
	}

	private boolean startSubmission(final Shell shell) {
		IntlolaError err = IntlolaError.DEFAULT;
		proc.loadHistory();
		SubmissionDialog subDlg = new SubmissionDialog(shell,
				proc.getAvailableProjects(), proc.getHistory());
		final int code = subDlg.open();
		if (code == Window.OK) {
			if (subDlg.isCreate()) {
				err = proc.createSubmission(subDlg.getProject());
			} else {
				err = proc.continueSubmission(subDlg.getSubmission(),
						subDlg.getProject());
			}
		} else {
			err = IntlolaError.USER;
		}
		if (err.equals(IntlolaError.SUCCESS)) {
			return true;
		} else {
			if (!err.equals(IntlolaError.USER)) {
				MessageDialog.openError(shell, err.toString(),
						err.getDescription());
			}
			return false;
		}
	}

	private void stop(Shell shell) {
		if (proc.getMode().isRemote()) {
			proc.logout();
		} else if (proc.getMode().equals(IntlolaMode.ARCHIVE_LOCAL)) {
			proc.handleLocalArchive(IOUtils.getFilename(shell));
		}
		try {
			proc.saveHistory();
		} catch (IOException e) {
			log(e, "Could not save history.");
		}
	}

	private String calcStorePath(IProject project) throws IOException {
		storePath = IOUtils.joinPath(project.getLocation().toOSString(),
				".intlola");
		File dir = new File(storePath);
		if (!dir.exists() && !dir.mkdirs()) {
			throw new IOException("Could not create plugin directory.");
		}
		return storePath;
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		Intlola.plugin = null;
		super.stop(context);
	}

}
