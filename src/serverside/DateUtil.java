package serverside;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Helper functions for handling dates
 * 
 * @author Mads Østergaard
 *
 */
public class DateUtil {

	/** the date pattern used for conversion. */
	private static final String DATE_TO_XML = "dd.MM.yy";
	private static final String DATE_TO_DATABASE = "dd.MM.yyyy";

	/** the date formatter */
	private static final DateTimeFormatter TO_XML = DateTimeFormatter.ofPattern(DATE_TO_XML);
	private static final DateTimeFormatter TO_DATABASE = DateTimeFormatter.ofPattern(DATE_TO_DATABASE);
	/**
	 * Returns the given date as a well formatted String. The above defined
	 * {@link DateUtil#DATE_TO_XML} is used.
	 * 
	 * @param date
	 *            the date to be returned as a String
	 * @return formatted string
	 */
	public static String toXML(LocalDate date) {
		if (date == null)
			return null;
		return TO_XML.format(date);
	}
	
	/**
	 * Returns the given date as a well formatted String. The above defined
	 * {@link DateUtil#DATE_TO_XML} is used.
	 * 
	 * @param date
	 *            the date to be returned as a String
	 * @return formatted string
	 */
	public static String toDatabase(LocalDate date) {
		if (date == null)
			return null;
		return TO_DATABASE.format(date);
	}

	/**
	 * Converts a String in the format of the defined
	 * {@link DateUtil#DATE_TO_XML} to a {@link LocalDate} object.
	 * 
	 * Returns null if the String could not be converted.
	 * 
	 * @param dateString
	 *            the date as String
	 * @return the date object or null if it could not be converted
	 */
	public static LocalDate parse(String dateString) {
		try {
			return TO_XML.parse(dateString, LocalDate::from);
		} catch (DateTimeParseException e) {
			return null;
		}
	}

	/**
	 * Checks the String whether it is a valid date.
	 * 
	 * @param dateString
	 * @return true if the String is a valid date
	 */
	public static boolean validDate(String dateString) {
		// try to parse the string
		return TO_XML.parse(dateString) != null;
	}
}
