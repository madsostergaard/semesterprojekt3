package cgi;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.*;

public class Welcome extends CGI {

	// private static final Logger log = LoggerFactory.getLogger(Welcome.class);
	private static ArrayList<String> notices;
	private static String passWord, cpr, uuid;

	private static void handleArgs(StringTokenizer t) {
		String field;
		while (t.hasMoreTokens()) {
			field = t.nextToken();
			if (field != null) {
				StringTokenizer tt = new StringTokenizer(field, "=\n\r");
				String s = tt.nextToken();
				if (s != null) {
					// log.info("First part: {}", s);
					switch (s) {
					case ("USERNAME"):
						s = tt.nextToken();
						if (s != null)
							cpr = s;
						// log.debug("CPR was set: {}", cpr);
						break;
					case "PASSWORD":
						s = tt.nextToken();
						if (s != null)
							passWord = s;
						// log.debug("Password was set: {}", passWord);
						break;
					default:
						break;
					}
				}
			}
		}
	}

	protected static void showBody() {

		try {
			notices = dtb.getNotices(uuid);
		} catch (SQLException e) {
			// log.error("Exception in retrieving notices {}", e);
			notices = null;
		}

		System.out.println("<P ALIGN=\"CENTER\">");
		// indhent indkaldelser fra databasen
		if (notices == null || notices.size() == 0) { // no data
			System.out.println("Ingen indkaldelser!</P>");
		}
		// ellers er der indkaldelser
		else {
			System.out.println("<TABLE BORDER=\"1\">");
			System.out.println("<TR>");
			System.out.println("	<TH>Dato og tid</TH>");
			System.out.println("	<TH>Detajler</TH>");
			System.out.println("</TR>");
			for (int i = 0; i < notices.size(); i++) {
				String temp = notices.get(i);

				StringTokenizer t = new StringTokenizer(temp, "&");

				String noticeID = t.nextToken(); // get
													// the
													// noticeID
													// for
													// database
													// connection
				String title = t.nextToken(); // get the
												// description
												// (which
												// is a
												// link
												// to
												// the
												// notice)
				String date = t.nextToken();
				; // get the date

				System.out.println("<TR><TD>" + date);
				System.out.println("</TD><TD>");
				System.out.println("<A HREF=\"http://su8.eduhost.dk/cgi-bin/Notices?id=" + noticeID
						+ /* do we need more attributes? */"\">");
				System.out.println(title + "</A></TD></TR>");
			}
			System.out.println("</TABLE>");
			System.out.println("</P>");
		}
		System.out.println("<P ALIGN=\"CENTER\">");
	}

	public static void main(String[] args) {
		boolean isUserValid = false;
		// hent login oplysninger
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			String[] data = { in.readLine() };
			handleArgs(new StringTokenizer(data[0], "&\n\r"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		setCookie(""/* et eller andet */);

		try {
			uuid = dtb.downloadUUID(passWord, cpr);
			isUserValid = dtb.validate(passWord, cpr, uuid);
		} catch (SQLException e) {
			// log.error("Tried to validate user {}", e);
		}
		if (isUserValid) {
			showHead();
			showMenu();
			showTitle("Denne side viser dine kommende indkaldelser til hospitalet.");
			showBody();
			showTail();
		} else {
			showLoginAgain();
		}

	}
}