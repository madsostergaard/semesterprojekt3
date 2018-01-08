package serverside;

import java.sql.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.StringTokenizer;

//import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;

public class DatabaseConnection {

	private static String passWord = "";
	private static String cpr = "";
	private static DatabaseConnection instance;
	private static Connection conn;
	private static final String url = "jdbc:mysql://su3.eduhost.dk/";
	private static final String user = "root";
	private static final String pass = "healerrearpattern";

	private DatabaseConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, user, pass);
		} catch (Exception e) {
			System.exit(0);
		}
	}

	public static DatabaseConnection getInstance() {
		if (instance == null)
			instance = new DatabaseConnection();
		return instance;
	}

	public String downloadUUID(String passWord, String cpr) throws SQLException {
		String uuid = "";
		String downString = "SELECT uuid FROM hospital.cprRegister WHERE cpr =? AND nemIDPassword =?";
		PreparedStatement statement = conn.prepareStatement(downString);
		statement.setString(1, cpr);
		statement.setString(2, passWord);
		ResultSet rs = statement.executeQuery();
		if (rs.next()) {
			uuid = rs.getString("uuid");
		}
		return uuid;
	}

	public boolean validate(String mPassWord, String mCpr, String uuid) throws SQLException {
		boolean bol = false;
		String downString = "SELECT cpr, nemIDPassword FROM hospital.cprRegister WHERE uuid = ?";
		PreparedStatement statement = conn.prepareStatement(downString);

		statement.setString(1, uuid);
		ResultSet rs = statement.executeQuery();
		if (rs.next()) {
			cpr = rs.getString("cpr");
			passWord = rs.getString("nemIDPassword");
		}
		if (cpr.equals(mCpr) && passWord.equals(mPassWord) && uuid.length() == 36) {
			bol = true;
		} else {
			System.out.println("user was not valid");
		}

		return bol;
	}
	
	/**
	 * Builds a Notices-object from the notices in the database
	 * @param sessionid the id of the session
	 * @return a Notices-object containing all the present notices 
	 */
	public Notices getSessionNotices(String sessionid){
		String uuid = "";
		String sql = "SELECT uuid FROM session WHERE idSession = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, sessionid);
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			uuid = rs.getString(1);
		}
		
		Notices notices = new Notices(uuid);
		
		sql = "SELECT datotid, titel, url, sted FROM sessionNotice WHERE session_idsession = ?";
		stmt = conn.prepareStatement(sql);
		stmt.setString(1, sessionid);
		rs = stmt.executeQuery();
		
		while(rs.next()){
			String time = rs.getTime(1).toLocalTime().toString();
			String date = rs.getDate(1).toLocalDate().toString();
			String title = rs.getString(2);
			String url = rs.getString(3);
			int sted = rs.getInt(4);
			Notice n = new Notice(title, url, date+" "+time, sted);
			notices.addNotice(n);
		}
		
		return notices;
	}
	
	
	public void saveSessionNotices(String sessionid, Notices n){		
		ArrayList<Notice> list = n.getNotice();
		for(Notice nt : list){
			saveNotice(sessionid, nt);
		}
	}
	
	
	public void saveNotice(String sessionid, Notice n){
		String sql = "";
		
	}

	public ArrayList<String> getNotices(String uuid) throws SQLException {
		ArrayList<String> output = new ArrayList<>();
		String temp; // string to add to output

		String sql = "SELECT idIndkaldelse, overskrift, tidspunkt FROM hospital.Indkaldelse WHERE Patient_CPR_UUID = ?;";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, uuid);

		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			temp = ""; // skal være ID&overskrift&tidspunkt
			temp += "" + rs.getInt(1);
			temp += "&" + rs.getString(2);
			String date = rs.getDate(3).toLocalDate().toString();
			temp += "&" + date.toString();
			String time = rs.getTime(3).toLocalTime().toString();
			temp += " " + time.toString();
			// log.debug("Found notice, adding to output {}", temp);
			output.add(temp);
		}

		return output;
	}

	public String getNoticeDetails(int id) throws SQLException, ParseException {
		String output = "";
		String sql = "SELECT overskrift, detaljer, tidspunkt FROM hospital.Indkaldelse WHERE idIndkaldelse = ?;";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, id);

		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			output += rs.getString(1);
			output += "&" + rs.getString(2);
			output += "&" + rs.getDate(3).toLocalDate().toString();
		}

		// den nye getNoticeDetails?
		if (rs.next()) {
			Notice ntc = new Notice(rs.getString(1), rs.getString(2), rs.getDate(3).toLocalDate().toString(),0);

		}
		// return ntc;

		return output;
	}

	public static void main(String[] args) throws SQLException, ClassNotFoundException {

		DatabaseConnection conn = DatabaseConnection.getInstance();
		passWord = "Per1969"; // input fra web app i stedet.
		cpr = "2808694625"; // input fra web app i stedet. ¨
		String uuid = conn.downloadUUID(passWord, cpr);
		System.out.println(uuid);
		boolean valLogin = conn.validate(passWord, cpr, uuid);
		System.out.println(valLogin);
		
		ArrayList<String> list = conn.getNotices(uuid);
		for(String s : list){
			System.out.println(s);
			//StringTokenizer st = new StringTokenizer("&");
			String datetime = s.substring(s.length()-19);
			System.out.println(datetime);
		}
		
		try {
			
			Notice ntc = new Notice("En titel", "su3.eduhost.dk", "030208",0);
			System.out.println(ntc.getDate());
			
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * for testing connection
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	static void printerTest() throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery("SELECT * FROM hospital.cprRegister;");

		while (rset.next()) {
			System.out.println(rset.getString(1) + " " + rset.getString(2) + " " + rset.getString(3) + " "
					+ rset.getInt(4) + " " + rset.getInt(5) + " " + rset.getString(6) + " " + rset.getInt(7) + " "
					+ rset.getString(8) + " " + rset.getInt(9) + " " + rset.getInt(10) + " " + rset.getInt(11) + " "
					+ rset.getString(12) + " " + rset.getString(13) + " " + rset.getString(14));
		}
	}
}
