package za.ac.sun.cs.intlola;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class TestClient {
	static class SendThread implements Runnable {
		@Override
		public void run() {
			IntlolaSender sender = new IntlolaSender(randString(),
					randString(), "Default", SendMode.ONSAVE);
			for (int i = 0; i < 20; i++) {
				sender.send(SendMode.ONSAVE,"plugin.xml", randString());
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
		ArrayList<Thread> threads = new ArrayList<Thread>();
		for (int i = 0; i < 10; i++) {
			threads.add(new Thread(new SendThread()));
		}
		for (Thread th : threads) {
			th.start();
		}
	}
}
