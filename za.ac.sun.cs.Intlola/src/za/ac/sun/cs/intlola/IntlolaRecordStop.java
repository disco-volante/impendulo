package za.ac.sun.cs.intlola;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.handlers.HandlerUtil;

public class IntlolaRecordStop extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IProject project = Intlola.getSelectedProject(event);
		if (Intlola.getRecordStatus(project)) {
			if (Intlola.getLocalStatus(project)) {
				Intlola.stopLocalRecord(project, HandlerUtil.getActiveShell(event));
			}
			else if (Intlola.getRemoteStatus(project)) {
				Intlola.stopRemoteRecord(project);
			}
			else {
				Intlola.stopRecord(project);
			}
		}
		return null;
	}

}
