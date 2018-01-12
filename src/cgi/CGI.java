package cgi;
import java.util.StringTokenizer;

import serverside.DatabaseConnection;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class CGI{
	
	//private static Person p;
	protected static DatabaseConnection dtb = DatabaseConnection.getInstance();
	protected static String cookie;
	protected static String session;
	//protected static final Logger log = LoggerFactory.getLogger(CGI.class);
	
	protected static void handleCookies(StringTokenizer t) {
	      String field;
	      while ( t.hasMoreTokens() ) {
	         field = t.nextToken();
	         if (field != null) {
	            field.trim();
	            StringTokenizer tt = new StringTokenizer(field,"=\n\r");
	            String s = tt.nextToken();
	            if ( s.equals("__session") ) {
	               s = tt.nextToken();
	               if ( s != null )
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
		System.out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2//EN\">");
		System.out.println("<HTML>");
		System.out.println("<HEAD>");
		System.out.println("	<TITLE>iPatientHealth</TITLE>");
		System.out.println("<META http-equiv=\"content-type\" content=\"text/html; charset=iso-8859-1\">");
		System.out.println("<META http-equiv=\"Pragma\" content=\"no-cache\">");
		System.out.println("<META http-equiv=\"expires\" content=\"0\">");
		System.out.println("</HEAD>");
		System.out.println("<BODY>");
	}
	
	protected static void showLoginAgain() {
		System.out.println("Content-Type: text/html");
		System.out.println();
		System.out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2//EN\">");
		System.out.println("<HTML>");
		System.out.println("<HEAD>");
		System.out.println("	<TITLE>iPatientHealth</TITLE>");
		System.out.println("<META http-equiv=\"content-type\" content=\"text/html; charset=iso-8859-1\">");
		System.out.println("<META http-equiv=\"Pragma\" content=\"no-cache\">");
		System.out.println("<META http-equiv=\"expires\" content=\"0\">");
		System.out.println("<meta http-equiv=\"refresh\" content=\"0; url=http://su8.eduhost.dk/index\" />");
		System.out.println("</HEAD>");
		System.out.println("<BODY>");
		
		System.out.println("<p>Du bliver nu sendt tilbage til loginsiden.<a href=\"http://su8.eduhost.dk/index\">Tryk her hvis du ikke bliver sendt tilbage automatisk</a></p>");
		showTail();
	}

	/**
	 * Prints the end of the HTML page
	 */
	protected static void showTail() {
		System.out.println("</BODY>\n</HTML>");
	}
	
	/**
	 * Is needed for printing the menu, which is the same on all pages
	 */
	protected static void showMenu() {
		System.out.println("<P ALIGN=\"RIGHT\">");
		if(cookie == null) System.out.println("<A HREF=\"index.html\"><INPUT type=\"submit\" value=\"Log ud\"/></A>");
		else System.out.println("<A HREF=\"su8.eduhost.dk/cgi-bin/logout=?cookie="+cookie+"\"><INPUT type=\"submit\" value=\"Log ud\"/></A>");
		System.out.println("</P>");
	}
	
	protected static void showTitle(String title) {
		System.out.println("<H1 ALIGN=\"CENTER\">" + title + "</H1>");
		System.out.println("<HR/>");
	}
	
	/**
	 * This method prints the body part of each page. <br>
	 * This is the part of the pages, which is different.
	 */
	protected static void showBody(){
		throw new UnsupportedOperationException("This operation is not allowed for this class!");
	};
	
	protected static void setCookie(String mCookie){
		cookie = mCookie;
	}
	
	public static void main(String[] args){
		
		//log.info("Showing default page");
		
		showHead();
		showTitle("Default side");
		showMenu();
		showTail();
	}
}