package fmv.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Tools {

	private static String[] tools;
	private static final String TOOL_CONFIG = "config/tools.config";

	public static BasicTool getTool(final String toolName) {
		BasicTool tool = null;
		if (toolName.equals("FindBugs")) {
			tool = new FindBugs();
			tool.configure("config/fb.config");
		} else if (toolName.equals("JLint")) {
			tool = new JLint();
			tool.configure("config/jlint.config");
		} else if (toolName.equals("PMD")) {
			tool = new PMD();
			tool.configure("config/pmd.config");
		} else if (toolName.equals("Lint4J")) {
			tool = new Lint4J();
			tool.configure("config/lint4j.config");
		}
		return tool;
	}

	public static String[] getTools() {
		if (Tools.tools == null) {
			BufferedReader br = null;
			ArrayList<String> config = null;
			try {
				br = new BufferedReader(new FileReader(Tools.TOOL_CONFIG));
				String line = br.readLine();
				config = new ArrayList<String>();
				while (line != null) {
					config.add(line.trim());
					line = br.readLine();
				}
			} catch (final FileNotFoundException e) {
				e.printStackTrace();
			} catch (final IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}
				Tools.tools = config.toArray(new String[config.size()]);
			}
		}
		return Tools.tools;
	}
}
