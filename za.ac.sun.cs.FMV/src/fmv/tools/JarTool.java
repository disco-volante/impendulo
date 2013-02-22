package fmv.tools;

import java.io.File;
import java.io.IOException;

import fmv.Pair;

public abstract class JarTool extends BasicTool{
	@Override
	public Pair run(File workDir, String... input) throws IOException, InterruptedException {
		String[] args = getArgs(input);
		Interpreter jar = new Interpreter();
		jar.configure(new String[]{"jar: true"});
		return jar.run(workDir, args);

	}
}
