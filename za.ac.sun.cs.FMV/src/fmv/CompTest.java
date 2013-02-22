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

import fmv.tools.Compiler;
import fmv.tools.Interpreter;

public class CompTest extends SwingWorker<Boolean, String> {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

	private static final String RT_LOC = "/usr/lib/jvm/java-7-openjdk/jre/lib/rt.jar;";

	private Source root;

	private String tempDir;

	private String sourceDir;

	private String testzip;

	private CompTestDialog dialog;

	public CompTest(Source root, String tz, CompTestDialog dialog) {
		this.root = root;
		this.dialog = dialog;
		tempDir = System.getProperty("java.io.tmpdir");
		if (!tempDir.endsWith(File.separator)) {
			tempDir = tempDir + File.separator;
		}
		sourceDir = tempDir + "src";
		testzip = tz;
		removeDirs(new File(sourceDir));
	}

	private void removeDirs(File file) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				removeDirs(f);
			}
		}
		else if (!file.getName().equals("tpgen.jar")) {
			file.delete();
		}
	}

	@SuppressWarnings("resource")
	private void prepareSrcdir() {
		if (FMV.prefs.getRemoveSrc()) {
			System.out.println("deleting dirs");
			removeDirs(new File(sourceDir));
		}
		try {
			ZipFile z = new ZipFile(testzip);
			Enumeration<? extends ZipEntry> zz = z.entries();
			while (zz.hasMoreElements()) {
				ZipEntry e = zz.nextElement();
				if (e.isDirectory()) {
					continue;
				}
				String toName = sourceDir + File.separator + e.getName();
				File toFile = new File(toName);
				toFile.getParentFile().mkdirs();
				toFile.createNewFile();
				InputStream from = z.getInputStream(e);
				FileOutputStream to = new FileOutputStream(toFile);
				byte[] buffer = new byte[65536];
				int bytesRead;
				while ((bytesRead = from.read(buffer)) != -1) {
					to.write(buffer, 0, bytesRead);
				}
				to.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected Boolean doInBackground() throws Exception {
		prepareSrcdir();
		Set<Date> dates = root.getKeys();
		int i = 1, n = dates.size();
		for (Date d : dates) {
			if (isCancelled()) {
				return false;
			}
			String df = "(" + i + "/" + n + ") " + dateFormat.format(d);
			root.unpack(d, sourceDir);
			Pair p;
			File workDir = new File(tempDir);
			Compiler compiler = new Compiler(FMV.prefs.getCompilerCmd());
			Interpreter interpreter = new Interpreter(FMV.prefs.getInterpreterCmd());
			while (true) {
				publish(df + ": compiling");
				compiler.configure(new String[]{"-cp", sourceDir});
				p = compiler.run(workDir, sourceDir + File.separator + "testing" + File.separator + "EasyTests.java");
				if (p.hasError()) {
					root.setStatus(d, Status.NOCOMPILE, p.outPut());
					break;
				}
				p = compiler.run(workDir, sourceDir + File.separator
						+ "testing" + File.separator + "AllTests.java");
				if (p.hasError()) {
					root.setStatus(d, Status.NOCOMPILE, p.outPut());
					break;
				}
				publish(df + ": running EasyTests");
				interpreter.configure(new String[]{"-cp", sourceDir, "org.junit.runner.JUnitCore"});
				p = interpreter.run(workDir, "testing.EasyTests");
				if (p.hasError()) {
					if (p.outPut().contains("Out of time")) {
						root.setStatus(d, Status.TIMEOUT, p.outPut());
					} else if (p.outPut().contains("Errors: 0")) {
							root.setStatus(d, Status.E_FAIL, p.outPut());
					} else {
						root.setStatus(d, Status.E_ERRS, p.outPut());
					}
					break;
				}

				publish(df + ": running AllTests");
				p = interpreter.run(workDir, "testing.AllTests");
				if (p.hasError()) {
					if (p.outPut().contains("Out of time")) {
						root.setStatus(d, Status.TIMEOUT, p.outPut());
					} else if (p.outPut().contains("Errors: 0")) {
						root.setStatus(d, Status.E_FAIL, p.outPut());
					} else {
						root.setStatus(d, Status.A_ERRS, p.outPut());
					}
					break;
				}
				root.setStatus(d, Status.OK, p.outPut());
				break;
			}
			setProgress((i++ * 100) / n);
		}
		return true;
	}

	public void process(List<String> messages) {
		for (String m : messages) {
			dialog.setMessage(m);
		}
	}

}
