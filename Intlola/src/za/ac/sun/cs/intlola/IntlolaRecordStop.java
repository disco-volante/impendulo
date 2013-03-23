package za.ac.sun.cs.intlola;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.handlers.HandlerUtil;

public class IntlolaRecordStop extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IProject project = Intlola.getSelectedProject(event);
		if (Intlola.getRecordStatus(project)) {
			Intlola.stopRecord(project, HandlerUtil.getActiveShell(event));
		}
		return null;
	}

}
