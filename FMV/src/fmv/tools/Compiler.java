package fmv.tools;

import java.util.Arrays;
import java.util.Collection;

public class Compiler extends ExternalTool {

	public static void main(final String[] args) {
		final ExternalTool javac = new Compiler();
		javac.configure("config/compiler.config");
		System.out.println(javac.run(null, "src/fmv/tools/FindBugs.java"));
	}

	private String command = "/usr/bin/javac";

	public Compiler() {
	};

	public Compiler(final String cmd) {
		command = cmd;
	}

	@Override
	protected Collection<String> getCommand() {
		return Arrays.asList(command);
	}

	@Override
	protected String getConfig(final String key, final String value) {
		String config = null;
		if (!value.equals("none") && !value.equals("")) {
			if (key.equals("classpath") || key.equals("cp")) {
				config = "-cp ! " + value.replaceAll(" ", " ! ");
			} else {
				throw new IllegalArgumentException(key + " : " + value);
			}
		}
		return config;
	}

	@Override
	protected boolean needCompile() {
		return false;
	}

}
