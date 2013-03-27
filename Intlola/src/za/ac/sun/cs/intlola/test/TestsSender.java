package za.ac.sun.cs.intlola.test;

import za.ac.sun.cs.intlola.IntlolaSender;
import za.ac.sun.cs.intlola.SendMode;
import za.ac.sun.cs.intlola.preferences.PreferenceConstants;

public class TestsSender {

	public static void main(final String argv[]) {
		TestsSender.sendTests("TESTING.zip");
	}	
	private static void sendTests(String name) {
		final IntlolaSender sender = new IntlolaSender("", "Data",
				SendMode.ONSAVE, PreferenceConstants.LOCAL_ADDRESS,
				PreferenceConstants.PORT);
		sender.sendTests(name);
	}
}
