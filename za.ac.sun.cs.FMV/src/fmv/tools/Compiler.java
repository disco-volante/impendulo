package fmv.tools;

import java.util.Arrays;
import java.util.Collection;

public class Compiler extends ExternalTool {

	private String command = "/usr/bin/javac";

	public Compiler(String cmd) {
		command = cmd;
	}
	public Compiler(){};

	@Override
	protected Collection<String> getCommand() {
		return Arrays.asList(command);
	}	
	@Override
	protected String getConfig(String key, String value) {
		String config = null;
		if (!value.equals("none") && !value.equals("")) {
			if (key.equals("classpath") || key.equals("cp") ){
				config = "-cp ! " + value.replaceAll(" ", " ! ");
			} else {
				throw new IllegalArgumentException(key + " : " + value);
			}
		}
		return config;
	}

	public static void main(String[] args) {
		ExternalTool javac = new Compiler();
		javac.configure("config/compiler.config");
		System.out.println(javac.run(null, "src/fmv/tools/FindBugs.java"));
	}
	@Override
	protected boolean needCompile() {
		return false;
	}


}
