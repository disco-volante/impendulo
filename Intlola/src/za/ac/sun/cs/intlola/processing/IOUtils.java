package za.ac.sun.cs.intlola.processing;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import za.ac.sun.cs.intlola.file.Const;
import za.ac.sun.cs.intlola.file.IndividualFile;
import za.ac.sun.cs.intlola.file.IntlolaFile;

import com.google.gson.JsonObject;

public class IOUtils {
	private static final String BIN = "bin";
	private static final String CLASS = ".class";
	public static final String COMPONENT_SEP = "_";
	private static final String JAVA = ".java";
	public static final String NAME_SEP = ".";
	private static final String SRC = "src";
	public static final int BUFFER_SIZE = 4096;
	public static final char LAUNCH = 'l';
	public static final char SAVE = 's';
	public static final char INVALID = 'i';
	private static final byte NOTHING = 0;
	public static final int LAUNCHED = -6666;

	public static String read(InputStream in) throws IOException {
		final byte[] buffer = new byte[BUFFER_SIZE];
		StringBuilder read = new StringBuilder();
		int count = 0;
		while ((count = in.read(buffer)) != -1) {
			byte[] subBuffer = Arrays.copyOfRange(buffer, count
					- Const.EOT_B.length, count);
			if (Arrays.equals(Const.EOT_B, subBuffer)) {
				read.append(new String(buffer, 0, count - Const.EOT_B.length));
				break;
			} else {
				String current = new String(buffer, 0, count);
				read.append(current);
			}
		}
		return read.toString();
	}

	public static void writeJson(OutputStream out, JsonObject data)
			throws IOException {
		out.write(data.toString().getBytes());
		out.write(Const.EOT_B);
		out.flush();
	}

	/**
	 * Copies the contents of a file to a new file location.
	 * 
	 * @param fromName
	 *            The file to be copied.
	 * @param toName
	 *            The path of the new file.
	 * @throws IOException
	 */
	public static void copy(final String fromName, final String toName)
			throws IOException {
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
		final byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead;
		while ((bytesRead = from.read(buffer)) != -1) {
			to.write(buffer, 0, bytesRead);
		}
		from.close();
		to.close();
	}

	/**
	 * Creates a zip archive from all the files found in the specified location.
	 * 
	 * @param location
	 *            The location of the files to be zipped.
	 * @param fname
	 *            The absolute file name of the zip file to be created.
	 * @throws IOException
	 */
	public static void createZip(final String location, final String fname)
			throws IOException {
		final File dir = new File(location);
		final FileOutputStream outfile = new FileOutputStream(fname);
		final BufferedOutputStream out = new BufferedOutputStream(outfile);
		final ZipOutputStream outzip = new ZipOutputStream(out);
		zipDir(outzip, dir);
		outzip.close();
		out.flush();
		out.close();
	}

	/**
	 * Retrieve file metadata encoded in a file name. These file names must have
	 * the format:
	 * <code>[[{package descriptor}"_"]*{file name}"_"]{time in nanoseconds}"_"{file number in current submission}"_"{modification char}</code>
	 * Where values between '[ ]' are optional, '*' indicates 0 to many, values
	 * inside '""' are literals and values inside '{ }' describe the contents at
	 * that position.
	 * 
	 * @param encodedName
	 *            The name from which to retrieve the metadata.
	 * @return A new {@link IntlolaFile} with containing the retrieved metadata.
	 */
	public static IntlolaFile decodeName(final String encodedName) {
		String[] elems = encodedName.split(File.separator);
		final String fdata = elems[elems.length - 1];
		elems = fdata.split(COMPONENT_SEP);
		final char mod = elems[elems.length - 1].charAt(0);
		if (!isKindSuffix(mod)) {
			throw new InvalidParameterException("Unknown kind: " + mod);
		}
		final int num = Integer.parseInt(elems[elems.length - 2]);
		final long time = Long.parseLong(elems[elems.length - 3]);
		String name = getFileName(elems);
		boolean sendContents = name.endsWith(Const.JAVA);
		String pkg = getPackage(elems, NAME_SEP);
		return new IndividualFile(encodedName, name, pkg, time, num, mod,
				sendContents);
	}

	public static void delete(final String filename) {
		new File(filename).deleteOnExit();
	}

	/**
	 * Stores file metadata in its name in the format described by
	 * {@link #decodeName(String)}.
	 * 
	 * @param path
	 *            The actual name of the file.
	 * @param kindSuffix
	 *            The type of file modification.
	 * @param count
	 *            The number of the file in the current recording.
	 * @return A name containing file metadata.
	 */
	public static String encodeName(final String path, final long time,
			final int count, final char kindSuffix) {
		final StringBuffer d = new StringBuffer();
		final String[] args = path.split(File.separator);
		final String pkg = getPackage(args, COMPONENT_SEP);
		if (pkg.length() > 0) {
			d.append(pkg);
			d.append(COMPONENT_SEP);
		}
		final String name = getFileName(args);
		if (name.length() > 0) {
			d.append(name);
			d.append(COMPONENT_SEP);
		}
		d.append(time);
		d.append(COMPONENT_SEP);
		d.append(count);
		d.append(COMPONENT_SEP);
		d.append(kindSuffix);
		return d.toString();
	}

