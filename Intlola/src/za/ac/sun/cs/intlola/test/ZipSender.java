package za.ac.sun.cs.intlola.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import za.ac.sun.cs.intlola.IntlolaSender;
import za.ac.sun.cs.intlola.SendMode;
import za.ac.sun.cs.intlola.preferences.PreferenceConstants;

public class ZipSender {
	static class SendThread implements Runnable {
		private final String user;
		private final String passwd;
		private final String zip;

		public SendThread(final String user, final String passwd,
				final String zip) {
			this.user = user;
			this.passwd = passwd;
			this.zip = zip;
		}

		@Override
		public void run() {
			final IntlolaSender sender = new IntlolaSender(user, "Data",
					SendMode.ONSTOP, PreferenceConstants.LOCAL_ADDRESS,
					PreferenceConstants.PORT);
			if (sender.login(passwd)) {
				sender.send(SendMode.ONSTOP, zip);
			}
		}

	}

	private static Random rand = new Random();
	private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final int LENGTH = 30;
	private static HashMap<String, Boolean> strings = new HashMap<String, Boolean>();

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
		ZipSender.sendZip("data/data.zip");
	}

	public static String randString() {
		String gen = "";
		ZipSender.strings.put(gen, true);
		while (ZipSender.strings.containsKey(gen)) {
			final char[] text = new char[ZipSender.LENGTH];
			for (int i = 0; i < ZipSender.LENGTH; i++) {
				text[i] = ZipSender.CHARACTERS.charAt(ZipSender.rand
						.nextInt(ZipSender.CHARACTERS.length()));
			}
			gen = new String(text);
		}
		ZipSender.strings.put(gen, true);
		return gen;
	}

	private static void sendZip(String zip) {
		final Map<String, String> users = ZipSender.getUsers("users");
		final ArrayList<Thread> threads = new ArrayList<Thread>();
		for (final Map.Entry<String, String> e : users.entrySet()) {
			threads.add(new Thread(new SendThread(e.getKey(), e.getValue(),
					zip)));
		}
		for (final Thread th : threads) {
			th.start();
		}
	}

}
