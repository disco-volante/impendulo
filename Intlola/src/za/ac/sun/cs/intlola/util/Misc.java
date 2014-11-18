package za.ac.sun.cs.intlola.util;

import java.text.DateFormat;
import java.util.Date;

public class Misc {
	public static String TimeString(long time) {
		return DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.SHORT).format(new Date(time));
	}
}
