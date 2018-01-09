package serverside;

import java.util.ArrayList;

/**
 * Helper functions for handling dates
 * 
 * @author Mads Østergaard
 *
 */
public class DateUtil {

	private static ArrayList<String> xml = new ArrayList<>();

	/**
	 * Returns the given date as a well formatted String.
	 * 
	 * @param date1
	 *            returns an ArrayList [<date> , <time>] "MMDDYY" & "HH:MM"
	 * @return formatted string
	 */

	public static ArrayList<String> toXML(String date1) {
		if (date1 == null)
			return null;

		String a;
		String b;

		a = date1.substring(5, 7);
		a += date1.substring(8, 10);
		a += date1.substring(2, 4);

		b = date1.substring(11, 13);
		b += ":";
		b += date1.substring(14, 16);

		xml.add(a);
		xml.add(b);
		return xml;
	}

	/**
	 * Returns the given date as a well formatted String to the Database.
	 * 
	 * @param date
	 *            The date to be returned as a String
	 * @return formatted string
	 */

	public static String toDatabase(String date, String time) {
		if (date == null)
			return null;
		String DBDate;

		DBDate = "20";
		DBDate += date.substring(4, 6);
		DBDate += "-";
		DBDate += date.substring(0, 2);
		DBDate += "-";
		DBDate += date.substring(2, 4);
		DBDate += " ";
		DBDate += time.substring(0, 2);
		DBDate += ":";
		DBDate += time.substring(3, 5);

		return DBDate;
	}
}
