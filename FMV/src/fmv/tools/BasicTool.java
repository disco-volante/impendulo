package fmv.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import fmv.Pair;

public abstract class BasicTool {
	protected HashMap<String, String> config = new HashMap<String, String>();

	protected void compile(final File current) {
		if (current.isDirectory()) {
			final File[] files = current.listFiles();
			for (final File file : files) {
				if (file.getName().endsWith("class")) {
					file.delete();
				} else {
					compile(file);
				}
			}
		} else if (current.getAbsolutePath().endsWith("java")) {
			compile(current.getParentFile(), current.getAbsolutePath());
		}
	}

	private void compile(final File sourceDir, final String java) {
		final Compiler compiler = new Compiler();
		compiler.configure(new String[] { "cp: " + sourceDir });
		compiler.run(sourceDir, java);
	}

	public void configure(final String configFile) {
		BufferedReader br = null;
		config.clear();
		try {
			br = new BufferedReader(new FileReader(configFile));
			String line = br.readLine();
			while (line != null) {
				process(line);
				line = br.readLine();
			}
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void configure(final String[] configs) {
		config.clear();
		for (final String config : configs) {
			process(config);
		}
	}

	protected String[] getArgs(final String... input) {
		final ArrayList<String> args = new ArrayList<String>();
		args.addAll(getCommand());
		for (final String cfg : config.values()) {
			final String[] params = cfg.split("!");
			for (final String param : params) {
				args.add(param.trim());
			}
		}
		for (final String in : input) {
			args.add(in);
		}
		return args.toArray(new String[args.size()]);
	}

	protected abstract Collection<String> getCommand();

	protected abstract String getConfig(String key, String value);

	protected abstract boolean needCompile();

	protected void process(final String line) {
		final String[] pair = line.split(":");
		if (pair.length == 2) {
			final String key = pair[0].trim();
			String value = pair[1].trim();
			if ((value = getConfig(key, value)) != null) {
				config.put(key, value);
			}
		}
	}

	public abstract Pair run(File workDir, String... input);

}
