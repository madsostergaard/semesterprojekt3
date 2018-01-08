package serverside;

//import java.sql.Date;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Notice {

	private Date date; // Gemmes det som et Date objekt i databasen eller er det bare en string?
	private DateFormat format = new SimpleDateFormat("mmddyy"); // Skal vi have tiden med, eller ny attribut?
	private String time;
	private String title;
	private String URL;
	private int hospID;

	Notice(String title, String URL, String date, int hospID) throws ParseException {
		this.date = format.parse(date);
		this.title = title;
		this.URL = URL;
		this.hospID = hospID;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String URL) {
		this.URL = URL;
	}

	public int getUuid() {
		return hospID;
	}

	public void setUuid(int hospID) {
		this.hospID = hospID;
	}

}
