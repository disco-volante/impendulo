package za.ac.sun.cs.intlola.processing;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import za.ac.sun.cs.intlola.Intlola;
import za.ac.sun.cs.intlola.file.Const;
import za.ac.sun.cs.intlola.file.IntlolaFile;

import com.google.gson.JsonObject;

public class FileSender implements Runnable {
	private IntlolaFile file;
	private OutputStream snd;
	private InputStream rcv;

	FileSender(IntlolaFile file, OutputStream snd, InputStream rcv) {
		this.file = file;
		this.snd = snd;
		this.rcv = rcv;
	}

	public void run() {
		final byte[] readBuffer = new byte[2048];
		final byte[] writeBuffer = new byte[2048];
		FileInputStream fis = null;
		try {
			JsonObject fjson = file.toJSON();
			fjson.addProperty(Const.REQ, Const.SEND);
			snd.write(fjson.toString().getBytes());
			snd.flush();
			rcv.read(readBuffer);
			String received = new String(readBuffer);
			if (received.startsWith(Const.OK)) {
				if (file.hasContents()) {
					int count;
					fis = new FileInputStream(file.getPath());
					while ((count = fis.read(writeBuffer)) >= 0) {
						snd.write(writeBuffer, 0, count);
					}
				}
				snd.write(Const.EOF.getBytes());
				snd.flush();
				rcv.read(readBuffer);
				received = new String(readBuffer);
				if (!received.startsWith(Const.OK)) {
					Intlola.log(null, "Send error:" + received);
				}
			}

		} catch (final IOException e) {
			Intlola.log(e, "Send error");
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (final IOException e) {
				Intlola.log(e, "Close error");
			}

		}

	}
}
