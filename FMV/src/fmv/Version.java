package fmv;

import java.util.HashMap;
import java.util.List;

public class Version {

	private List<Integer> lineList;

	private List<DiffAction> diff = null;

	private Status status = Status.UNKNOWN;

	private String output = "";

	private HashMap<String, String> reports;

	private String annotation = "";

	public Version(final List<Integer> lineList) {
		setLineList(lineList);
	}

	public String getAnnotation() {
		return annotation;
	}

	public List<DiffAction> getDiff() {
		return diff;
	}

	public List<Integer> getLineList() {
		return lineList;
	}

	public String getOutput() {
		return output;
	}

	public String getReport(final String tool) {
		return getReports().get(tool);
	}

	public HashMap<String, String> getReports() {
		if (reports == null) {
			reports = new HashMap<String, String>();
		}
		return reports;
	}

	public Status getStatus() {
		return status;
	}

	public void setAnnotation(final String annotation) {
		this.annotation = annotation;
	}

	public void setDiff(final List<DiffAction> diff) {
		this.diff = diff;
	}

	public void setLineList(final List<Integer> lineList) {
		this.lineList = lineList;
	}

	public void setOutput(final String output) {
		this.output = output;
	}

	public void setReport(final String tool, final String report) {
		getReports().put(tool, report);
	}

	public void setStatus(final Status status) {
		this.status = status;
	}

	public void setStatus(final String status) {
		this.status = Status.parse(status);
	}

}
