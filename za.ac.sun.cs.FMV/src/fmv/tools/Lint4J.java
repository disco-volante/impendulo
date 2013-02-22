package fmv.tools;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class Lint4J extends JarTool {
	private static final String[] command = new String[] {"tools/lint4j.jar"};

	@Override
	protected String getConfig(String key, String value) {
		String config = null;
		if (!value.equals("none") && !value.equals("")) {
			if (key.equals("verbosity")) {
				config = "-v ! " + value;
			} else if (key.equals("exact")) {
				config = "-exact";
			} else if (key.equals("sourcepath") || key.equals("classpath") || key.equals("classes")) {
				config = "-"+key+" ! " + value.replaceAll(" ", ":");
			} else if (key.equals("exclude")) {
				config = "-exclude ! " + value;
			} else {
				throw new IllegalArgumentException(key + " : " + value);
			}
		}
		return config;
	}

	public static void main(String[] args) {
		BasicTool lj = new Lint4J();
		lj.configure("config/lint4j.config");
		try {
			System.out.println(lj.run(null, "fmv"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Collection<String> getCommand() {
		return Arrays.asList(command);
	}
}
