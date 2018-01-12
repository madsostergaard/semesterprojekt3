package cgi;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.*;

import serverside.Client;
import serverside.Notice;
import serverside.Notices;

/**
 * CGI klasse til at håndtere login.
 * 
 * @author Mads Østergaard
 *
 */
public class Welcome extends CGI {

	// private static final Logger log = LoggerFactory.getLogger(Welcome.class);
	// private static ArrayList<String> notices;
	private static String passWord, cpr, uuid, sessionid;
	private static Client client;

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
		Notices notices = null;
		try {
			notices = dtb.getSessionNotices(sessionid);

			ArrayList<Notice> list = new ArrayList<>();
			if (notices != null)
				list = notices.getNotice();

			System.out.println("<P ALIGN=\"CENTER\">");
			// indhent indkaldelser fra databasen
			if (list == null || list.size() == 0) { // no data
				System.out.println("Ingen indkaldelser!</P>");
			}
			// ellers er der indkaldelser
			else {
				System.out.println("<TABLE BORDER=\"1\">");
				System.out.println("<TR>");
				System.out.println("	<TH>Dato og tid</TH>");
				System.out.println("	<TH>Detajler</TH>");
				System.out.println("</TR>");
				for (int i = 0; i < list.size(); i++) {
					Notice temp = list.get(i);

					String url = temp.getURL();
					String title = temp.getTitle();
					String date = temp.getDate();

					System.out.println("<TR><TD>" + date);
					System.out.println("</TD><TD>");
					System.out.println("<A HREF=\"http://" + url
							+ /* do we need more attributes? */"\">");
					System.out.println(title + "</A></TD></TR>");
				}
				System.out.println("</TABLE>");
				System.out.println("</P>");
			}
			System.out.println("<P ALIGN=\"CENTER\">");
		} catch (SQLException e) {
			notices = null;
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

		if (args.length > 1 && args[0] != null && args[0].length() > 0) {
			cookie = args[0];
			handleCookies(new StringTokenizer(cookie, ";\n\r"));
		}

		setCookie(""/* et eller andet */);

		try {
			uuid = dtb.downloadUUID(passWord, cpr);
			isUserValid = dtb.validate(passWord, cpr, uuid);
		} catch (SQLException e) {
			// log.error("Tried to validate user {}", e);
		}
		if (isUserValid) {
			if (session == null){
				sessionid=dtb.createSession(uuid);
				System.out.println("Set-Cookie: __session=" + sessionid);
			}
				
			client = new Client();
			String status = client.sendRequests(uuid);

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