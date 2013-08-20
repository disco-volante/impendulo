package za.ac.sun.cs.intlola;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public class IntlolaRecord extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IProject project = PluginUtils.getSelectedProject(event);
		if (!Intlola.projectRecording(project) && !Intlola.isRecording()) {
			Intlola.startRecord(project, HandlerUtil.getActiveShell(event));
		} else {
			MessageDialog
					.openError(
							HandlerUtil.getActiveShell(event),
							"Recording Error",
							"You are already recording a project. Please stop recording it before starting a new recording.");
		}
		return null;
	}

}
