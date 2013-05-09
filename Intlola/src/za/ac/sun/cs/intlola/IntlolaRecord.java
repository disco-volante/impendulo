package za.ac.sun.cs.intlola;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.handlers.HandlerUtil;

public class IntlolaRecord extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IProject project = PluginUtils.getSelectedProject(event);
		if (!Intlola.getRecordStatus(project)) {
			Intlola.startRecord(project, HandlerUtil.getActiveShell(event));
		}
		return null;
	}

}
