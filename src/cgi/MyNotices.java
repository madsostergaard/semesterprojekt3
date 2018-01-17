package cgi;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import serverside.Notice;
import serverside.Notices;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class MyNotices extends CGI {

	private static String id = "";
	private static String title = "";
	private static String details = "";
	private static String date = "";
	// private static final Logger log = LoggerFactory.getLogger(Notices.class);

	private static void handleArgs(StringTokenizer t) {
		String field;
		while (t.hasMoreTokens()) {
			field = t.nextToken();
			if (field != null) {
				StringTokenizer tt = new StringTokenizer(field, "=\n\r");
				String s = tt.nextToken();
				if (s != null) {
					// log.info("First part: {}", s);
					if (s.equals("id")) {
						s = tt.nextToken();
						if (s != null)
							id = s;
						// log.debug("ID was set: {}", id);
					}
				}
			}
		}
	}
	
	protected static void showMenu(){
		System.out.println("<nav>");
		System.out.println("");
		System.out.println("<ul>");
		System.out.println("<li><a href=\"http://su3.eduhost.dk/cgi-bin/Welcome?redirect=1\">Hjem</a></li>");
		System.out.println("<li><a class=\"active\" href=\"http://su3.eduhost.dk/cgi-bin/MyNotices\">Indkaldelser</a></li>");
		System.out.println("<li><a href=\"#myPage\">Min side</a></li>");
		System.out.println("<li style=\"float:right\"><a href=\"http://su3.eduhost.dk/index.html?logout=1\">Log ud</a></li>");
		System.out.println("<li style=\"float:right\"><a href=\"http://su3.eduhost.dk/helpSite.html\">Hjælp</a></li>");
		System.out.println("</ul>");
		System.out.println("</nav>");
	}

	protected static void showBody() {
		try {
			System.out.println("<div class=\"group\">	");
			System.out.println("	<section> <h2> Her kan du se dine indkaldelser. </h2>");
			System.out.println("	");
			String uuid = dtb.getUUIDFromSession(session);
			Notices local = dtb.getNotices(uuid);
			ArrayList<Notice> listLocal = local.getNotice();
			if((listLocal!=null) && listLocal.size() > 0){
				
				for(int k = 0; k<listLocal.size(); k++){
					Notice t = listLocal.get(k);
					System.out.println("	<article class=\"notice\">");
					System.out.println("");
					System.out.println("		<div style=\"width: 100%; overflow: hidden;\">");
					System.out.println("			<h3 style=\"width: 400px; float: left;\"> "+t.getTitle()+" </h3>");
					System.out.println("			<p style=\"width: 400px; float: left;\"> "+t.getDate()+" </p>");
					System.out.println("			<p style=\"width: 400px; float: left;\"> Hospital 3 </p>");
					System.out.println("			");
					System.out.println("			<button id=\"timeBtn\" class=\"buttonTime\">Ændre tid</button>");
					System.out.println("");
					System.out.println("");
					System.out.println("			<!-- The Modal -->");
					System.out.println("			<div id=\"myModal\" class=\"modal\">");
					System.out.println("");
					System.out.println("			<!-- Modal content -->");
					System.out.println("				<div class=\"modal-content\">");
					System.out.println("					<span class=\"close\">&times;</span>");
					System.out.println("					<p class = \"modalInputNB\" style=\"margin-right:1%;\">Uge Nr. </p>");
					System.out.println("					<input class = \"modalInputNB\" type=\"number\" name=\"quantity\" min=\"1\" max=\"52\">");
					System.out.println("					<input class = \"modalInputNB\" type=\"submit\" value=\"Send\">");
					System.out.println("					<div class=\"modalBox\">");
					System.out.println("						<div class=\"modalBoxInUpLeft\">");
					System.out.println("							(9-12)");
					System.out.println("						</div>");
					System.out.println("						<div class=\"modalBoxInUpRight\">");
					System.out.println("							(13-16)");
					System.out.println("						</div>");
					System.out.println("");
					System.out.println("						<div class=\"modalBoxInDownLeft\">");
					System.out.println("							Mandag <input type=\"checkbox\" name=\"vehicle1\" value=\"Bike\">");
					System.out.println("							<hr class=\"rulerkasse\">");
					System.out.println("							Tirsdag <input type=\"checkbox\" name=\"vehicle1\" value=\"Bike\">");
					System.out.println("							<hr class=\"rulerkasse\">");
					System.out.println("							Onsdag <input type=\"checkbox\" name=\"vehicle1\" value=\"Bike\">");
					System.out.println("							<hr class=\"rulerkasse\">");
					System.out.println("							Torsdag <input type=\"checkbox\" name=\"vehicle1\" value=\"Bike\">");
					System.out.println("							<hr class=\"rulerkasse\">");
					System.out.println("							Fredag <input type=\"checkbox\" name=\"vehicle1\" value=\"Bike\">");
					System.out.println("						</div>");
					System.out.println("");
					System.out.println("						<div class=\"modalBoxInDownRight\">");
					System.out.println("							Mandag <input type=\"checkbox\" name=\"vehicle1\" value=\"Bike\">");
					System.out.println("							<hr class=\"rulerkasse\">");
					System.out.println("							Tirsdag <input type=\"checkbox\" name=\"vehicle1\" value=\"Bike\">");
					System.out.println("							<hr class=\"rulerkasse\">");
					System.out.println("							Onsdag <input type=\"checkbox\" name=\"vehicle1\" value=\"Bike\">");
					System.out.println("							<hr class=\"rulerkasse\">");
					System.out.println("							Torsdag <input type=\"checkbox\" name=\"vehicle1\" value=\"Bike\">");
					System.out.println("							<hr class=\"rulerkasse\">");
					System.out.println("							Fredag <input type=\"checkbox\" name=\"vehicle1\" value=\"Bike\">");
					System.out.println("						</div>");
					System.out.println("					</div>");
					System.out.println("					<div class = \"modalBox2\">");
					System.out.println("						<p class = \"modalBox2Times\">Valgte tider</p>");
					System.out.println("						<hr style=\"margin-top:-5%;\" class=\"rulerkasse\">");
					System.out.println("						<p>Man. D.22.01.2018 - (9-12)</p>");
					System.out.println("						<p>Man. D.22.01.2018 - (13-16)</p>");
					System.out.println("						<p>Fre. D.26.01.2018 - (9-12)</p>");
					System.out.println("						<p>Fre. D.26.01.2018 - (13-16)</p>");
					System.out.println("						");
					System.out.println("						");
					System.out.println("					</div>");
					System.out.println("					");
					System.out.println("				</div>");
					System.out.println("");
					System.out.println("			</div>");
					System.out.println("");
					System.out.println("");
					System.out.println("");
					System.out.println("			<button class=\"buttonCancel\">Aflys tid</button>");
					System.out.println("");
					System.out.println("			<p style=\"margin-left: 400px;\"> "+t.getDetails()+" </div>");
					System.out.println("		</div>");
					System.out.println("		");
					System.out.println("	</article>");
				}
			}
			

			Notices n;

			n = dtb.getSessionNotices(session);

			ArrayList<Notice> list = n.getNotice();

			if (list == null || list.size() == 0) { // no data
				System.out.println("<p>Ingen indkaldelser!</P>");
			}
			// ellers er der indkaldelser
			else {
				int i;
				for (i = 0; i < list.size() - 1; i++) {
					Notice temp = list.get(i);

					String url = temp.getURL();
					String title = temp.getTitle();
					String date = temp.getDate();

					System.out.println("	<article class=\"notice\">");
					System.out.println("");
					System.out.println("		<div style=\"width: 100%; overflow: hidden;\">");
					System.out.println("			<h3 style=\"width: 400px; float: left;\">" + title + "</h3>");
					System.out.println("			<p style=\"width: 400px; float: left;\"> " + date + " </p>");
					System.out.println("			<button id=\"reDirect\" class=\"buttonTime\" href=\"" + url
							+ "\">Se mere</button>");
					System.out.println("		</div>");
					System.out.println("	</article>");
					System.out.println("	");

				}

				Notice temp = list.get(i);
				String url = temp.getURL();
				String title = temp.getTitle();
				String date = temp.getDate();

				System.out.println("	<article class=\"lastNotice\">");
				System.out.println("");
				System.out.println("		<div style=\"width: 100%; overflow: hidden;\">");
				System.out.println("			<h3 style=\"width: 400px; float: left;\">" + title + "</h3>");
				System.out.println("			<p style=\"width: 400px; float: left;\"> " + date + " </p>");
				System.out.println("			<p style=\"width: 400px; float: left;\"> [Hospital - Afdeling] </p>");
				System.out.println("			<button id=\"reDirect\" class=\"buttonTime\" href=\"" + url
						+ "\">Se mere</button>");
				System.out.println("		</div>");
				System.out.println("	</article>");
				System.out.println("	");

			}
			System.out.println("	</section>");
			System.out.println("</div>");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	private static void handleNoticeDetails(String temp) {
		StringTokenizer t = new StringTokenizer(temp, "&");
		if (t.hasMoreTokens()) {
			title = t.nextToken();
			details = t.nextToken();
			date = t.nextToken();
		} else
			; // log.warn("No elements!");
	}

	public static void main(String[] args) {

		if (args.length > 0 && args[0].length() > 0) {
			cookie = args[0];
			String temp = args[0].substring(args[0].indexOf(" ")+1);
			handleCookies(new StringTokenizer(temp, ";\n\r"));
		}
		
		System.out.println(cookie);
		System.out.println(session);

		showHead();
		showMenu();
		showBody();
		showTail();

	}
}
