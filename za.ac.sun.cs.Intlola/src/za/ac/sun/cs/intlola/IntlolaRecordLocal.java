package za.ac.sun.cs.intlola;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;

public class IntlolaRecordLocal extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IProject project = Intlola.getSelectedProject(event);
		if (!Intlola.getRecordStatus(project)) {
			Intlola.startLocalRecord(project);
		}
		return null;
	}

}
