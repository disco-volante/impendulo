package fmv;

import java.util.HashMap;
import java.util.List;

public class Version {

	private List<Integer> lineList;

	private List<DiffAction> diff = null;

	private Status status = Status.UNKNOWN;

	private String output = "";

	private HashMap<String, String> reports;

	private int warningCount = 0;
	
	private String annotation = "";

	public Version(List<Integer> lineList) {
		this.setLineList(lineList);
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setStatus(String status) {
		this.status = Status.parse(status);
	}

	public Status getStatus() {
		return status;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public String getOutput() {
		return output;
	}

	public void setReport(String tool, String report) {
		reports.put(tool, report);
	}

	public String getReport(String tool) {
		return reports.get(tool);
	}

	public int getWarningCount() {
		return warningCount;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setLineList(List<Integer> lineList) {
		this.lineList = lineList;
	}

	public List<Integer> getLineList() {
		return lineList;
	}

	public void setDiff(List<DiffAction> diff) {
		this.diff = diff;
	}

	public List<DiffAction> getDiff() {
		return diff;
	}

}
