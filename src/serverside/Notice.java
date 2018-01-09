package serverside;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Notice {

	private String date;
	private String title;
	private String URL;
	private int hospID;

	Notice(String title, String URL, String date, int hospID) throws ParseException {
		this.date = date;
		this.title = title;
		this.URL = URL;
		this.hospID = hospID;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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

	public int getHospID() {
		return hospID;
	}

	public void setHospID(int hospID) {
		this.hospID = hospID;
	}

}
