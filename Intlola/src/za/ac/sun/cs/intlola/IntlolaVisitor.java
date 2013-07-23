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
		final int num = counter++;
		final String path = resource.getLocation().toString();
		final boolean isFile = resource.getType() == IResource.FILE;
		if (Intlola.getActive().getProcessor().getMode().isArchive()) {
			final String name = Intlola.getActive().getStorePath()
					+ File.separator
					+ FileUtils.encodeName(path, System.nanoTime(), num,
							kindSuffix);
			if (isFile) {
				FileUtils.copy(path, name);
			} else {
				FileUtils.touch(name);
			}
		} else if (Intlola.getActive().getProcessor().getMode().isRemote()) {
			Intlola.getActive()
					.getProcessor()
					.sendFile(new IndividualFile(path, kindSuffix, num, isFile));
		}
	}

	@Override
	public boolean visit(final IResourceDelta delta) throws CoreException {
		final IResource resource = delta.getResource();
		final IProject project = resource.getProject();
		boolean isSrc = resource.getType() == IResource.FILE
				&& resource.getLocation().toString().trim().endsWith("java");
		if (project == null) {
			return true;
		} else if (Intlola.getRecordStatus(project)) {
			if (isSrc) {
				IntlolaVisitor.processChanges(resource, delta.getKind());
			}
			return true;
		} else {
			return false;
		}
	}

	public static void setCounter(int counter) {
		IntlolaVisitor.counter = counter;
	}

}
