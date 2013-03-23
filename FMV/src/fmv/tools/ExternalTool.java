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
	public Pair run(final File workDir, final String... input) {
		if (needCompile()) {
			for (final String in : input) {
				compile(new File(in));
			}
		}
		Pair ret;
		try {
			final String[] args = getArgs(input);
			final ProcessBuilder pb = new ProcessBuilder(args)
					.redirectErrorStream(true);
			System.out.println(pb.command());
			if (workDir != null && workDir.exists() && workDir.isDirectory()) {
				pb.directory(workDir);
			}
			final Map<String, String> env = pb.environment();
			env.clear();
			final Process p = pb.start();
			final BufferedReader errReader = new BufferedReader(
					new InputStreamReader(p.getErrorStream()));
			String line;
			while ((line = errReader.readLine()) != null) {
				System.err.println(line);
			}
			final BufferedInputStream is = new BufferedInputStream(
					p.getInputStream());
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			int rb;
			while ((rb = is.read()) != -1) {
				os.write(rb);
			}
			final int exit = p.waitFor();
			ret = new Pair(exit, os.toString());
			if (ret.outPut().trim() == "") {
				ret.setOutput("No problems detected.");
			}
			System.out.println(ret.outPut());
		} catch (final InterruptedException e) {
			e.printStackTrace();
			ret = new Pair(1, e.getMessage());
		} catch (final IOException e) {
			e.printStackTrace();
			ret = new Pair(1, e.getMessage());
		}
		return ret;

	}

}
