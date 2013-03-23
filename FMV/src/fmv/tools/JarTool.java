package fmv.tools;

import java.io.File;

import fmv.Pair;

public abstract class JarTool extends BasicTool {
	@Override
	public Pair run(final File workDir, final String... input) {
		if (needCompile()) {
			for (final String in : input) {
				compile(new File(in));
			}
		}
		final String[] args = getArgs(input);
		final Interpreter jar = new Interpreter();
		jar.configure(new String[] { "jar: true" });
		return jar.run(workDir, args);

	}
}
