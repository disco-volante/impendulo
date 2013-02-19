package fmv.tools;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class PMDRunner extends ToolRunner {
	private String[] command = new String[] { "tools/pb.sh", "pmd" };

	private String getConfig(String key, String value) {
		String config = null;
		if (!value.equals("none") && !value.equals("")) {
			config = "-"+key+" ! "+value;
		}
		return config;
	}
	@Override
	public String run(String ... input) throws IOException{
		config.put("dir", "-dir"+" ! "+input[0]);
		return super.run();
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

	@Override
	protected Collection<String> getCommand() {
		return Arrays.asList(command);
	}

	public static void main(String[] args) {
		ToolRunner pmd = new PMDRunner();
		pmd.configure("config/pmd.config");
		try {
			System.out.println(pmd.run("/home/disco/rw334/src"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
