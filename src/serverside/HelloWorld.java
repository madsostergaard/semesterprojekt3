package serverside;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

public class HelloWorld {
	
	public static void main(String[] args) throws Exception{
		
		System.out.println("Test af toXML:");
		String date1 = "2018-09-12 15:20:45";
		System.out.println(date1);
		
		ArrayList<String> xml = DateUtil.toXML(date1);
		System.out.println(" ");
		System.out.println(xml.get(0) + " : " + xml.get(1));
		System.out.println(" ");
		
		System.out.println("Test af toDatabase:");
		// 				mmddyy HH:MM
		String date2 = "082618 22:30";
		System.out.println(date2);
		
		LocalDate date = DateUtil.toDatabase(date2);
		System.out.println(date);
		
		//LocalDate date = DateUtil.toDatabase(date1);
		//System.out.println(date);
		
	}

}
