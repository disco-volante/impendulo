package fmv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.SwingWorker;

import fmv.tools.BasicTool;
import fmv.tools.Compiler;
import fmv.tools.Interpreter;
import fmv.tools.Tools;

public class ToolRunner extends SwingWorker<Boolean, String> {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"HH:mm:ss.SSS");

	private final Source root;

	private String tempDir;

	private final String sourceDir;

	private final String toolName;

	private final ToolRunnerDialog dialog;

	private final String[] tests = new String[] { "EasyTests", "AllTests" };

	private String testZip;

	public ToolRunner(final Source root, final ToolRunnerDialog dialog,
			final String toolName) {
		this.root = root;
		this.dialog = dialog;
		this.toolName = toolName;
		tempDir = System.getProperty("java.io.tmpdir");
		if (!tempDir.endsWith(File.separator)) {
			tempDir = tempDir + File.separator;
		}
		sourceDir = tempDir + "src";
	}

	public ToolRunner(final Source root, final ToolRunnerDialog dialog,
			final String toolName, final String testZip) {
		this(root, dialog, toolName);
		removeDirs(new File(sourceDir));
		this.testZip = testZip;
	}

	@Override
	protected Boolean doInBackground() throws Exception {
		publish("Initializing");
		boolean ret;
		if (toolName.equals("Tests")) {
			ret = executeTests();
		} else {
			ret = executeTool();
		}
		return ret;
	}

	private boolean executeTool() {
		final Set<Date> dates = root.getKeys();
		int i = 1;
		final int n = dates.size();
		final BasicTool tool = Tools.getTool(toolName);
		for (final Date d : dates) {
			if (isCancelled()) {
				return false;
			}
			final String df = "(" + i + "/" + n + ") "
					+ ToolRunner.dateFormat.format(d);
			final File file = root.unpack(d, sourceDir);
			if (file == null) {
				return false;
			}
			final File workDir = new File(tempDir);
			publish(df + ": running " + tool);
			final Pair p = tool.run(workDir, file.getParentFile()
					.getAbsolutePath());
			root.setReport(d, toolName, p.outPut());
			setProgress(i++ * 100 / n);
		}
		return true;
	}

	private boolean executeTests() {
		prepareTestdir();
		final Set<Date> dates = root.getKeys();
		int i = 1;
		final int n = dates.size();
		final Compiler compiler = new Compiler(FMV.prefs.getCompilerCmd());
		compiler.configure(new String[] { "cp: " + sourceDir });
		final Interpreter interpreter = new Interpreter(
				FMV.prefs.getInterpreterCmd());
		interpreter.configure(new String[] { "cp: " + sourceDir
				+ " org.junit.runner.JUnitCore" });
		for (final Date d : dates) {
			if (isCancelled()) {
				return false;
			}
			final String df = "(" + i + "/" + n + ") "
					+ ToolRunner.dateFormat.format(d);
			root.unpack(d, sourceDir);
			final File workDir = new File(sourceDir);
			runTests(d, df, compiler, interpreter, workDir);
			setProgress(i++ * 100 / n);
		}
		return true;
	}

	@SuppressWarnings("resource")
	private void prepareTestdir() {
		if (FMV.prefs.getRemoveSrc()) {
			removeDirs(new File(sourceDir));
		}
		try {
			final ZipFile z = new ZipFile(testZip);
			final Enumeration<? extends ZipEntry> zz = z.entries();
			while (zz.hasMoreElements()) {
				final ZipEntry e = zz.nextElement();
				if (e.isDirectory()) {
					continue;
				}
				final String toName = sourceDir + File.separator + e.getName();
				final File toFile = new File(toName);
				toFile.getParentFile().mkdirs();
				toFile.createNewFile();
				final InputStream from = z.getInputStream(e);
				final FileOutputStream to = new FileOutputStream(toFile);
				final byte[] buffer = new byte[65536];
				int bytesRead;
				while ((bytesRead = from.read(buffer)) != -1) {
					to.write(buffer, 0, bytesRead);
				}
				to.close();
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void process(final List<String> messages) {
		for (final String m : messages) {
			dialog.setMessage(m);
		}
	}

	private void removeDirs(final File file) {
		if (file.isDirectory()) {
			for (final File f : file.listFiles()) {
				removeDirs(f);
			}
		} else {
			file.delete();
		}
	}

	private void runTests(final Date date, final String df,
			final Compiler compiler, final Interpreter interpreter,
			final File workDir) {
		Pair p;
		for (final String test : tests) {
			publish(df + ": compiling " + test);
			p = compiler.run(workDir, sourceDir + File.separator + "testing"
					+ File.separator + test + ".java");
			if (p.hasError()) {
				root.setStatus(date, Status.NOCOMPILE, p.outPut());
				FMV.log(ToolRunner.class.getName(), p.outPut());
				return;
			}
		}
		for (final String test : tests) {
			publish(df + ": running " + test);
			p = interpreter.run(workDir, "testing." + test);
			if (p.hasError()) {
				if (p.outPut().contains("Out of time")) {
					root.setStatus(date, Status.TIMEOUT, p.outPut());
				} else if (p.outPut().contains("Errors: 0")) {
					root.setStatus(date, Status.E_FAIL, p.outPut());
				} else {
					root.setStatus(date, Status.E_ERRS, p.outPut());
				}
				return;
			}
			FMV.log(ToolRunner.class.getName(), p.outPut());
			root.setStatus(date, Status.OK, p.outPut());
		}
	}

}
