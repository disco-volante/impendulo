package fmv.tools;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class FindBugsRunner extends ToolRunner {
	private static final String[] command = new String[] {"tools/fb.sh", "-textui" };

	private String getConfig(String key, String value) {
		String config = null;
		if (!value.equals("none") && !value.equals("")) {
			if (key.equals("outFormat") || key.equals("priority")) {
				config = "-" + value;
			} else if (key.equals("auxclasspath")) {
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
			if ((value = getConfig(key, value)) != null) {
				config.put(key, value);
			}
		}
	}

	public static void main(String[] args) {
		ToolRunner fb = new FindBugsRunner();
		fb.configure("config/fb.config");
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
