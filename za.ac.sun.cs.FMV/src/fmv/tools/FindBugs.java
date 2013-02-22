package fmv.tools;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class FindBugs extends JarTool {
	private static final String FB_HOME = "/home/disco/prog/findbugs";
	private static final String[] command = new String[] {FB_HOME+"/lib/findbugs.jar", "-textui" };

	@Override
	protected String getConfig(String key, String value) {
		String config = null;
		if (!value.equals("none") && !value.equals("")) {
			if (key.equals("outFormat") || key.equals("priority")) {
				config = "-" + value;
			} else if (key.equals("auxclasspath")) {
				config = "-auxclasspath ! " + value;
			} else if (key.equals("nested")) {
				config = "-nested:" + value;
			} else if (key.equals("relaxed")) {
				if (value.equals("true")) {
					config = "-relaxed";
				}
			} else {
				throw new IllegalArgumentException(key + " : " + value);
			}
		}
		return config;
	}

	public static void main(String[] args) {
		BasicTool fb = new FindBugs();
		fb.configure("config/fb.config");
		try {
			System.out.println(fb.run(null, "src"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected Collection<String> getCommand() {
		return Arrays.asList(command);
	}
}
