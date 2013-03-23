package fmv.tools;

import java.util.Arrays;
import java.util.Collection;

public class JLint extends ExternalTool {

	public static void main(final String[] args) {
		final ExternalTool fb = new JLint();
		fb.configure("config/jlint.config");
		System.out.println(fb.run(null, "/home/disco/rw334/src"));
	}

	private final String command = "jlint";

	@Override
	protected Collection<String> getCommand() {
		return Arrays.asList(command);
	}

	@Override
	protected String getConfig(final String key, final String value) {
		return null;
	}

	@Override
	protected boolean needCompile() {
		return true;
	}

}
