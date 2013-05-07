package za.ac.sun.cs.intlola.test;

import za.ac.sun.cs.intlola.IntlolaError;
import za.ac.sun.cs.intlola.IntlolaMode;
import za.ac.sun.cs.intlola.file.TestFile;
import za.ac.sun.cs.intlola.preferences.PreferenceConstants;
import za.ac.sun.cs.intlola.processing.Processor;

public class TestsSender {

	public static void main(final String argv[]) {
		TestsSender.sendTests("test.zip");
	}

	private static void sendTests(final String name) {
		final Processor sender = new Processor("pjordaan",
				"Triangle", IntlolaMode.ARCHIVE_TEST,
				PreferenceConstants.REMOTE_ADDRESS, PreferenceConstants.PORT);
		if (sender.init()) {
			IntlolaError err = sender.login(sender.getUsername(), "1brandwag",
					sender.getProject(), sender.getMode(), sender.getAddress(),
					sender.getPort());
			if (err.equals(IntlolaError.SUCCESS)) {
				sender.sendFile(new TestFile(name));
				sender.logout();
			}
		}
	}
}
