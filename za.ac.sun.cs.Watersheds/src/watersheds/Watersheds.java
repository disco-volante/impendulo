package watersheds;

public class Watersheds {

	private char d = 'a';

	private int h = 0;

	private int w = 0;

	private int a[][] = null;

	private char b[][] = null;

	private char calculate(int y, int x) {
		if (b[y][x] == '?') {
			int dir = 0;
			int min = 999999;
			if ((y < h - 1) && (a[y + 1][x] <= min)) {
				dir = 3;
				min = a[y + 1][x];
			}
			if ((x < w - 1) && (a[y][x + 1] <= min)) {
				dir = 2;
				min = a[y][x + 1];
			}
			if ((x > 0) && (a[y][x - 1] <= min)) {
				dir = 4;
				min = a[y][x - 1];
			}
			if ((y > 0) && (a[y - 1][x] <= min)) {
				dir = 1;
				min = a[y - 1][x];
			}
			if (min < a[y][x]) {
				if (dir == 1) { b[y][x] = calculate(y - 1, x); }
				else if (dir == 2) { b[y][x] = calculate(y, x + 1); }
				else if (dir == 3) { b[y][x] = calculate(y + 1, x); }
				else { b[y][x] = calculate(y, x - 1); }
			}
			else {
				b[y][x] = d;
				d++;
			}
		}
		return b[y][x];
	}

	public void drainage(int h, int w, int alts[][], char basins[][]) {
		this.h = h;
		this.w = w;
		a = alts;
		b = basins;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				basins[i][j] = calculate(i, j);
			}
		}
	}
}
