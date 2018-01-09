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
	 * 
	 * @param sessionid
	 *            the id of the session
	 * @return a Notices-object containing all the present notices
	 * @throws SQLException
	 * @throws ParseException
	 */
	public Notices getSessionNotices(String sessionid) throws SQLException, ParseException {
		String uuid = "";
		String sql = "SELECT uuid FROM session WHERE idSession = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, sessionid);
		ResultSet rs = stmt.executeQuery();

		if (rs.next()) {
			uuid = rs.getString(1);
		}

		Notices notices = new Notices(uuid);

		sql = "SELECT datotid, titel, url, sted FROM sessionNotice WHERE session_idsession = ?";
		stmt = conn.prepareStatement(sql);
		stmt.setString(1, sessionid);
		rs = stmt.executeQuery();

		while (rs.next()) {
			String time = rs.getTime(1).toLocalTime().toString();
			String date = rs.getDate(1).toLocalDate().toString();
			String title = rs.getString(2);
			String url = rs.getString(3);
			int sted = rs.getInt(4);
			Notice n = new Notice(title, url, date + " " + time, sted);
			notices.addNotice(n);
		}

		return notices;
	}

	private void insertStatement(String sql) throws SQLException {
		Statement stmt = conn.createStatement();
		stmt.execute(sql);
		stmt.close();
	}

	private void insertStatement(String sql, String[] params) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(sql);
		int parNum = params.length;
		for (int i = 0; i < parNum; i++) {
			stmt.setString(i + 1, params[i]);
		}
		stmt.execute();
	}

	private ResultSet query(String sql, String[] params) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(sql);
		int parNum = params.length;
		for (int i = 0; i < parNum; i++) {
			stmt.setString(i + 1, params[i]);
		}
		ResultSet rs = stmt.executeQuery();
		return rs;
	}

	public void saveSessionNotices(String sessionid, Notices n) throws SQLException {
		ArrayList<Notice> list = n.getNotice();
		for (Notice nt : list) {
			saveNotice(sessionid, nt);
		}
	}

	private void saveNotice(String sessionid, Notice n) throws SQLException {
		String sql = "INSERT INTO hospital.sessionid (session_idsession, datotid, titel, url, sted, sted) VALUES (?,?,?,?,?,?);";
		String[] param = { sessionid, n.getDate(), n.getTitle(), n.getURL(), "" + n.getHospID() };
		insertStatement(sql, param);
	}

	public Notices getNotices(String uuid) throws SQLException, Exception {
		ArrayList<String> output = new ArrayList<>();
		String temp; // string to add to output

		String sql = "SELECT idIndkaldelse, overskrift, tidspunkt FROM hospital.Indkaldelse WHERE Patient_CPR_UUID = ?;";

		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, uuid);

		ResultSet rs = stmt.executeQuery();
		Notices notices = new Notices(uuid);
		while (rs.next()) {
			Notice n = new Notice("", "", "", 0);
			String murl = "";
			temp = ""; // skal være ID&overskrift&tidspunkt
			murl = "su3.eduhost.dk/cgi-bin/SeeNotice?id=" + rs.getInt(1);
			n.setURL(murl);
			temp = /* "&" + */rs.getString(2);
			n.setTitle(temp);
			String date = rs.getDate(3).toLocalDate().toString();
			String time = rs.getTime(3).toLocalTime().toString();
			n.setDate(date + " " + time);
			temp += "&" + date.toString();
			temp += " " + time.toString();
			temp += "&" + murl;
			output.add(temp);
			notices.addNotice(n);
		}

		return notices;
	}
	
	public String getSessionFromUUID(String uuid){
		String sessionid = "";
		String sql = "SELECT idSession FROM hospital.session WHERE uuid = ?";
		String [] par = {uuid};
		try {
			ResultSet rs = query(sql,par);
			if(rs.next()) sessionid = ""+rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sessionid;
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
			Notice ntc = new Notice(rs.getString(1), rs.getString(2), rs.getDate(3).toLocalDate().toString(), 0);

		}
		// return ntc;

		return output;
	}

	public static void main(String[] args) throws Exception {

		DatabaseConnection conn = DatabaseConnection.getInstance();
		passWord = "Per1969"; // input fra web app i stedet.
		cpr = "2808694625"; // input fra web app i stedet. ¨
		// String sql = "INSERT INTO hospital.Indkaldelse
		// (detaljer,overskrift,tidspunkt,Patient_idPatient,Patient_CPR_uuid)
		// VALUES ('Test en indkaldelse med detaljer2', 'Testen er vild2',
		// '2017-10-12 03:04:05', 3, '0943d433-3566-4caa-829c-1f19eda428de');";
		// conn.insertStatement(sql);
		// sql = "SELECT overskrift, detaljer, tidspunkt FROM
		// hospital.Indkaldelse WHERE idIndkaldelse = ?;";
		String uuid = conn.downloadUUID(passWord, cpr);
		// String [] param = { "1" };
		// ResultSet rs = conn.query(sql, param);
		// while(rs.next()){
		// String temp = ""; // skal være ID&overskrift&tidspunkt
		// temp += "" + rs.getString(1);
		// temp += "&" + rs.getString(2);
		// String date = rs.getDate(3).toLocalDate().toString();
		// temp += "&" + date.toString();
		// String time = rs.getTime(3).toLocalTime().toString();
		// temp += " " + time.toString();
		// System.out.println(temp + "\n");
		// }
		System.out.println(uuid);
		boolean valLogin = conn.validate(passWord, cpr, uuid);
		System.out.println(valLogin);

		Notices list = conn.getNotices(uuid);
		// for (Notice s : list) {
		// System.out.println(s);
		// // StringTokenizer st = new StringTokenizer("&");
		// String datetime = "";//s.substring(s.length() - 19);
		// System.out.println(datetime);
		// }

		try {

			Notice ntc = new Notice("En titel", "su3.eduhost.dk", "030208", 0);
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
