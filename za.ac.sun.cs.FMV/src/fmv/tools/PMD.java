package fmv.tools;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import fmv.Pair;

public class PMD extends ExternalTool {
	private String[] command = new String[] { "tools/pb.sh", "pmd" };
	@Override
	protected String getConfig(String key, String value) {
		String config = null;
		if (!value.equals("none") && !value.equals("")) {
			config = "-"+key+" ! "+value;
		}
		return config;
	}
	@Override
	public Pair run(File workDir, String ... input){
		config.put("dir", "-dir"+" ! "+input[0]);
		return super.run(workDir);
	}

	@Override
	protected Collection<String> getCommand() {
		return Arrays.asList(command);
	}

	public static void main(String[] args) {
		BasicTool pmd = new PMD();
		pmd.configure("config/pmd.config");
		System.out.println(pmd.run(null, "/home/disco/rw334/src"));
	}

}
