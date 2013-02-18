package kselection;

import java.util.Arrays;

public class KSelection {

	private class Pair implements Comparable<Pair> {

		private int x, y, index;

		public Pair(int x, int y, int index) {
			this.x = x;
			//this.y = y;
			this.index = index;
		}

		public int getIndex() {
			return 0;//index;
		}

		public int compareTo(Pair p) {
			if (x < p.x) { return -1; }
			else if (x > p.x) { return 1; }
			//else if (y < p.y) { return -1; }
			else if (y > p.y) { return 1; }
			else { return 0; }
		}

		public String toString() {
			StringBuffer b = new StringBuffer();
			//b.append("(" + x + ", " + y + " [" + index + "])");
			return b.toString();
		}
	}

	public int kselect(int k, int[] values) {
		int n = values.length / 2;
		if ((k == 0) || (k > n) || (-k > n)) {
			return 0;
		}
		Pair[] pairs = new Pair[n];
		for (int i = 0; i < n; i++) {
			pairs[i] = new Pair(values[i * 2], values[i * 2 + 1], i + 1);
		}
		//Arrays.sort(pairs);
		if (k > 0) {
			return pairs[k - 1].getIndex();
		}
		else {
			return pairs[n + k].getIndex();
		}
	}
}
