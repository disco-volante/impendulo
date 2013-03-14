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
		private String pword;
		private String user;
		private ArrayList<String> files;

		public SendThread(String user, String pword, ArrayList<String> files) {
			this.user = user;
			this.pword = pword;
			this.files = files;
		}

		@Override
		public void run() {
			IntlolaSender sender = new IntlolaSender(user, pword, "Data",
					SendMode.ONSAVE, PreferenceConstants.REMOTE_ADDRESS,
					PreferenceConstants.PORT);
			for (String file : files) {
				sender.send(SendMode.ONSAVE, file);
			}
			sender.send(SendMode.ONSTOP, randString() + ".zip");
		}

	}

	private static Random rand = new Random();
	private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final int LENGTH = 30;
	private static HashMap<String, Boolean> strings = new HashMap<String, Boolean>();

	public static String randString() {
		String gen = "";
		strings.put(gen, true);
		while (strings.containsKey(gen)) {
			char[] text = new char[LENGTH];
			for (int i = 0; i < LENGTH; i++) {
				text[i] = CHARACTERS.charAt(rand.nextInt(CHARACTERS.length()));
			}
			gen = new String(text);
		}
		strings.put(gen, true);
		return gen;
	}

	public static Map<String, String> getUsers(String fname) {
		Map<String, String> users = new HashMap<String, String>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					fname)));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] vals = line.split(":");
				users.put(vals[0], vals[1]);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return users;
	}
	
	public static ArrayList<String> getFiles(File dir){
		ArrayList<String> files = new ArrayList<String>();
		for(File f : dir.listFiles()){
			if(f.isFile()){
				files.add(f.getAbsolutePath());
			}
		}
		return files;
	}

	public static void main(String argv[]) {
		Map<String, String> users = getUsers("users");
		ArrayList<String> files = getFiles(new File("data"));
		ArrayList<Thread> threads = new ArrayList<Thread>();
		for (Map.Entry<String, String> e : users.entrySet()) {
			threads.add(new Thread(new SendThread(e.getKey(), e.getValue(), files)));
		}
		for (Thread th : threads) {
			th.start();
		}
	}
}
