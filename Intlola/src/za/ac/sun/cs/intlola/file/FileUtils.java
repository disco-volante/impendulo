package za.ac.sun.cs.intlola.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.resources.IResourceDelta;

import za.ac.sun.cs.intlola.Intlola;

public class FileUtils {
	private static final CharSequence	BIN				= "bin";
	private static final String			CLASS			= ".class";
	public static final String			COMPONENT_SEP	= "_";
	private static final String			FORMAT			= "%1$tY%1$tm%1$td%1$tH%1$tM%1$tS%1$tL";
	private static final String			JAVA			= ".java";
	public static final String			NAME_SEP		= ".";
	private static final CharSequence	SRC				= "src";
	private static final int			ZIP_BUFFER_SIZE	= 2048;

	/**
	 * Copies the contents of a file to a new file location.
	 * 
	 * @param fromName
	 *            The file to be copied.
	 * @param toName
	 *            The path of the new file.
	 */
	public static void copy(final String fromName, final String toName) {
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

	/**
	 * Creates a zip archive from all the files found in the specified location.
	 * 
	 * @param location
	 *            The location of the files to be zipped.
	 * @param fname
	 *            The absolute file name of the zip file to be created.
	 */
	public static void createZip(final String location, final String fname) {
		try {
			final File dir = new File(location);
			final FileOutputStream outfile = new FileOutputStream(fname);
			final BufferedOutputStream out = new BufferedOutputStream(outfile);
			final ZipOutputStream outzip = new ZipOutputStream(out);
			zipDir(outzip, dir);
			outzip.close();
			out.flush();
			out.close();
		} catch (final FileNotFoundException e) {
			Intlola.log(e);
		} catch (final IOException e) {
			Intlola.log(e);
		}

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
		final int num = Integer.parseInt(elems[elems.length - 2]);
		final long time = Long.parseLong(elems[elems.length - 3]);
		String name = null, pkg = null;
		boolean hasContents = false;
		if (elems.length > 3) {
			name = elems[elems.length - 4];
			pkg = getPackage(elems, elems.length - 4, NAME_SEP);
			if (name.contains(NAME_SEP)) {
				hasContents = true;
			}
		}
		return new IndividualFile(encodedName, name, pkg, mod, num, time,
				hasContents);
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
	public static String encodeName(final String path, final char kindSuffix,
			final int count) {
		final StringBuffer d = new StringBuffer();
		final String[] args = path.split(File.separator);
		final String pkg = getPackage(args, args.length, COMPONENT_SEP);
		if (pkg.length() > 0) {
			d.append(pkg);
			d.append(COMPONENT_SEP);
		}
		final String name = getFileName(args);
		if (name.length() > 0) {
			d.append(name);
			d.append(COMPONENT_SEP);
		}
		d.append(String.format(FORMAT, Calendar.getInstance()));
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
	private static String getFileName(final String[] args) {
		String name = "";
		for (final String arg : args) {
			if (isFileName(arg)) {
				name = arg;
				break;
			}
		}
		return name;
	}

	public static char getKind(final int kind) {
		char kindSuffix = ' ';
		switch (kind) {
			case IResourceDelta.ADDED:
				kindSuffix = 'a';
				break;
			case IResourceDelta.REMOVED:
				kindSuffix = 'r';
				break;
			case Intlola.LAUNCHED:
				kindSuffix = 'l';
				break;
			case IResourceDelta.MOVED_FROM:
				kindSuffix = 'f';
				break;
			case IResourceDelta.MOVED_TO:
				kindSuffix = 't';
				break;
			case IResourceDelta.CHANGED:
				kindSuffix = 'c';
				break;
			default:
				throw new InvalidParameterException();
		}
		return kindSuffix;
	}

	/**
	 * Retrieves a file's package from an array containing other data such as
	 * the path to the file or file metadata.
	 * 
	 * @param args
	 *            Array containing a file's package among other information.
	 * @param len
	 * @param sep
	 *            Seperator to place between package components.
	 * @return A file's package.
	 */
	public static String getPackage(final String[] args, final int len,
			final String sep) {
		String pkg = "";
		boolean start = false;
		for (int i = 0; i < len; i++) {
			if (isFileName(args[i])) {
				return pkg;
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
	private static boolean isFileName(final String arg) {
		return arg.endsWith(JAVA) || arg.endsWith(CLASS);
	}

	private static boolean isIntlolaFile(final String fname) {
		if (fname.charAt(fname.length() - 2) == '_') {
			final char modChar = fname.charAt(fname.length() - 1);
			return modChar == 'a' || modChar == 'c' || modChar == 'r'
					|| modChar == 'l';
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
	 * Saves a string to a file specified by <code>fname</code>.
	 * 
	 * @param string
	 *            The string to be saved.
	 * @param fname
	 *            The name of the file it is to be saved in.
	 */
	public static void saveString(final String string, final String fname) {
		try {
			final FileOutputStream outfile = new FileOutputStream(fname);
			final BufferedOutputStream out = new BufferedOutputStream(outfile);
			out.write(string.getBytes());
			out.flush();
			out.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Creates a new empty file.
	 * 
	 * @param toName
	 *            The path of the empty file.
	 */
	public static void touch(final String toName) {
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

	/**
	 * Recursively zips a directory. Iterates through the current directory
	 * adding files to zip file if they are regular files. If the file is a
	 * directory, {@link #zipDir(ZipOutputStream, File)} is called on it.
	 * 
	 * @param outzip
	 *            The zip file.
	 * @param dirfile
	 *            The current directory.
	 */
	private static void zipDir(final ZipOutputStream outzip, final File dirfile) {
		for (final File file : dirfile.listFiles()) {
			if (file.isDirectory()) {
				zipDir(outzip, file);
			} else if (isIntlolaFile(file.toString())) {
				try {
					final byte[] data = new byte[ZIP_BUFFER_SIZE];
					final FileInputStream origin = new FileInputStream(file);
					outzip.putNextEntry(new ZipEntry(file.getName()));
					int count;
					while ((count = origin.read(data, 0, ZIP_BUFFER_SIZE)) != -1) {
						outzip.write(data, 0, count);
					}
					outzip.closeEntry();
					origin.close();
				} catch (final IOException e) {
					Intlola.log(e);
				}
				file.deleteOnExit();
			}
		}
	}

}
