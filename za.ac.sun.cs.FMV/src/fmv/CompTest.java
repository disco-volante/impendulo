package fmv;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.SwingWorker;

import fmv.tools.FindBugsRunner;
import fmv.tools.ToolRunner;

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

	private static class Pair {
		int rv;
		String s;

		public Pair(int rv, String s) {
			this.rv = rv;
			this.s = s;
		}
	}

	private Pair execute(String... args) {
		try {
			StringBuffer bbb = new StringBuffer();
			for (String sss : args) { bbb.append(sss); bbb.append(" "); }
			System.out.println("CMD: " + bbb);
			ProcessBuilder b = new ProcessBuilder(args).directory(
					new File(tempDir)).redirectErrorStream(true);
			Map<String, String> env = b.environment();
			env.clear();
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			byte[] buffer = new byte[65536];
			int bytesRead;
			Process p = b.start();
			BufferedInputStream op = new BufferedInputStream(p.getInputStream());
			while ((bytesRead = op.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			int r = p.waitFor();
			return new Pair(r, os.toString());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Pair(1, "execution failure");
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
			String c, fbs;
			Pair p;
			while (true) {
				publish(df + ": compiling");
				c = FMV.prefs.getCompilerCmd();
				p = execute(c, "-cp", sourceDir, sourceDir + File.separator
						+ "testing" + File.separator + "EasyTests.java");
				if (p.rv != 0) {
					root.setStatus(d, Status.NOCOMPILE, p.s);
					break;
				}

				p = execute(c, "-cp", sourceDir, sourceDir + File.separator
						+ "testing" + File.separator + "AllTests.java");
				if (p.rv != 0) {
					root.setStatus(d, Status.NOCOMPILE, p.s);
					break;
				}
/*
				publish(df + ": running findbugs");
				c = FMV.prefs.getInterpreterCmd();
				fbj = FMV.prefs.getFindbugsJar();
				fbs = FMV.prefs.getFindbugsSrc();
				
				String classlist = "";
				File f = new File(sourceDir + File.separator + fbs);
				String s[] = f.list();
				for (int ii = 0; ii < s.length; ii++) {
					String nn = s[ii];
					if (nn.endsWith(".class")) {
						classlist += " " + sourceDir + File.separator + fbs + File.separator + nn;
					}
				}
				// p = execute(c, "-jar", fbj, "-textui", "-low", "-progress", "-sourcepath", sourceDir + File.separator + fbs, classlist);
				
				// p = execute(c, "-jar", fbj, "-textui", "-low", "-progress", "-longBugCodes", "-sourcepath", sourceDir + File.separator + fbs, sourceDir + File.separator + fbs);
				// p = execute(c, "-jar", fbj, "-textui", "-low", "-longBugCodes", "-sourcepath", sourceDir + File.separator + fbs, sourceDir + File.separator + fbs);
				p = execute("C:/Program Files/jlint-3/jlint.exe", "-source", sourceDir + File.separator + fbs, classlist);
*/
				/*publish(df + ": running tpgen");
				c = FMV.prefs.getInterpreterCmd();
				fbs = FMV.prefs.getFindbugsSrc();
				p = execute(c, "-jar", sourceDir + File.separator + "tpgen.jar",
						"-max-instr-recur", "10",
						"-soot-classpath", RT_LOC + sourceDir,
						fbs);
				int w = 0;
				int z = p.s.indexOf("\n");
				while (z != -1) {
					w++;
					z = p.s.indexOf("\n", z + 1);
				}*/
				/*
				if (p.s.contains("Warnings generated:")) {
					int v = p.s.indexOf("Warnings generated:");
					w = Integer.parseInt(p.s.substring(v + 18));
				}
				*/
				//root.setReport(d, p.s, w);
				ToolRunner fb = new FindBugsRunner();
				fb.configure("/home/disco/impendulo/za.ac.sun.cs.FMV/src/fmv/fb.config");
				fb.run("watersheds"+d.toString()+".html", false, sourceDir+ File.separator+"watersheds");
				publish(df + ": running EasyTests");
				c = FMV.prefs.getInterpreterCmd();
				p = execute(c, "-cp", sourceDir, "org.junit.runner.JUnitCore",
						"testing.EasyTests");
				if (p.rv != 0) {
					if (p.s.contains("Out of time")) {
						root.setStatus(d, Status.TIMEOUT, p.s);
					} else if (p.s.contains("Errors: 0")) {
							root.setStatus(d, Status.E_FAIL, p.s);
					} else {
						root.setStatus(d, Status.E_ERRS, p.s);
					}
					break;
				}

				publish(df + ": running AllTests");
				p = execute(c, "-cp", sourceDir, "org.junit.runner.JUnitCore",
						"testing.AllTests");
				if (p.rv != 0) {
					if (p.s.contains("Out of time")) {
						root.setStatus(d, Status.TIMEOUT, p.s);
					} else if (p.s.contains("Errors: 0")) {
							root.setStatus(d, Status.E_FAIL, p.s);
					} else {
						root.setStatus(d, Status.A_ERRS, p.s);
					}
					break;
				}
				root.setStatus(d, Status.OK, p.s);
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
