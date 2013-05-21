package za.ac.sun.cs.intlola.test.sending;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import za.ac.sun.cs.intlola.Intlola;
import za.ac.sun.cs.intlola.file.Const;
import za.ac.sun.cs.intlola.file.FileUtils;
import za.ac.sun.cs.intlola.file.TestFile;
import za.ac.sun.cs.intlola.preferences.PreferenceConstants;

public class TestsSender {

	public static void main(final String argv[]) {
		TestsSender.sendTests("Tests.zip", "Data.zip");
	}

	private static void sendTests(final String tests, final String data) {
		final byte[] readBuffer = new byte[FileUtils.BUFFER_SIZE];
		final byte[] writeBuffer = new byte[FileUtils.BUFFER_SIZE];
		InetSocketAddress address = new InetSocketAddress(
				PreferenceConstants.LOCAL_ADDRESS, 8000);
		Socket sock = new Socket();
		try {
			sock.connect(address, 5000);
			OutputStream snd = sock.getOutputStream();
			InputStream rcv = sock.getInputStream();
			TestFile file = new TestFile(tests, "za.ac.sun.cs.Triangle",
					new String[] { "EasyTests.java", "AllTests.java" });
			snd.write(file.toJSON().toString().getBytes());
			snd.write(Const.EOF);
			System.out.println("sent json");
			rcv.read(readBuffer);
			String received = new String(readBuffer);
			System.out.println(received);
			if (received.startsWith(Const.OK)) {
				FileInputStream fis = new FileInputStream(tests);
				int count;
				while ((count = fis.read(writeBuffer)) >= 0) {
					snd.write(writeBuffer, 0, count);
				}
				fis.close();
				snd.write(Const.EOF);
				snd.flush();
				System.out.println("sent file");
				rcv.read(readBuffer);
				received = new String(readBuffer);
				if (!received.startsWith(Const.OK)) {
					Intlola.log(null, "Received invalid reply: " + received);
				}
				fis = new FileInputStream(data);
				while ((count = fis.read(writeBuffer)) >= 0) {
					snd.write(writeBuffer, 0, count);
				}
				fis.close();
				snd.write(Const.EOF);
				snd.flush();
				System.out.println("sent data");
				rcv.read(readBuffer);
				received = new String(readBuffer);
				if (!received.startsWith(Const.OK)) {
					Intlola.log(null, "Received invalid reply: " + received);
				}

			}
		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
