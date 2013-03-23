package fmv;

public class Pair {
	private final int exit;
	private String output;

	public Pair(final int ex, final String out) {
		exit = ex;
		output = out;
	}

	public boolean hasError() {
		return exit != 0;
	}

	public String outPut() {
		return output;
	}

	public void setOutput(final String out) {
		output = out;

	}

	@Override
	public String toString() {
		return output + "\n Exit code : " + exit;
	}

}
