package fmv.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import fmv.Pair;

public class Lint4J extends JarTool {
	private static final String[] command = new String[] { "/home/disco/prog/lint4j/jars/lint4j.jar" };

	public static void main(final String[] args) {
		final BasicTool lj = new Lint4J();
		lj.configure("config/lint4j.config");
		System.out.println(lj.run(null, "fmv"));
	}

	@Override
	protected Collection<String> getCommand() {
		return Arrays.asList(Lint4J.command);
	}

	@Override
	protected String getConfig(final String key, final String value) {
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

	private ArrayList<String> getFiles(final File[] files) {
		final ArrayList<String> args = new ArrayList<String>();
		for (final File file : files) {
			if (file.isDirectory()) {
				args.addAll(getFiles(file.listFiles()));
			} else {
				args.add(file.getAbsolutePath());
			}
		}
		return args;
	}

	private String[] getFiles(final String[] input) {
		final ArrayList<File> files = new ArrayList<File>();
		for (final String in : input) {
			final File test = new File(in);
			if (!test.exists()) {
				throw new IllegalArgumentException("Unexpected argument " + in);
			} else {
				files.add(test);
			}
		}
		final ArrayList<String> processed = getFiles(files
				.toArray(new File[files.size()]));
		return processed.toArray(new String[processed.size()]);
	}

	@Override
	protected boolean needCompile() {
		return false;
	}

	@Override
	public Pair run(final File workDir, final String... input) {
		return super.run(workDir, getFiles(input));
	}
}
