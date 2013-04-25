package za.ac.sun.cs.intlola;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.Calendar;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import za.ac.sun.cs.intlola.file.IndividualFile;

public class IntlolaVisitor implements IResourceDeltaVisitor {

	private static int counter = 0;
	private static final String ECLIPSE_SEP = new StringBuffer().append(
			IPath.SEPARATOR).toString();
	private static final String COMPONENT_SEP = "_";
	private static final String FORMAT = "%1$tY%1$tm%1$td%1$tH%1$tM%1$tS%1$tL";
	private static String storePath = Intlola.getDefault().getStateLocation()
			.toOSString();

	

	public static void processChanges(final IResource resource, final int kind) {
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
		if (Intlola.proc.mode.isArchive()) {
			save(resource, kindSuffix);
		} else if(Intlola.proc.mode.isRemote()){
			send(resource, kindSuffix);
		}
	}

	private static void save(final IResource resource,
			final char kindSuffix) {
		final String f = resource.getLocation().toString();
		final StringBuffer d = new StringBuffer(IntlolaVisitor.storePath);
		d.append(File.separator);
		d.append(f.replace(IntlolaVisitor.ECLIPSE_SEP,
				IntlolaVisitor.COMPONENT_SEP));
		d.append(IntlolaVisitor.COMPONENT_SEP);
		d.append(String.format(IntlolaVisitor.FORMAT, Calendar.getInstance()));
		d.append(IntlolaVisitor.COMPONENT_SEP);
		d.append(IntlolaVisitor.counter++);
		d.append(IntlolaVisitor.COMPONENT_SEP);
		d.append(kindSuffix);
		if (resource.getType() == IResource.FILE) {
			Utils.copy(f, d.toString());
		} else {
			Utils.touch(d.toString());
		}

	}

	private static void send(final IResource resource,
			final char kindSuffix) {
		final String f = resource.getLocation().toString();
		Intlola.proc.sendFile(new IndividualFile(f, kindSuffix, counter++,
				resource.getType() == IResource.FILE));
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
