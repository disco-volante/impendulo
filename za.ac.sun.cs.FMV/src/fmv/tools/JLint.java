package fmv.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import fmv.Pair;

public class JLint extends ExternalTool {

	private String command = "tools/jlint";

    public Pair run(File workDir, String ... input) {
        for(String in : input){
            genClasses(new File(in));
         }
        return super.run(workDir, input);
    }
    private void genClasses(File current){
        if(current.isDirectory()){
            File[] files = current.listFiles();
            for(File file : files){
                genClasses(file);
            }
        }   else if(current.getAbsolutePath().endsWith("java")){
            try {
                compile(current.getAbsolutePath());
            } catch (IOException ie){
                                   ie.printStackTrace();
            }
           }
    }
    private void compile(String src) throws IOException{
        ProcessBuilder pb = new ProcessBuilder("javac",src);
        Map<String, String> env = pb.environment();
        env.clear();
        Process p = pb.start();
        BufferedReader errReader = new BufferedReader(new InputStreamReader(
                p.getErrorStream()));
        String line;
        while ((line = errReader.readLine()) != null) {
            System.err.println(line);
        }
    }

	public static void main(String[] args) {
		ExternalTool fb = new JLint();
		fb.configure("config/jlint.config");
		System.out.println(fb.run(null, "/home/disco/rw334/src"));
	}

	@Override
	protected Collection<String> getCommand() {
		return Arrays.asList(command);
	}
	@Override
	protected String getConfig(String key, String value) {
		return null;
	}

}
