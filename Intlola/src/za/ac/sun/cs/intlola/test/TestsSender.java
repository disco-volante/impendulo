package za.ac.sun.cs.intlola.test;

import za.ac.sun.cs.intlola.file.TestFile;
import za.ac.sun.cs.intlola.preferences.PreferenceConstants;
import za.ac.sun.cs.intlola.processing.IntlolaError;
import za.ac.sun.cs.intlola.processing.IntlolaMode;
import za.ac.sun.cs.intlola.processing.Processor;

public class TestsSender {

	public static void main(final String argv[]) {
		TestsSender.sendTests("test.zip");
	}

	private static void sendTests(final String name) {
		final Processor sender = new Processor("username", "Triangle",
				IntlolaMode.ARCHIVE_TEST, PreferenceConstants.REMOTE_ADDRESS,
				PreferenceConstants.PORT);
		IntlolaError err = sender.login(sender.getUsername(), "password",
				sender.getProject(), sender.getMode(), sender.getAddress(),
				sender.getPort());
		if (err.equals(IntlolaError.SUCCESS)) {
			sender.sendFile(new TestFile(name));
			sender.logout();
		}
	}
}
