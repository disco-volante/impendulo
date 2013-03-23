package fmv.tools;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import fmv.Pair;

public class PMD extends ExternalTool {
	public static void main(final String[] args) {
		final BasicTool pmd = new PMD();
		pmd.configure("config/pmd.config");
		System.out.println(pmd.run(null, "/home/disco/rw334/src"));
	}

	private final String[] command = new String[] {
			"/home/disco/prog/pmd/bin/run.sh", "pmd" };

	@Override
	protected Collection<String> getCommand() {
		return Arrays.asList(command);
	}

	@Override
	protected String getConfig(final String key, final String value) {
		String config = null;
		if (!value.equals("none") && !value.equals("")) {
			config = "-" + key + " ! " + value;
		}
		return config;
	}

	@Override
	protected boolean needCompile() {
		return false;
	}

	@Override
	public Pair run(final File workDir, final String... input) {
		config.put("dir", "-dir" + " ! " + input[0]);
		return super.run(workDir);
	}

}
