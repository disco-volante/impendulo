package fmv;

public class Pair {
	private int exit;
	private String output;

	public Pair(int ex, String out) {
		exit = ex;
		output = out;
	}

	public String toString() {
		return output + "\n Exit code : " + exit;
	}

	public boolean hasError() {
		return  exit != 0;
	}

	public String outPut() {
		return output;
	}

}
