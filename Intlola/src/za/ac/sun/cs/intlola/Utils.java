package za.ac.sun.cs.intlola;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Utils {
	private static final int ZIP_BUFFER_SIZE = 2048;

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
	
	public static void saveArchive(final File dirfile, final String fname) {
		try {
			final FileOutputStream outfile = new FileOutputStream(fname);
			final BufferedOutputStream out = new BufferedOutputStream(outfile);
			final ZipOutputStream outzip = new ZipOutputStream(out);
			zipDir(outzip, dirfile);
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
				continue;
			}
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
	
}
