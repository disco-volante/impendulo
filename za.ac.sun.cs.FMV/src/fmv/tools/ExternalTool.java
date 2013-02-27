package fmv.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import fmv.Pair;

public abstract class ExternalTool extends BasicTool {
	@Override
	public Pair run(File workDir, String... input) {
		if (needCompile()) {
			for (String in : input) {
				compile(new File(in));
			}
		}
		Pair ret;
		try {
			String[] args = getArgs(input);
			ProcessBuilder pb = new ProcessBuilder(args)
					.redirectErrorStream(true);
			if (workDir != null && workDir.exists() && workDir.isDirectory()) {
				pb.directory(workDir);
			}
			Map<String, String> env = pb.environment();
			env.clear();
			Process p = pb.start();
			BufferedReader errReader = new BufferedReader(
					new InputStreamReader(p.getErrorStream()));
			String line;
			while ((line = errReader.readLine()) != null) {
				System.err.println(line);
			}
			BufferedInputStream is = new BufferedInputStream(p.getInputStream());
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			int rb;
			while ((rb = is.read()) != -1) {
				os.write(rb);
			}
			int exit = p.waitFor();
			ret = new Pair(exit, os.toString());
			if (ret.outPut().trim() == ""){
				ret.setOutput("No problems detected.");
			}
			System.out.println(ret.outPut());
		} catch (InterruptedException e) {
			e.printStackTrace();
			ret = new Pair(1, e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			ret = new Pair(1, e.getMessage());
		}
		return ret;

	}

}
