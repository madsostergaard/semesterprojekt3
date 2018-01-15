package cgi;

import java.io.*;
import java.util.*;

public class Convert_Bo {

	private static BufferedReader in = null;
	private static String filename = null;
	private static PrintWriter out = null;

	public static void main(String[] args) {
		if (args.length > 0)
			filename = args[0];
		else
			System.exit(0);

		try {
			in = new BufferedReader(new FileReader(filename));
			out = new PrintWriter(new FileWriter(filename + ".out"));
			String l = in.readLine();
			while (l != null) {
				out.print("System.out.println(\"");
				for (int i = 0; i < l.length(); i++) {
					char c = l.charAt(i);
					if (c == '"')
						out.print("\\");
					out.print(c);
				}
				out.println("\");");
				l = in.readLine();
			}
			out.close();
			in.close();
		} catch (Exception e) {
		}
	}
}
