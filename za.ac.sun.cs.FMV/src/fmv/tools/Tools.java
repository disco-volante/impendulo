package fmv.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Tools {

	private static String[] tools;
	private static final String TOOL_CONFIG = "config/tools.config";

	public static String[] getTools() {
		if (tools == null) {
			BufferedReader br = null;
			ArrayList<String> config = null;
			try {
				br = new BufferedReader(new FileReader(TOOL_CONFIG));
				String line = br.readLine();
				config = new ArrayList<String>();
				while (line != null) {
					config.add(line.trim());
					line = br.readLine();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				tools = config.toArray(new String[config.size()]);
			}
		}
		return tools;
	}

	public static BasicTool getTool(String toolName) {
		BasicTool tool = null;
		if (toolName.equals("Findbugs")) {
			tool = new FindBugs();
			tool.configure("config/fb.config");
		} else if (toolName.equals("JLint")) {
			tool = new JLint();
			tool.configure("config/jlint.config");
		} else if (toolName.equals("PMD")) {
			tool = new PMD();
			tool.configure("config/pmd.config");
		}
		return tool;
	}
}
