package fmv.tools;

import java.io.IOException;
import java.util.ArrayList;

public class JLintRunner extends ToolRunner {

	private String command;

	protected String[] getArgs(String output, String[] input) {
		ArrayList<String> args = new ArrayList<String>();
		args.add(command);
		for (String in : input) {
			args.add(in);
		}
		return args.toArray(new String[args.size()]);
	}

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
			fb.run("out.txt",true, "/home/disco/rw334/src");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
