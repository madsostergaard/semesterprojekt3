package cgi;

import java.util.StringTokenizer;

import serverside.DatabaseConnection;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class CGI {

	// private static Person p;
	protected static DatabaseConnection dtb = DatabaseConnection.getInstance();
	protected static String cookie;
	protected static String session;
	// protected static final Logger log = LoggerFactory.getLogger(CGI.class);

	protected static void handleCookies(StringTokenizer t) {
		String field;
		while (t.hasMoreTokens()) {
			field = t.nextToken();
			// System.out.println(field);
			if (field != null) {
				field.trim();
				StringTokenizer tt = new StringTokenizer(field, "=\n\r");
				String s = tt.nextToken();
				// System.out.println(s);
				if (s.equals("__session")) {
					s = tt.nextToken();
					// System.out.println(s);
					if (s != null)
						session = s;
				}
			}
		}
	}

	/**
	 * Prints the head of the HTML page.
	 */
	protected static void showHead() {
		System.out.println("Content-Type: text/html");
		System.out.println();
		System.out.println("<!DOCTYPE html>");
		System.out.println("<html lang=\"dk\">");
		System.out.println("<head>");
		System.out.println("");
		System.out.println("<!--<script type=\"text/javascript\" src=\"myNotices.js\"></script>-->");
		System.out.println("");
		System.out.println("<meta charset=\"UTF-8\">");
		System.out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
		System.out.println("<link rel=\"stylesheet\" href=\"styles.css\">");
		System.out.println("<title>Hospital 3</title>");
		System.out.println("");
		System.out.println("</head>");
		System.out.println("<body>");
		System.out.println("");
		System.out.println("<header>");
		System.out.println("<div class=\"group\">");
		System.out.println("<a href=\"http://su3.eduhost.dk/cgi-bin/Welcome?redirect=1\"><img class=\"logo\" src=\"logo.png\"/></a> ");
		System.out.println("<h1 class=\"overskrift\">Hospital 3</h1>");
		System.out.println("</div>");
		System.out.println("</header>");
	}

	protected static void showLoginAgain() {
		showHead();

		System.out.println(
				"<p>Du bliver nu sendt tilbage til loginsiden.<a href=\"http://su8.eduhost.dk/index\">Tryk her hvis du ikke bliver sendt tilbage automatisk</a></p>");
		showTail();
	}

	/**
	 * Prints the end of the HTML page
	 */
	protected static void showTail() {
		System.out.println("");
		System.out.println("<footer>");
		System.out.println("<p>&copy; 2017 Hospital 3. All rights reserved.</p>");
		System.out.println("</footer>");
		System.out.println("");
		System.out.println("<script>");
		System.out.println("// Get the modal");
		System.out.println("var modal = document.getElementById('myModal');");
		System.out.println("");
		System.out.println("// Get the button that opens the modal");
		System.out.println("var btn = document.getElementById(\"timeBtn\");");
		System.out.println("");
		System.out.println("// Get the <span> element that closes the modal");
		System.out.println("var span = document.getElementsByClassName(\"close\")[0];");
		System.out.println("");
		System.out.println("// When the user clicks the button, open the modal ");
		System.out.println("btn.onclick = function() {");
		System.out.println("    modal.style.display = \"block\";");
		System.out.println("}");
		System.out.println("");
		System.out.println("// When the user clicks on <span> (x), close the modal");
		System.out.println("span.onclick = function() {");
		System.out.println("    modal.style.display = \"none\";");
		System.out.println("}");
		System.out.println("");
		System.out.println("// When the user clicks anywhere outside of the modal, close it");
		System.out.println("window.onclick = function(event) {");
		System.out.println("    if (event.target == modal) {");
		System.out.println("        modal.style.display = \"none\";");
		System.out.println("    }");
		System.out.println("}");
		System.out.println("</script>");
		System.out.println("");
		System.out.println("</body>");
		System.out.println("");
		System.out.println("</html>");
	}

	/**
	 * Is needed for printing the menu, which is the same on all pages
	 */
	protected static void showMenu() {
		System.out.println("<nav>");
		System.out.println("");
		System.out.println("<ul>");
		System.out.println("<li><a class=\"active\" href=\"#home\">Hjem</a></li>");
		System.out.println("<li><a href=\"#notices\">Indkaldelser</a></li>");
		System.out.println("<li><a href=\"#myPage\">Min side</a></li>");
		System.out.println("<li style=\"float:right\"><a href=\"#logout\">Log ud</a></li>");
		System.out.println("<li style=\"float:right\"><a href=\"#help\">Hjælp</a></li>");
		System.out.println("</ul>");
		System.out.println("</nav>");
		System.out.println("");
		if (cookie == null)
			System.out.println("<A HREF=\"index.html\"><INPUT type=\"submit\" value=\"Log ud\"/></A>");
		else
			System.out.println("<A HREF=\"su8.eduhost.dk/cgi-bin/logout=?cookie=" + cookie
					+ "\"><INPUT type=\"submit\" value=\"Log ud\"/></A>");
	}

	protected static void showTitle(String title) {
		System.out.println("<H1 ALIGN=\"CENTER\">" + title + "</H1>");
		System.out.println("<HR/>");
	}

	/**
	 * This method prints the body part of each page. <br>
	 * This is the part of the pages, which is different.
	 */
	protected static void showBody() {
		throw new UnsupportedOperationException("This operation is not allowed for this class!");
	};

	protected static void setCookie(String mCookie) {
		cookie = mCookie;
	}

	public static void main(String[] args) {

		// log.info("Showing default page");

		showHead();
		showTitle("Default side");
		showMenu();
		showTail();
	}
}