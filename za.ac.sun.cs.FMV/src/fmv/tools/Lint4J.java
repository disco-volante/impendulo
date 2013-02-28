package fmv.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import fmv.Pair;

public class Lint4J extends JarTool {
	private static final String[] command = new String[] { "/home/disco/prog/lint4j/jars/lint4j.jar" };

	@Override
	public Pair run(File workDir, String... input) {
		return super.run(workDir, getFiles(input));
	}

	private String[] getFiles(String[] input) {
		ArrayList<File> files = new ArrayList<File>();
		for (String in : input) {
			File test = new File(in);
			if (!test.exists()) {
				throw new IllegalArgumentException("Unexpected argument " + in);
			} else {
				files.add(test);
			}
		}
		ArrayList<String> processed = getFiles(files.toArray(new File[files
				.size()]));
		return processed.toArray(new String[processed.size()]);
	}

	private ArrayList<String> getFiles(File[] files) {
		ArrayList<String> args = new ArrayList<String>();
		for (File file : files) {
			if (file.isDirectory()) {
				args.addAll(getFiles(file.listFiles()));
			} else {
				args.add(file.getAbsolutePath());
			}
		}
		return args;
	}

	@Override
	protected String getConfig(String key, String value) {
		String config = null;
		if (!value.equals("none") && !value.equals("")) {
			if (key.equals("verbosity")) {
				config = "-v ! " + value;
			} else if (key.equals("exact")) {
				config = "-exact";
			} else if (key.equals("sourcepath") || key.equals("classpath")
					|| key.equals("classes")) {
				config = "-" + key + " ! " + value.replaceAll(" ", ":");
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
		System.out.println(lj.run(null, "fmv"));
	}

	@Override
	protected Collection<String> getCommand() {
		return Arrays.asList(command);
	}

	@Override
	protected boolean needCompile() {
		return false;
	}
}
