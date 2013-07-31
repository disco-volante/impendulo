package za.ac.sun.cs.intlola.processing;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import za.ac.sun.cs.intlola.file.Const;
import za.ac.sun.cs.intlola.file.FileUtils;
import za.ac.sun.cs.intlola.file.IntlolaFile;

import com.google.gson.JsonObject;

public class FileSender implements Runnable {
	private final IntlolaFile file;
	private final InputStream rcv;
	private final OutputStream snd;
	private final Socket sock;

	FileSender(final IntlolaFile file, final Socket sock,
			final OutputStream snd, final InputStream rcv) {
		this.file = file;
		this.sock = sock;
		this.snd = snd;
		this.rcv = rcv;
	}

	private void closeConnection() throws IOException {
		if (sock != null) {
			sock.close();
		}
	}

	@Override
	public void run() {
		final byte[] readBuffer = new byte[FileUtils.BUFFER_SIZE];
		final byte[] writeBuffer = new byte[FileUtils.BUFFER_SIZE];
		FileInputStream fis = null;
		boolean ok = true;
		try {
			final JsonObject fjson = file.toJSON();
			fjson.addProperty(Const.REQ, Const.SEND);
			snd.write(fjson.toString().getBytes());
			snd.write(Const.EOF);
			snd.flush();
			int count = rcv.read(readBuffer);
			String received = new String(readBuffer, 0, count);
			if (received.startsWith(Const.OK)) {
				if (file.hasContents()) {
					try {
						fis = new FileInputStream(file.getPath());
						while ((count = fis.read(writeBuffer)) >= 0) {
							snd.write(writeBuffer, 0, count);
						}
					} catch (FileNotFoundException fe) {
						System.err.println(fe.getMessage());
					}
				}
				snd.write(Const.EOF);
				snd.flush();
				count = rcv.read(readBuffer);
				received = new String(readBuffer, 0, count);
				if (!received.startsWith(Const.OK)) {
					System.err.println("Received invalid reply: " + received);
				}
			}

		} catch (final IOException e) {
			System.err.println(e.getMessage());
			ok = false;
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (final IOException e) {
				System.err.println(e.getMessage());
			}
			if (!ok) {
				try {
					closeConnection();
				} catch (final IOException e) {
					System.err.println(e.getMessage());
				}
			}

		}

	}
}
