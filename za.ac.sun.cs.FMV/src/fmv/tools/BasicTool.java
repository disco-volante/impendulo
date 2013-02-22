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

	protected abstract Collection<String> getCommand();

	protected String[] getArgs(String... input) {
		ArrayList<String> args = new ArrayList<String>();
		args.addAll(getCommand());
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

	public abstract Pair run(File workDir, String... input) throws IOException, InterruptedException;

	public void configure(String configFile) {
		BufferedReader br = null;
		config.clear();
		try {
			br = new BufferedReader(new FileReader(configFile));
			String line = br.readLine();
			while (line != null) {
				process(line);
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void configure(String[] configs) {
		config.clear();
		for(String config : configs){
			process(config);
		}
	}

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

	protected abstract String getConfig(String key, String value);
	

}
