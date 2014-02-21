package za.ac.sun.cs.intlola.processing.json;

import java.util.HashMap;
import java.util.Map;

import za.ac.sun.cs.intlola.file.Const;
import za.ac.sun.cs.intlola.processing.IOUtils;

public class SkeletonInfo {
	public String name = "";
	public FileInfo[] tests, files;
	public DataInfo[] data;
	public Map<String, String> sendPaths;
	public void buildSendPaths(String base){
		sendPaths= new HashMap<String, String>();
		for(FileInfo t : tests){
			if(t.send){
				String dir = IOUtils.joinPath(t.folder, t.pkg.split("\\."));
				sendPaths.put(IOUtils.joinPath(base, dir, t.name), Const.TEST);
			}
		}
		for(FileInfo f : files){
			if(f.send){
				String dir = IOUtils.joinPath(f.folder, f.pkg.split("\\."));
				sendPaths.put(IOUtils.joinPath(base, dir, f.name), Const.SRC);
			}
		}
	}
}
