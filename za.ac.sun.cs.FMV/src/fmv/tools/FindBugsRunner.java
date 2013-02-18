package fmv.tools;

import java.io.IOException;
import java.util.ArrayList;

public class FindBugsRunner extends ToolRunner {
	private String command;
	private String lib;

	@Override
	protected String[] getArgs(String output, String[] input) {
		ArrayList<String> args = new ArrayList<String>();
		args.add(command);
		args.add("-jar");
		args.add(lib);
		args.add("-textui");
		args.add("-output");
		args.add(output);
		for (String cfg : config.values()) {
			String[] params = cfg.split("!");
			for (String param : params) {
				args.add(param.trim());
			}
		}
		for (String in : input) {
			args.add(in);
		}
		return args.toArray(new String[args.size()]);
	}

	private String getConfig(String key, String value) {
		String config = null;
		if (!value.equals("none") && !value.equals("")) {
			if (key.equals("outFormat") || key.equals("priority")) {
				config = "-" + value;
			} else if (key.equals("auxCP")) {
				config = "-auxclasspath ! " + value;
			} else if (key.equals("nested")) {
				config = "-nested:" + value;
			} else if (key.equals("relaxed")) {
				if (value.equals("true")) {
					config = "-relaxed";
				}
			} else {
				throw new IllegalArgumentException(key + " : " + value);
			}
		}
		return config;
	}

	@Override
	protected void process(String line) {
		String[] pair = line.split(":");
		if (pair.length == 2) {
			String key = pair[0].trim();
			String value = pair[1].trim();
			if (key.equals("command")) {
				command = value;
			} else if (key.equals("toolLocation")) {
				lib = value;
			} else {
				if ((value = getConfig(key, value)) != null) {
					config.put(key, value);
				}
			}
		}
	}

	public static void main(String[] args) {
		ToolRunner fb = new FindBugsRunner();
		fb.configure("config/fb.config");
		try {
			fb.run("out.html", false, "/home/disco/rw334/Parse.jar");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
