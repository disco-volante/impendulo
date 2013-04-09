package za.ac.sun.cs.intlola;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Calendar;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public class IntlolaVisitor implements IResourceDeltaVisitor {

	private static final String DIRECTORY_SEP = System
			.getProperty("file.separator");

	private static final String ECLIPSE_SEP = new StringBuffer().append(
			IPath.SEPARATOR).toString();

	private static final String COMPONENT_SEP = "_";

	private static final String FORMAT = "%1$tY%1$tm%1$td%1$tH%1$tM%1$tS%1$tL";

	private static String storePath = Intlola.getDefault().getStateLocation()
			.toOSString();

	private static int counter = 0;

	public static void copy(final IResource resource, final char kindSuffix) {
		final String l = resource.getLocation().toString();
		final String f = resource.getFullPath().toString();
		final StringBuffer d = new StringBuffer(IntlolaVisitor.storePath);
		d.append(IntlolaVisitor.DIRECTORY_SEP);
		d.append(f.replace(IntlolaVisitor.ECLIPSE_SEP,
				IntlolaVisitor.COMPONENT_SEP));
		d.append(IntlolaVisitor.COMPONENT_SEP);
		d.append(String.format(IntlolaVisitor.FORMAT, Calendar.getInstance()));
		d.append(IntlolaVisitor.COMPONENT_SEP);
		d.append(IntlolaVisitor.counter++);
		d.append(IntlolaVisitor.COMPONENT_SEP);
		d.append(kindSuffix);
		IntlolaVisitor.copyFile(l, d.toString());
		Intlola.sender.send(SendMode.MULTIPLE, d.toString());
	}

	public static void copyFile(final String fromName, final String toName) {
		try {
			final File fromFile = new File(fromName);
			final File toFile = new File(toName);
			if (!fromFile.exists()) {
				throw new IOException("No such file: " + fromName);
			}
			if (!fromFile.isFile()) {
				throw new IOException("Not a file: " + fromName);
			}
			if (!fromFile.canRead()) {
				throw new IOException("Cannot read file: " + fromName);
			}
			if (toFile.exists()) {
				throw new IOException("File already exists: " + fromName);
			}
			final FileInputStream from = new FileInputStream(fromFile);
			final FileOutputStream to = new FileOutputStream(toFile);
			final byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = from.read(buffer)) != -1) {
				to.write(buffer, 0, bytesRead);
			}
			from.close();
			to.close();
		} catch (final IOException e) {
			Intlola.log(e);
		}
	}

	public static void copyOrTouch(final IResource resource, final int kind) {
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
		if (resource.getType() == IResource.FILE) {
			IntlolaVisitor.copy(resource, kindSuffix);
		} else {
			IntlolaVisitor.touch(resource, kindSuffix);
		}
	}

	public static void touch(final IResource resource, final char kindSuffix) {
		final String f = resource.getFullPath().toString();
		final StringBuffer d = new StringBuffer(IntlolaVisitor.storePath);
		d.append(IntlolaVisitor.DIRECTORY_SEP);
		d.append(f.replace(IntlolaVisitor.ECLIPSE_SEP,
				IntlolaVisitor.COMPONENT_SEP));
		d.append(IntlolaVisitor.COMPONENT_SEP);
		d.append(String.format(IntlolaVisitor.FORMAT, Calendar.getInstance()));
		d.append(IntlolaVisitor.COMPONENT_SEP);
		d.append(IntlolaVisitor.counter++);
		d.append(IntlolaVisitor.COMPONENT_SEP);
		d.append(kindSuffix);
		IntlolaVisitor.touchFile(d.toString());
		Intlola.sender.send(SendMode.MULTIPLE, d.toString());
	}

	public static void touchFile(final String toName) {
		try {
			final File toFile = new File(toName);
			if (toFile.exists()) {
				throw new IOException("File already exists: " + toName);
			}
			final FileOutputStream to = new FileOutputStream(toFile);
			to.write(0);
			to.close();
		} catch (final IOException e) {
			Intlola.log(e);
		}
	}

	@Override
	public boolean visit(final IResourceDelta delta) throws CoreException {
		final IResource resource = delta.getResource();
		final IProject project = resource.getProject();
		if (project == null) {
			return true;
		} else if (Intlola.getRecordStatus(project)) {
			IntlolaVisitor.copyOrTouch(resource, delta.getKind());
			return true;
		} else {
			return false;
		}
	}

}
