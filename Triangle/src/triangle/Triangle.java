package triangle;

public class Triangle {
	public int maxpath(int[][] tri) {
		int h = tri.length - 2;
		for (int i = h; i >= 0; i--) {
			for (int j = 0; j <= i; j++) {
				tri[i][j] += Math.max(tri[i + 1][j + 1], tri[i][j]);
			}
		}		
		return tri[0][0];
	}
}