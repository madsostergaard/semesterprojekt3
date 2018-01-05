package cgi;
import java.sql.SQLException;
import java.util.StringTokenizer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class Notices extends CGI {

	private static String id = "";
	private static String title = "";
	private static String details = "";
	private static String date = "";
//	private static final Logger log = LoggerFactory.getLogger(Notices.class);

	private static void handleArgs(StringTokenizer t) {
		String field;
		while (t.hasMoreTokens()) {
			field = t.nextToken();
			if (field != null) {
				StringTokenizer tt = new StringTokenizer(field, "=\n\r");
				String s = tt.nextToken();
				if (s != null) {
//					log.info("First part: {}", s);
					if (s.equals("id")) {
						s = tt.nextToken();
						if (s != null)
							id = s;
//						log.debug("ID was set: {}", id);
					}
				}
			}
		}
	}

	protected static void showBody() {
		System.out.println("");
		System.out.println("<P ALIGN=\"CENTER\">");
		System.out.println("<TABLE BORDER=\"1\">");
		System.out.println("");
		System.out.println("<TR>");
		System.out.println("	<TD>Dato og tid</TD><TD>"+date+"</TD>");
		System.out.println("</TR>");
		System.out.println("<TR>");
		System.out.println("	<TD>Detaljer</TD><TD>"+details+"</TD>");
		System.out.println("</TR>");
		System.out.println("");
		System.out.println("<P ALIGN=\"CENTER\">");
		System.out.println("<A HREF=\"su8.eduhost.dk/cgi-bin/cancel?id="+id+"\"><INPUT type=\"submit\" value=\"Afbestil\"/>");
		System.out.println("<A HREF=\"su8.eduhost.dk/cgi-bin/change?id="+id+"\"><INPUT type=\"submit\" value=\"Lav behandling om\"/></A>");
		System.out.println("</P>");
		System.out.println("");
	}

	private static void handleNoticeDetails(String temp) {
		StringTokenizer t = new StringTokenizer(temp,"&");
		if(t.hasMoreTokens()){
			title = t.nextToken();		
			details = t.nextToken();
			date = t.nextToken();
		}
		else; //log.warn("No elements!");
	}

	public static void main(String[] args) {
		if (args.length > 0 && args[0].length() > 0) {
			handleArgs(new StringTokenizer(args[0], "&\n\r"));
		}
		try {
			String temp = "";
			if (!id.isEmpty()) {
				temp = dtb.getNoticeDetails(Integer.valueOf(id));
				handleNoticeDetails(temp);
			}

		} catch (SQLException e) {
			//log.error("Tried to get notice details: {}", e);
		}

		showHead();
		showMenu();
		showTitle("Indkaldelse: " + title);
		showBody();
		showTail();

	}
}
