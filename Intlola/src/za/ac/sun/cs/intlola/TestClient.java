package za.ac.sun.cs.intlola;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import za.ac.sun.cs.intlola.preferences.PreferenceConstants;

public class TestClient {
	static class SendThread implements Runnable {
		private final String pword;
		private final String user;
		private final ArrayList<String> files;

		public SendThread(final String user, final String pword,
				final ArrayList<String> files) {
			this.user = user;
			this.pword = pword;
			this.files = files;
		}

		@Override
		public void run() {
			final IntlolaSender sender = new IntlolaSender(user, pword, "Data",
					SendMode.ONSAVE, PreferenceConstants.LOCAL_ADDRESS,
					PreferenceConstants.PORT);
			for (final String file : files) {
				sender.send(SendMode.ONSAVE, file);
			}
			sender.send(SendMode.ONSTOP, TestClient.randString() + ".zip");
		}

	}

	private static Random rand = new Random();
	private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final int LENGTH = 30;
	private static HashMap<String, Boolean> strings = new HashMap<String, Boolean>();

	public static ArrayList<String> getFiles(final File dir) {
		final ArrayList<String> files = new ArrayList<String>();
		for (final File f : dir.listFiles()) {
			if (f.isFile()) {
				files.add(f.getAbsolutePath());
			}
		}
		return files;
	}

	public static Map<String, String> getUsers(final String fname) {
		final Map<String, String> users = new HashMap<String, String>();

		try {
			final BufferedReader reader = new BufferedReader(new FileReader(
					new File(fname)));
			String line;
			while ((line = reader.readLine()) != null) {
				final String[] vals = line.split(":");
				users.put(vals[0], vals[1]);
			}
			reader.close();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return users;
	}

	public static void main(final String argv[]) {
		IntlolaSender sender = new IntlolaSender("", "", "Data", SendMode.ONSAVE,
				PreferenceConstants.LOCAL_ADDRESS, PreferenceConstants.PORT);
		sender.sendTests("TESTING.zip");
		/*
		 * final Map<String, String> users = TestClient.getUsers("users"); final
		 * ArrayList<String> files = TestClient.getFiles(new File("data"));
		 * final ArrayList<Thread> threads = new ArrayList<Thread>(); for (final
		 * Map.Entry<String, String> e : users.entrySet()) { threads.add(new
		 * Thread(new SendThread(e.getKey(), e.getValue(), files))); } for
		 * (final Thread th : threads) { th.start(); }
		 */
	}

	public static String randString() {
		String gen = "";
		TestClient.strings.put(gen, true);
		while (TestClient.strings.containsKey(gen)) {
			final char[] text = new char[TestClient.LENGTH];
			for (int i = 0; i < TestClient.LENGTH; i++) {
				text[i] = TestClient.CHARACTERS.charAt(TestClient.rand
						.nextInt(TestClient.CHARACTERS.length()));
			}
			gen = new String(text);
		}
		TestClient.strings.put(gen, true);
		return gen;
	}
}
