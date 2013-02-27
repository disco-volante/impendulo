package fmv.tools;

import java.util.Arrays;
import java.util.Collection;

public class JLint extends ExternalTool {

	private String command = "tools/jlint";

	public static void main(String[] args) {
		ExternalTool fb = new JLint();
		fb.configure("config/jlint.config");
		System.out.println(fb.run(null, "/home/disco/rw334/src"));
	}

	@Override
	protected Collection<String> getCommand() {
		return Arrays.asList(command);
	}

	@Override
	protected String getConfig(String key, String value) {
		return null;
	}

	@Override
	protected boolean needCompile() {
		return true;
	}

}
