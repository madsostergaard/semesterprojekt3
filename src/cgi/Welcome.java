package cgi;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.*;
import java.time.OffsetDateTime;
import java.time.*;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import serverside.Client;

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

					case "REDIRECT":
						break;
					default:
						break;
					}
				}
			}
		}
	}

	protected static void showMenu() {
		System.out.println("<nav>");
		System.out.println("");
		System.out.println("<ul>");
		System.out.println("<li><a class=\"active\" href=\"#home\">Hjem</a></li>");
		System.out.println("<li><a href=\"http://su3.eduhost.dk/cgi-bin/MyNotices\">Indkaldelser</a></li>");
		System.out.println("<li><a href=\"#myPage\">Min side</a></li>");
		System.out.println(
				"<li style=\"float:right\"><a href=\"http://su3.eduhost.dk/index.html?logout=1\">Log ud</a></li>");
		System.out.println("<li style=\"float:right\"><a href=\"http://su3.eduhost.dk/helpSite.html\">Hjælp</a></li>");
		System.out.println("</ul>");
		System.out.println("</nav>");
	}

	protected static void showBody() {
		String name = dtb.getNameFromUUID(uuid);

		System.out.println("<div class=\"group\">");
		System.out.println("	<section>");
		System.out.println(
				"	<h2 align=\"center\">Velkommen til din side " + name + "! Her kan følge din behandling</h2>");
		System.out.println("	<article>Velkommen til DTU Ballerup Universitetshospital.");
		System.out.println("	Her har du blandt andet mulighed for at se dine kommende indkaldeser. ");
		System.out.println("	P&aring; knappen \"Min side\" kan du se og &aelig;ndre dine kontaktoplysninger mm.");
		System.out.println(
				"	P&aring; knappen \"Indkaldelser\" kan du f&aring; det fulde overblik over alle dine indkaldelser. Du har blandt andet mulighed for at aflyse og &aelig;ndre tider p&aring; dine kommende indkaldelser.<br>");
		System.out.println(
				"	Hvis du har problemer og &oslash;nsker yderligere information, kan du altid trykke p&aring; \"Hj&aelig;lp\"-knappen oppe i højre hjørne og l&aelig;se n&aelig;rmere. </article>");
		System.out
				.println("	<article><iframe class=\"centerframe\" src=\"https://www.youtube.com/embed/Tuv4q5mgHOQ\">");
		System.out.println("</iframe></article>	");
		System.out.println("	</section>");
		System.out.println("</div>");
	}

	public static void main(String[] args) {
		boolean isUserValid = false;
		// hent login oplysninger

		if (args.length > 2 && args[1] != null && args[1].length() > 0) {
			// redirect.
		} else {

			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			try {
				String[] data = { in.readLine() };
				handleArgs(new StringTokenizer(data[0], "&\n\r"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (args.length > 1 && args[0] != null && args[0].length() > 0) {
			cookie = args[0];
			handleCookies(new StringTokenizer(cookie, ";\n\r"));
		}

		try {
			uuid = dtb.downloadUUID(passWord, cpr);
			isUserValid = dtb.validate(passWord, cpr, uuid);
		} catch (SQLException e) {
			// log.error("Tried to validate user {}", e);
		}
		if (isUserValid) {
			if (session == null) {
				sessionid = dtb.createSession(uuid);
				OffsetDateTime oneHourFromNow = OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofHours(1));

				String cookieExpires = DateTimeFormatter.RFC_1123_DATE_TIME.format(oneHourFromNow);
				System.out.println("Set-Cookie: __session=" + sessionid + "; expires=" + cookieExpires + ";");
			} else {
				sessionid = session;
			}

			client = new Client();
			String status = client.sendRequests(uuid);

			showHead();
			showMenu();
			showBody();
			showTail();
		} else {
			showLoginAgain();
		}

	}
}