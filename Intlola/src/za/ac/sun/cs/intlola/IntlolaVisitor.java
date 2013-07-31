package za.ac.sun.cs.intlola;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

public class IntlolaVisitor implements IResourceDeltaVisitor {

	@Override
	public boolean visit(final IResourceDelta delta) throws CoreException {
		final IResource resource = delta.getResource();
		final IProject project = resource.getProject();
		boolean isFile = resource.getType() == IResource.FILE;
		if (project == null) {
			return true;
		} else if (Intlola.getRecordStatus(project)) {
			final String path = resource.getLocation().toString();
			try {
				Intlola.getActive().getProcessor()
						.processChanges(path, isFile, delta.getKind());
			} catch (IOException e) {
				Intlola.log(e, "Could not process changes");
			}
			return true;
		} else {
			return false;
		}
	}

}
