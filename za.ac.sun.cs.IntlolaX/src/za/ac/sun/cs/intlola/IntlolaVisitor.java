package za.ac.sun.cs.intlola;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;

public class IntlolaVisitor implements IResourceDeltaVisitor {

	private static String DIRECTORY_SEP = System.getProperty("file.separator");
		
	private static String ECLIPSE_SEP = new StringBuffer().append(IPath.SEPARATOR).toString();

	private static String COMPONENT_SEP = "_";

	public static final int LAUNCHED = -6666;

	private static int counter = 0;

	private static boolean exceptionHappened = false;

	public static void copyFile(String fromName, String toName, String suffix) {
		if (exceptionHappened) {
			return;
		}
		try {
			File fromFile = new File(fromName);
			File toFile = new File(toName + COMPONENT_SEP + suffix);
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
			// Try to send across network
			Intlola.out.write(toName.getBytes());
			Intlola.out.write(0);
			FileInputStream from2 = new FileInputStream(fromFile);
			Intlola.out.write(Long.toString(fromFile.length()).getBytes());
			Intlola.out.write(0);
			while ((bytesRead = from2.read(buffer)) != -1) {
				Intlola.out.write(buffer, 0, bytesRead);
			}
			from2.close();
			Intlola.out.flush();
		} catch (IOException e) {
			MessageDialog.openInformation(null, "IO exception occurred", e.getMessage());
			// MessageDialog.openInformation(null, "IO exception occurred", "FROM: \"" + fromName + "\" TO: \"" + toName + "\" MESSAGE: " + e.getMessage());
			exceptionHappened = true;
		}
	}

	public static void touchFile(String toName, String suffix) {
		if (exceptionHappened) {
			return;
		}
		try {
			File toFile = new File(toName + COMPONENT_SEP + suffix);
			if (toFile.exists()) {
				throw new IOException("file already exists: " + toName);
			}
			FileOutputStream to = new FileOutputStream(toFile);
			to.write(0);
			to.close();
			Intlola.out.write(toName.getBytes());
			Intlola.out.write(0);
			Intlola.out.write(Long.toString(0).getBytes());
			Intlola.out.write(0);
			Intlola.out.flush();
		} catch (IOException e) {
			MessageDialog.openInformation(null, "IO exception occurred", e.getMessage());
			// MessageDialog.openInformation(null, "IO exception occurred", "TO: \"" + toName + "\" MESSAGE: " + e.getMessage());
			exceptionHappened = true;
		}
	}

	public static void copyOrTouch(IResource resource, String storePath, int kind) {
		String l = resource.getLocation().toString();
		String f = resource.getFullPath().toString();
		StringBuffer d = new StringBuffer(storePath);
		d.append("" + DIRECTORY_SEP + f.replace(ECLIPSE_SEP, COMPONENT_SEP));
		d.append("" + COMPONENT_SEP + String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS%1$tL", Calendar.getInstance()));
		d.append("" + COMPONENT_SEP + counter++);
		if (resource.getType() == IResource.FILE) {
			switch (kind) {
			case IResourceDelta.ADDED:
				copyFile(l, d.toString(), "a");
				Intlola.trace("\"" + l + "\" (added)");
				break;
			case IResourceDelta.CHANGED:
				copyFile(l, d.toString(), "c");
				Intlola.trace("\"" + l + "\" (changed)");
				break;
			case IResourceDelta.REMOVED:
				copyFile(l, d.toString(), "r");
				Intlola.trace("\"" + l + "\" (removed)");
				break;
			case LAUNCHED:
				copyFile(l, d.toString(), "l");
				Intlola.trace("\"" + l + "\" (launched)");
				break;
			}
		} else {
			switch (kind) {
			case IResourceDelta.ADDED:
				touchFile(d.toString(), "a");
				Intlola.trace("\"" + l + "\" (added)");
				break;
			case IResourceDelta.CHANGED:
				touchFile(d.toString(), "c");
				Intlola.trace("\"" + l + "\" (changed)");
				break;
			case IResourceDelta.REMOVED:
				touchFile(d.toString(), "r");
				Intlola.trace("\"" + l + "\" (removed)");
				break;
			case LAUNCHED:
				touchFile(d.toString(), "l");
				Intlola.trace("\"" + l + "\" (launched)");
				break;
			}
		}
	}

	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource r = delta.getResource();
		IResource p = r.getProject();
		if (p == null) {
			return true;
		}
		if (!Intlola.getActiveState(p)) {
			return false;
		}
		copyOrTouch(r, Intlola.getStorePath(p), delta.getKind());
		return true;
	}

}
