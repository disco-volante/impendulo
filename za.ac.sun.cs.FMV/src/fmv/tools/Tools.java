package fmv.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import fmv.Pair;

public class Tools {

    private static String tempDir;
    private static String sourceDir;

    public static Pair run(String tool, String string) {
        buildSrc(string);
        BasicTool runner = null;
        if (tool.equals("Findbugs")) {
            runner = new FindBugs();
            runner.configure("config/fb.config");
        } else if (tool.equals("JLint")) {
            runner = new JLint();
            runner.configure("config/jlint.config");
        } else if (tool.equals("PMD")) {
            runner = new PMD();
            runner.configure("config/pmd.config");
        }
        Pair ret = null;
        try {
            ret = runner.run(new File(tempDir), sourceDir);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return ret;
    }

    private static void buildSrc(String date) {
        tempDir = System.getProperty("java.io.tmpdir");
        if (!tempDir.endsWith(File.separator)) {
            tempDir = tempDir + File.separator;
        }
        sourceDir = tempDir + "src";
        removeDirs(new File(sourceDir));
        prepareSrcdir(date);
    }

    private static void removeDirs(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                removeDirs(f);
            }
        }
    }

    private static void prepareSrcdir(String date) {
        try {
            ZipFile z = new ZipFile("/home/disco/test/kselect3228.zip");
            Enumeration<? extends ZipEntry> zz = z.entries();
            while (zz.hasMoreElements()) {
                ZipEntry e = zz.nextElement();
                if (e.isDirectory()) {
                    continue;
                }
                if (e.getName().contains(".java_" + date) || e.getName().contains(".class_" + date)) {
                    String fname;
                    if (e.getName().contains(".java")) {
                        fname = e.getName().substring(0, e.getName().lastIndexOf(".")) + ".java";
                        fname = sourceDir + File.separator + fname.substring(fname.lastIndexOf('_') + 1);
                    } else {
                        fname = e.getName().substring(0, e.getName().lastIndexOf(".")) + ".class";
                        fname = sourceDir + File.separator + fname.substring(fname.lastIndexOf('_') + 1);
                    }
                    System.out.println(fname);
                    File toFile = new File(fname);
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
    	BasicTool runner;
        try {
            runner = new JLint();
            runner.configure("config/jlint.config");
            runner.run(null, "/home/disco/test/kselect/");
            runner = new FindBugs();
            runner.configure("config/fb.config");
            runner.run(null, "/home/disco/test/kselect/");
            runner = new PMD();
            runner.configure("config/pmd.config");
            runner.run(null, "/home/disco/test/kselect/");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Tools.run("JLint", "1256028633727");
    }
}
