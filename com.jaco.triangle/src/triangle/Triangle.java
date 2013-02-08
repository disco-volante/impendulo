package triangle;

public class Triangle {

	public int maxpath(int[][] tri) {
		int h = tri.length;
		for (int i = h - 2; i >= 0; i--) {
			for (int j = 0; j <= i; j++) {
				tri[i][j] += Math.max(tri[i + 1][j], tri[i + 1][j + 1]);
			}
		}
		return tri[0][0];
	}
}
