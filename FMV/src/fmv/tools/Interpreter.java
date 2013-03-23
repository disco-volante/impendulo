package fmv.tools;

import java.util.Arrays;
import java.util.Collection;

public class Interpreter extends ExternalTool {
	public static void main(final String[] args) {
		final ExternalTool java = new Interpreter();
		java.configure("config/interpreter.config");
		System.out.println(java.run(null, "fmv.tools.FindBugs"));
	}

	private String command = "/usr/bin/java";

	public Interpreter() {
	};

	public Interpreter(final String cmd) {
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
			} else if (key.equals("jar") && value.equals("true")) {
				config = "-jar";
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
