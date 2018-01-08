package serverside;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;


/**
 * Helper functions for handling dates
 * 
 * @author Mads Ã˜stergaard
 *
 */
public class DateUtil {

	/** the date pattern used for conversion. */
	private static final String DATE_TO_DATABASE = "yyyy-MM-SS HH:MM:SS";
	private static ArrayList<String> xml = new ArrayList<>();

	/** the date formatter */
	private static final DateTimeFormatter TO_DATABASE = DateTimeFormatter.ofPattern(DATE_TO_DATABASE);
	
	/**
	 * Returns the given date as a well formatted String. The above defined
	 * {@link DateUtil#DATE_TO_XML} is used.
	 * 
	 * @param date1
	 *            the date to be returned as a String
	 * @return formatted string
	 */
	
	// Metode der tager datbase date objektet og laver om til en arraylist med dato og tid
	public static ArrayList<String> toXML(String date1) {
		if (date1 == null)
			return null;
		
		xml.add(date1.toString().substring(0,10));
		System.out.println("Dato: " + xml.get(0));
		xml.add(date1.toString().substring(11));
		System.out.println("Tid: " + xml.get(1));
		return xml;
	}
	
	/**
	 * Returns the given date as a well formatted String to the Database. The above defined
	 * {@link DateUtil#DATE_TO_DATABSE} is used.
	 * 
	 * @param date
	 *            the date to be returned as a String
	 * @return formatted string
	 */
	
	// Metode der sætter to XML string (dato/tid) til dato objekt til database (mm.dd.yy)
	public static LocalDate toDatabase(String date) {
		if (date == null) return null;
		if (validDate(date))
		return parse(date);
		return null;
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
			return TO_DATABASE.parse(dateString, LocalDate::from);
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
		return TO_DATABASE.parse(dateString) != null;
	}
}
