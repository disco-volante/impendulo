package za.ac.sun.cs.intlola;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import za.ac.sun.cs.intlola.file.IndividualFile;
import za.ac.sun.cs.intlola.file.IntlolaFile;

public class Utils {
	private static final int ZIP_BUFFER_SIZE = 2048;
	public static final String COMPONENT_SEP = "_";
	public static final String NAME_SEP = ".";
	private static final String FORMAT = "%1$tY%1$tm%1$td%1$tH%1$tM%1$tS%1$tL";
	private static final CharSequence SRC = "src";
	private static final CharSequence BIN = "bin";
	private static final String JAVA = ".java";
	private static final String CLASS = ".class";
	public static final String STORE_PATH = Intlola.getDefault()
			.getStateLocation().toOSString();
	public static final String FILE_DIR = STORE_PATH + File.separator + "tmp";
	static{
		File f = new File(FILE_DIR);
		f.mkdirs();
	}

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

	public static void saveArchive(final String location, final String fname) {
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

	private static void zipDir(final ZipOutputStream outzip, final File dirfile) {
		for (final File file : dirfile.listFiles()) {
			if (file.isDirectory()) {
				zipDir(outzip, file);
			} else {
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
			}
			//file.delete();
		}
	}

	public static void saveString(String string, String fname) {
		try {
			FileOutputStream outfile = new FileOutputStream(fname);
			final BufferedOutputStream out = new BufferedOutputStream(outfile);
			out.write(string.getBytes());
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

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
			System.out.println(name);
			if (name.contains(NAME_SEP)) {
				hasContents = true;
			}
		}
		return new IndividualFile(encodedName, name, pkg, mod, num, time,
				hasContents);
	}

	public static String encodeName(String path, char kindSuffix, int count) {
		final StringBuffer d = new StringBuffer();
		String[] args = path.split(File.separator);
		String pkg = getPackage(args, args.length, COMPONENT_SEP);
		if (pkg.length() > 0) {
			d.append(pkg);
			d.append(COMPONENT_SEP);
		}
		String name = getFileName(args);
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

	private static String getFileName(String[] args) {
		String name = "";
		for (String arg : args) {
			if (isFileName(arg)) {
				name = arg;
				break;
			}
		}
		return name;
	}

	public static String getPackage(final String[] holder, final int len,
			String sep) {
		String pkg = "";
		boolean start = false;
		for (int i = 0; i < len; i++) {
			if (isFileName(holder[i])) {
				return pkg;
			}
			if (start) {
				pkg += holder[i];
				if (i < len - 1) {
					pkg += sep;
				}
			}
			if (holder[i].equals(SRC) || holder[i].equals(BIN)) {
				start = true;
			}
		}
		if (pkg.length() > 0) {
			pkg = pkg.substring(0, pkg.length() - 1);
		}
		return pkg;
	}

	private static boolean isFileName(String arg) {
		return arg.endsWith(JAVA) || arg.endsWith(CLASS);
	}

}
