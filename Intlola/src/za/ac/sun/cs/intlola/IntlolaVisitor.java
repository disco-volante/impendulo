package za.ac.sun.cs.intlola;

import java.io.File;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

import za.ac.sun.cs.intlola.file.FileUtils;
import za.ac.sun.cs.intlola.file.IndividualFile;

public class IntlolaVisitor implements IResourceDeltaVisitor {

	private static int counter = 0;

	public static void processChanges(final IResource resource, final int kind) {
		final char kindSuffix = FileUtils.getKind(kind);
		if (Intlola.getActive().getProcessor().getMode().isArchive()) {
			save(resource, kindSuffix);
		} else if (Intlola.getActive().getProcessor().getMode().isRemote()) {
			send(resource, kindSuffix);
		}
	}

	private static void save(final IResource resource, final char kindSuffix) {
		final String f = resource.getLocation().toString();
		final String name = Intlola.getActive().getStorePath()
				+ File.separator
				+ FileUtils.encodeName(f, System.nanoTime(), counter++,
						kindSuffix);
		if (resource.getType() == IResource.FILE) {
			FileUtils.copy(f, name);
		} else {
			FileUtils.touch(name);
		}

	}

	private static void send(final IResource resource, final char kindSuffix) {
		final String f = resource.getLocation().toString();
		Intlola.getActive()
				.getProcessor()
				.sendFile(
						new IndividualFile(f, kindSuffix, counter++, resource
								.getType() == IResource.FILE));
	}

	@Override
	public boolean visit(final IResourceDelta delta) throws CoreException {
		final IResource resource = delta.getResource();
		final IProject project = resource.getProject();
		if (project == null) {
			return true;
		} else if (Intlola.getRecordStatus(project)) {
			IntlolaVisitor.processChanges(resource, delta.getKind());
			return true;
		} else {
			return false;
		}
	}

}
