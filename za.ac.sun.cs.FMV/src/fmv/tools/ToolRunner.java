package fmv.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class ToolRunner {
	protected HashMap<String, String> config;

	protected abstract void process(String line);

	protected abstract String[] getArgs(String output, String[] input);

	public void run(String output, boolean write, String... input)
			throws IOException {
		String[] args = getArgs(output, input);
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
		if (write) {
			InputStream is = p.getInputStream();
			OutputStream os = new FileOutputStream(new File(output));
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = is.read(bytes)) != -1) {
				os.write(bytes, 0, read);
			}
			os.close();
		}

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
