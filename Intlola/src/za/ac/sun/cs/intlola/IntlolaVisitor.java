package za.ac.sun.cs.intlola;

import java.security.InvalidParameterException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

public class IntlolaVisitor implements IResourceDeltaVisitor {

	private static int counter = 0;

	public static void sendChanges(final IResource resource, final int kind) {
		Intlola.log(null, "Intlola processing resource", resource, kind);
		char kindSuffix = ' ';
		switch (kind) {
		case IResourceDelta.ADDED:
			kindSuffix = 'a';
			break;
		case IResourceDelta.CHANGED:
			kindSuffix = 'c';
			break;
		case IResourceDelta.REMOVED:
			kindSuffix = 'r';
			break;
		case Intlola.LAUNCHED:
			kindSuffix = 'l';
			break;
		default:
			throw new InvalidParameterException();
		}
		final String f = resource.getFullPath().toString();
		IntlolaFile ifile = new SingleFile(f, kindSuffix, counter);
		Intlola.sender.sendFile(ifile);
	}

	@Override
	public boolean visit(final IResourceDelta delta) throws CoreException {
		final IResource resource = delta.getResource();
		final IProject project = resource.getProject();
		if (project == null) {
			return true;
		} else if (Intlola.getRecordStatus(project)) {
			IntlolaVisitor.sendChanges(resource, delta.getKind());
			return true;
		} else {
			return false;
		}
	}

}