	/**
	 * Retrieves a file's name from an array containing other data such as the
	 * path to the file or file metadata.
	 * 
	 * @param args
	 *            Array containing a file name among other information.
	 * @return A file name.
	 */
	public static String getFileName(final String[] args) {
		String name = "";
		for (final String arg : args) {
			if (isFileName(arg)) {
				name = arg;
				break;
			}
		}
		return name;
	}

	public static boolean isKindSuffix(char modChar) {
		return modChar == LAUNCH || modChar == SAVE;
	}

	public static char getKind(final int kind) {
		char kindSuffix = ' ';
		switch (kind) {
		case LAUNCHED:
			kindSuffix = LAUNCH;
			break;
		case IResourceDelta.CHANGED:
			kindSuffix = SAVE;
			break;
		default:
			kindSuffix = INVALID;
		}
		return kindSuffix;
	}

	public static boolean shouldSend(char kindSuffix, String path) {
		if (kindSuffix == LAUNCH) {
			return true;
		}
		if (!path.endsWith(Const.JAVA)) {
			return false;
		}
		if (kindSuffix == INVALID) {
			return false;
		}
		return true;
	}

	/**
	 * Retrieves a file's package from an array containing other data such as
	 * the path to the file or file metadata.
	 * 
	 * @param args
	 *            Array containing a file's package among other information.
	 * @param sep
	 *            Separator to place between package components.
	 * @return A file's package.
	 */
	public static String getPackage(final String[] args, final String sep) {
		String pkg = "";
		boolean start = false;
		for (int i = 0; i < args.length; i++) {
			if (isFileName(args[i])) {
				break;
			}
			if (start) {
				pkg += args[i] + sep;
			}
			if (isOutFolder(args[i])) {
				start = true;
			}
		}
		if (pkg.endsWith(sep)) {
			pkg = pkg.substring(0, pkg.length() - 1);
		}
		return pkg;
	}

	/**
	 * Determines whether a given string is a java file (source or compiled).
	 * 
	 * @param arg
	 *            The string to be checked.
	 * @return <code>true</code> if it is, <code>false</code> if not.
	 */
	public static boolean isFileName(final String arg) {
		return arg.endsWith(JAVA) || arg.endsWith(CLASS);
	}

	private static boolean isIntlolaFile(final String fname) {
		if (fname.length() > 1 && fname.charAt(fname.length() - 2) == '_') {
			final char modChar = fname.charAt(fname.length() - 1);
			return isKindSuffix(modChar);
		}
		return false;
	}

	/**
	 * Determines whether a given string is an output folder (src or bin).
	 * 
	 * @param arg
	 *            The string to be checked.
	 * @return <code>true</code> if it is, <code>false</code> if not.
	 */
	private static boolean isOutFolder(final String arg) {
		return arg.equals(SRC) || arg.equals(BIN);
	}

	/**
	 * Creates a new empty file.
	 * 
	 * @param toName
	 *            The path of the empty file.
	 * @throws IOException
	 */
	public static void touch(final String toName) throws IOException {
		final File toFile = new File(toName);
		if (toFile.exists()) {
			throw new IOException("File already exists: " + toName);
		}
		final FileOutputStream to = new FileOutputStream(toFile);
		to.write(NOTHING);
		to.close();
	}

	/**
	 * Recursively zips a directory. Iterates through the current directory
	 * adding files to zip file if they are regular files. If the file is a
	 * directory, {@link #zipDir(ZipOutputStream, File)} is called on it.
	 * 
	 * @param outzip
	 *            The zip file.
	 * @param dirfile
	 *            The current directory.
	 * @throws IOException
	 */
	public static void zipDir(final ZipOutputStream outzip, final File dirfile)
			throws IOException {
		for (final File file : dirfile.listFiles()) {
			if (file.isDirectory()) {
				zipDir(outzip, file);
			} else if (isIntlolaFile(file.toString())) {
				final byte[] data = new byte[BUFFER_SIZE];
				final FileInputStream origin = new FileInputStream(file);
				outzip.putNextEntry(new ZipEntry(file.getName()));
				int count;
				while ((count = origin.read(data)) != -1) {
					outzip.write(data, 0, count);
				}
				outzip.closeEntry();
				origin.close();
				file.deleteOnExit();
			}
		}
	}

	public static String getFilename(Shell shell) {
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFileName("intlola.zip");
		String filename = null;
		boolean isDone = false;
		while (!isDone) {
			filename = dialog.open();
			if (filename == null) {
				isDone = true;
			} else {
				File file = new File(filename);
				if (file.exists()) {
					if (file.isFile()) {
						isDone = MessageDialog
								.openQuestion(
										shell,
										"Replace?",
										"File \""
												+ filename
												+ "\" already exists.  Do you want to replace it?");
					} else {
						MessageDialog
								.openInformation(
										shell,
										"Irregular file",
										"File \""
												+ filename
												+ "\" exists, but it is not a regular file.  Please choose another file.");
					}
				} else {
					isDone = true;
				}
			}
		}
		return filename;
	}

	public static Object deserialize(String serilizedObject)
			throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(serilizedObject);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Object ret = ois.readObject();
		fis.close();
		return ret;
	}

	public static void serialize(String fname, Object serializableObject)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(fname);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(serializableObject);
		fos.close();
	}

	public static String joinPath(String path1, String path2) {
		File file1 = new File(path1);
		File file2 = new File(file1, path2);
		return file2.getPath();
	}

}
