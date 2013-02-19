package fmv.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class ToolRunner {
	protected HashMap<String, String> config;

	protected abstract void process(String line);

	protected abstract Collection<String> getCommand();

	protected String[] getArgs(String[] input) {
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

	public String run(String... input) throws IOException {
		String[] args = getArgs(input);
		ProcessBuilder pb = new ProcessBuilder(args);
		Map<String, String> env = pb.environment();
		env.clear();
		Process p = pb.start();
		BufferedReader errReader = new BufferedReader(new InputStreamReader(
				p.getErrorStream()));
		String line;
		while ((line = errReader.readLine()) != null) {
			System.err.println(line);
		}
		StringBuilder sb = new StringBuilder();
		InputStream is = p.getInputStream();
		byte[] bytes = new byte[1024];
		while ((is.read(bytes)) != -1) {
			sb.append(new String(bytes));
		}
		return sb.toString();

	}

	public void configure(String configFile) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(configFile));
			String line = br.readLine();
			config = new HashMap<String, String>();
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
}
