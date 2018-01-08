package serverside;
import java.sql.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;

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
			LocalDate date = rs.getDate(3).toLocalDate();
			temp += "&" + date.toString();
			//log.debug("Found notice, adding to output {}", temp);
			output.add(temp);
		}

		return output;
	}
	
	// ----------------------------------
	 /*
	public ArrayList<Notice> getNotices(String uuid) throws SQLException {
		ArrayList<Notice> output = new ArrayList<>();
		String temp; // string to add to output

		String sql = "SELECT idIndkaldelse, overskrift, tidspunkt FROM hospital.Indkaldelse WHERE Patient_CPR_UUID = ?;";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, uuid);

		ResultSet rs = stmt.executeQuery();
		
		while (rs.next()) {
			temp = ""; // skal være ID&overskrift&tidspunkt
			temp += "" + rs.getInt(1);
			temp += "&" + rs.getString(2);
			LocalDate date = rs.getDate(3).toLocalDate();
			temp += "&" + date.toString();
			//log.debug("Found notice, adding to output {}", temp);
			output.add(temp);
		}

		return output;
	}
	*/
	
	// ----------------------------------
	
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
				Notice ntc = new Notice(rs.getString(1), rs.getString(2), rs.getDate(3).toLocalDate().toString());
			
		}
		// return ntc;
		
		return output;
	}
	

	/*
	 * public static void main(String[] args) throws SQLException,
	 * ClassNotFoundException {
	 * 
	 * Connection conn = getConnection(); passWord =
	 * "blOoperairshiPairshiP5$anatomY"; //input fra web app i stedet. cpr =
	 * "2147483647"; //input fra web app i stedet. String uuid =
	 * downloadUUID(conn, passWord, cpr); boolean valLogin = validate(conn,
	 * passWord, cpr, uuid); System.out.println(valLogin); }
	 */

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
