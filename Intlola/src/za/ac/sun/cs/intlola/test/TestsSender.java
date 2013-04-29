package za.ac.sun.cs.intlola.test;

import za.ac.sun.cs.intlola.IntlolaProcessor;
import za.ac.sun.cs.intlola.IntlolaMode;
import za.ac.sun.cs.intlola.file.TestFile;
import za.ac.sun.cs.intlola.preferences.PreferenceConstants;

public class TestsSender {

	public static void main(final String argv[]) {
		TestsSender.sendTests("test.zip");
	}

	private static void sendTests(final String name) {
		final IntlolaProcessor sender = new IntlolaProcessor("pjordaan", "Triangle",
				IntlolaMode.ARCHIVE_TEST, PreferenceConstants.REMOTE_ADDRESS,
				PreferenceConstants.PORT);
		if (sender.init()) {
			sender.login("pjordaan", "1brandwag");
			if (sender.loggedIn()) {
				sender.sendFile(new TestFile(name));
				sender.logout();
			}
		}
	}
}
