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

	public static void copyFileLocal(String fromName, String toName) {
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
			// do nothing
		}
	}

	public static void touchFileLocal(String toName) {
		try {
			File toFile = new File(toName);
			if (toFile.exists()) {
				throw new IOException("file already exists: " + toName);
			}
			FileOutputStream to = new FileOutputStream(toFile);
			to.write(0);
			to.close();
		} catch (IOException e) {
			// do nothing
		}
	}

	public static void copyLocal(IResource resource, char kindSuffix) {
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
		copyFileLocal(l, d.toString());
//		System.out.println("COPY \"" + l + "\" -> \"" + d.toString() + "\"");
	}

	public static void touchLocal(IResource resource, char kindSuffix) {
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
		touchFileLocal(d.toString());
//		System.out.println("TOUCH \"" + d.toString() + "\"");
	}

	public static void copyOrTouchLocal(IResource resource, int kind) {
		if (resource.getType() == IResource.FILE) {
			switch (kind) {
			case IResourceDelta.ADDED:
				copyLocal(resource, 'a');
				break;
			case IResourceDelta.CHANGED:
				copyLocal(resource, 'c');
				break;
			case IResourceDelta.REMOVED:
				copyLocal(resource, 'r');
				break;
			case Intlola.LAUNCHED:
				copyLocal(resource, 'l');
				break;
			}
		} else {
			switch (kind) {
			case IResourceDelta.ADDED:
				touchLocal(resource, 'a');
				break;
			case IResourceDelta.CHANGED:
				touchLocal(resource, 'c');
				break;
			case IResourceDelta.REMOVED:
				touchLocal(resource, 'r');
				break;
			case Intlola.LAUNCHED:
				touchLocal(resource, 'l');
				break;
			}
		}
	}

	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();
		IProject project = resource.getProject();
		if (project == null) {
			return true;
		}
		if (Intlola.getLocalStatus(project)) {
			copyOrTouchLocal(resource, delta.getKind());
			return true;
		}
		return false;
	}

}
