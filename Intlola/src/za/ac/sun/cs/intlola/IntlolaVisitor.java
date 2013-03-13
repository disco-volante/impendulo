package za.ac.sun.cs.intlola;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

	private static String storePath = Intlola.getDefault().getStateLocation().toOSString();

	private static int counter = 0;

	public static void copyFile(String fromName, String toName) {
		try {
			File fromFile = new File(fromName);
			File toFile = new File(toName);
			if (!fromFile.exists()) {
				throw new IOException("no such file: " + fromName);
			}
			if (!fromFile.isFile()) {
				throw new IOException("not a file: " + fromName);
			}
			if (!fromFile.canRead()) {
				throw new IOException("cannot read file: " + fromName);
			}
			if (toFile.exists()) {
				throw new IOException("file already exists: " + fromName);
			}
			FileInputStream from = new FileInputStream(fromFile);
			FileOutputStream to = new FileOutputStream(toFile);
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = from.read(buffer)) != -1) {
				to.write(buffer, 0, bytesRead);
			}
			from.close();
			to.close();
		} catch (IOException e) {
		}
	}

	public static void touchFile(String toName) {
		try {
			File toFile = new File(toName);
			if (toFile.exists()) {
				throw new IOException("file already exists: " + toName);
			}
			FileOutputStream to = new FileOutputStream(toFile);
			to.write(0);
			to.close();
			Intlola.sender.send(SendMode.ONSAVE, toName);
		} catch (IOException e) {
		}
	}

	public static void copy(IResource resource, char kindSuffix) {
		String l = resource.getLocation().toString();
		String f = resource.getFullPath().toString();
		StringBuffer d = new StringBuffer(storePath);
		d.append(DIRECTORY_SEP);
		d.append(f.replace(ECLIPSE_SEP, COMPONENT_SEP));
		d.append(COMPONENT_SEP);
		d.append(String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS%1$tL", Calendar
				.getInstance()));
		d.append(COMPONENT_SEP);
		d.append(counter++);
		d.append(COMPONENT_SEP);
		d.append(kindSuffix);
		copyFile(l, d.toString());
		Intlola.sender.send(SendMode.ONSAVE, d.toString());
	}

	public static void touch(IResource resource, char kindSuffix) {
		String f = resource.getFullPath().toString();
		StringBuffer d = new StringBuffer(storePath);
		d.append(DIRECTORY_SEP);
		d.append(f.replace(ECLIPSE_SEP, COMPONENT_SEP));
		d.append(COMPONENT_SEP);
		d.append(String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS%1$tL", Calendar
				.getInstance()));
		d.append(COMPONENT_SEP);
		d.append(counter++);
		d.append(COMPONENT_SEP);
		d.append(kindSuffix);
		touchFile(d.toString());
	}

	public static void copyOrTouch(IResource resource, int kind) {
		if (resource.getType() == IResource.FILE) {
			switch (kind) {
			case IResourceDelta.ADDED:
				copy(resource, 'a');
				break;
			case IResourceDelta.CHANGED:
				copy(resource, 'c');
				break;
			case IResourceDelta.REMOVED:
				copy(resource, 'r');
				break;
			case Intlola.LAUNCHED:
				copy(resource, 'l');
				break;
			}
		} else {
			switch (kind) {
			case IResourceDelta.ADDED:
				touch(resource, 'a');
				break;
			case IResourceDelta.CHANGED:
				touch(resource, 'c');
				break;
			case IResourceDelta.REMOVED:
				touch(resource, 'r');
				break;
			case Intlola.LAUNCHED:
				touch(resource, 'l');
				break;
			}
		}
	}

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();
		IProject project = resource.getProject();
		if (project == null) {
			return true;
		}
		if (Intlola.getRecordStatus(project)) {
			copyOrTouch(resource, delta.getKind());
			return true;
		}
		return false;
	}

}
