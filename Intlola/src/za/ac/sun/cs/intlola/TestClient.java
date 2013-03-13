package za.ac.sun.cs.intlola;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class TestClient {
	static class SendThread implements Runnable {
		private String pword;
		private String user;

		public SendThread(String user, String pword) {
			this.user = user;
			this.pword = pword;
		}

		@Override
		public void run() {
			IntlolaSender sender = new IntlolaSender(user,
					pword, "Default", SendMode.ONSAVE);
			for (int i = 0; i < 20; i++) {
				sender.send(SendMode.ONSAVE,"plugin.xml");
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
	
	public static void main(String argv[]) {
		String[][] users = new String[100][2];
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("users")));
			String line;
			int i = 0;
			while((line = reader.readLine()) != null){
				String[] vals = line.split(":");
				users[i][0] = vals[0];
				users[i++][1] = vals[1];
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayList<Thread> threads = new ArrayList<Thread>();
		for (int j = 0; j < 100; j++) {
			threads.add(new Thread(new SendThread(users[j][0], users[j][1])));
		}
		for (Thread th : threads) {
			th.start();
		}
	}
}
