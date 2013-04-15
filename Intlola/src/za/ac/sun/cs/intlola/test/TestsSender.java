package za.ac.sun.cs.intlola.test;

import za.ac.sun.cs.intlola.IntlolaSender;
import za.ac.sun.cs.intlola.SendMode;
import za.ac.sun.cs.intlola.file.TestFile;
import za.ac.sun.cs.intlola.preferences.PreferenceConstants;

public class TestsSender {

	public static void main(final String argv[]) {
		TestsSender.sendTests("TESTING.zip");
	}

	private static void sendTests(final String name) {
		final IntlolaSender sender = new IntlolaSender("username", "Data",
				SendMode.TEST, PreferenceConstants.LOCAL_ADDRESS,
				PreferenceConstants.PORT);
		if (sender.openConnection()) {
			sender.login("username", "password");
			if (sender.loggedIn()) {
				sender.sendFile(new TestFile(name));
				sender.logout();
			}
		}
	}
}
