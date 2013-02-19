package fmv.tools;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class JLintRunner extends ToolRunner {

	private String command = "tools/jlint";

	protected void process(String line) {
		String[] pair = line.split(":");
		if (pair.length == 2) {
			if (pair[0].trim().equals("command")) {
				command = pair[1].trim();
			}
		}
	}

	public static void main(String[] args) {
		ToolRunner fb = new JLintRunner();
		fb.configure("config/jlint.config");
		try {
			System.out.println(fb.run("/home/disco/rw334/src"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Collection<String> getCommand() {
		return Arrays.asList(command);
	}

}
