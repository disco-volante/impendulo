package za.ac.sun.cs.intlola;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

import za.ac.sun.cs.intlola.file.Const;

public class IntlolaVisitor implements IResourceDeltaVisitor {

	@Override
	public boolean visit(final IResourceDelta delta) throws CoreException {
		final IResource resource = delta.getResource();
		final IProject project = resource.getProject();
		if (project == null) {
			return true;
		} else if (Intlola.getRecordStatus(project)) {
			final String path = resource.getLocation().toString();
			if(path.contains("test")){
				return false;
			}
			boolean sendContents = resource.getType() == IResource.FILE
					&& path.trim().endsWith(Const.JAVA);
			try {
				Intlola.getActive().getProcessor()
						.processChanges(path, sendContents, delta.getKind());
			} catch (IOException e) {
				Intlola.log(e, "Could not process changes");
			}
			return true;
		} else {
			return false;
		}
	}

}
