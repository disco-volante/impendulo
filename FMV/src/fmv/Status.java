package fmv;

import java.awt.Color;

import fmv.TablePane.ArchiveData;

public enum Status {

	UNKNOWN(Color.black, 15, "Unknown", "unknown"),
	NOCOMPILE(Color.red, 14, "No compile", "nocompile"),
	E_ERRS(Color.cyan, 12, "Easy errors", "eerrs"),
	E_FAIL(Color.cyan, 10, "Easy failures", "efail"),
	A_ERRS(Color.yellow, 8, "All errors", "aerrs"),
	A_FAIL(Color.yellow, 6, "All failures", "afail"),
	TIMEOUT(Color.magenta, 4, "Timeout", "timeout"),
	OK(Color.green, 2, "No problems", "ok");

	private Color color;

	private double y;

	private String message;

	private String name;

	private Status(Color color, int y, String message, String name) {
		this.color = color;
		this.y = y * 1.0 / 16;
		this.message = message;
		this.name = name;
	}

	public static Status parse(String s) {
		for (Status st : values()) {
			if (st.name.equals(s)) {
				return st;
			}
		}
		return UNKNOWN;
	}

	public Color getColor() {
		return color;
	}

	public double getY() {
		return y;
	}

	public String getMessage() {
		return message;
	}

	public String getName() {
		return name;
	}

	public void setData(ArchiveData data) {
		if (this == NOCOMPILE) {
			data.nocomp = 1 + ((data.nocomp < 0) ? 0 : data.nocomp);
		} else if (this == E_ERRS) {
			data.eerrs = 1 + ((data.eerrs < 0) ? 0 : data.eerrs);
		} else if (this == E_FAIL) {
			data.efail = 1 + ((data.efail < 0) ? 0 : data.efail);
		} else if (this == A_ERRS) {
			data.aerrs = 1 + ((data.aerrs < 0) ? 0 : data.aerrs);
		} else if (this == A_FAIL) {
			data.afail = 1 + ((data.afail < 0) ? 0 : data.afail);
		} else if (this == TIMEOUT) {
			data.timeout = 1 + ((data.timeout < 0) ? 0 : data.timeout);
		} else if (this == OK) {
			data.ok = 1 + ((data.ok < 0) ? 0 : data.ok);
		}
		data.total = 1 + ((data.total < 0) ? 0 : data.total);
	}
}
