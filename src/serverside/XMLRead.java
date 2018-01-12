package serverside;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class XMLHandler extends DefaultHandler {

	private final String ITEMNAME = "ntc";
	private final String[] PARAMS = { "uuid", "stat", "plc", "pass", "seq", "total"};
	private final String[] REQ = { "prm", "cmd", "pass", "id" };
	private final String[] ITEMS = { "date", "time", "titl", "url" };
	private String[] items = new String[ITEMS.length];
	private String[] params = new String[PARAMS.length];
	private String[] req = new String[REQ.length];
	private String out = "";
	private int antalIndkaldelser = 0;
	private int indeks = -1;
	private int paramIndeks = -1;
	private int reqIndeks = -1;

	public int antalIndkaldelser() {
		return antalIndkaldelser;
	}

	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		if (ITEMNAME.equals(localName)) {
			for (int i = 0; i < items.length; i++)
				items[i] = "";
			antalIndkaldelser++;
			indeks = -1;
		} else {
			for (int i = 0; i < items.length; i++) {
				if (ITEMS[i].equals(localName))
					indeks = i;
			}
			for (int k = 0; k < params.length; k++) {
				if (PARAMS[k].equals(localName))
					paramIndeks = k;
			}
			for (int k = 0; k < req.length; k++) {
				if (REQ[k].equals(localName))
					reqIndeks = k;
			}
		}

	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		if (paramIndeks > -1)
			try {
				params[paramIndeks] = new String(ch, start, length);
			} catch (Exception e) {
			}
		if (indeks > -1)
			try {
				items[indeks] = new String(ch, start, length);
			} catch (Exception e) {
			}
		if (reqIndeks > -1)
			try {
				req[reqIndeks] = new String(ch, start, length);
			} catch (Exception e) {
			}
	}

	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {

		if (ITEMNAME.equals(localName)) {
			//out += "Indkaldelser:";
			for (int i = 0; i < items.length; i++)
				out += items[i] + "\t";
			//out += "\n";
		}
		for (int k = 0; k < params.length; k++) {
			if (PARAMS[0].equals(localName));
				//out += params[0] + "\t";
		}
		/*if (XMLNAME.equals(localName)) {
			// out += "Antal indkaldelser i XML besked: " + antalBeskeder();
		}*/
		indeks = -1;
		paramIndeks = -1;
		reqIndeks = -1;
	}
	
	/**
	 * 
	 * @return params: [prm, cmd, pass]
	 */
	public String[] getRequest(){
		return req;
	}

	/**
	 * 
	 * @return params: [uuid, stat, plc, pass, seq, total]
	 */
	public String[] getMsgOutput() {
		return params;
	}
	
	public Notices getNotices(){
		//Notice n = new Notice("","","",0);
		StringTokenizer st = new StringTokenizer(out,"\t");
		String uuid = params[0];
		Notices notices = new Notices(uuid);
		String plc = params[2];
		//System.out.println(out);
		ArrayList<Notice> aList = new ArrayList<>();
		
		for(int k = 0; k<antalIndkaldelser; k++){
			Notice notice = new Notice("","","",0);
			notice.setDate(st.nextToken() + " " + st.nextToken());
			notice.setTitle(st.nextToken());
			notice.setHospID(new Integer(plc));
			notice.setURL(st.nextToken());
			aList.add(notice);
		}
		notices = new Notices(uuid,aList);
		return notices;
	}
}

class MakeParser {

	private DefaultHandler handler;
	private SAXParser saxParser;

	public MakeParser(DefaultHandler handler) {
		this.handler = handler;
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(true);
			saxParser = factory.newSAXParser();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void parse(InputStream uri) {
		try {
			saxParser.parse(uri, handler);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}

public class XMLRead {
	/**
	 * Main class for testing purposes.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		XMLWriter writer = new XMLWriter();
		writer.writeStart("rqst");
		writer.writeTag("cmd", "01", true);
		writer.writeTag("prm", UUID.randomUUID().toString(), true);
		writer.writeTag("pass", "abcd1234", true);
		writer.writeEnd("rqst");

		String in = writer.getXML();

		writer.reset();
		
		InputStream is = new ByteArrayInputStream(in.getBytes());
		XMLHandler handler = new XMLHandler();
		MakeParser parser = new MakeParser(handler);
		
		parser.parse(is);
		String[] output = handler.getRequest();
		for(String s : output){
			System.out.println(s);
		}
		
		writer.writeStart("rpl");
		writer.writeTag("pass", "dcba4321", true);
		writer.writeTag("stat", "00", true);
		writer.writeTag("plc", "03", true);
		writer.writeTag("seq", "01", true);
		writer.writeTag("total", "01", true);
		writer.writeTag("uuid", UUID.randomUUID().toString(), true);
		writer.writeStart("ntc");
		writer.writeTag("date", "081518", true);
		writer.writeTag("time", "12:34", true);
		writer.writeTag("titl", "Undersøg mortens lille pik", true);
		writer.writeTag("url", "su3.eduhost.dk/pisk", true);
		writer.writeEnd("ntc");
		writer.writeEnd("rpl");
		is = new ByteArrayInputStream(writer.getXML().getBytes());
		
		parser.parse(is);
		
		output = handler.getMsgOutput();
		for(String s : output){
			if(!s.isEmpty()) System.out.println(s);
		}
		
		Notices notices = handler.getNotices();
		ArrayList<Notice> list = notices.getNotice();
		for(Notice n : list){
			System.out.println(n.getDate() + " " + n.getTitle() + " " + n.getURL() + " " + n.getHospID());
		}
		

		// SomeHandler handler = new SomeHandler();
		// MakeParser parser = new MakeParser(handler);

		// InputStream is = new ByteArrayInputStream(in.getBytes());
		// byte[] send = new byte[1024];
		// byte[] receive = new byte[1024];
		//
		// send = in.getBytes();
		//
		// DatagramSocket clientSocket = new DatagramSocket();
		// InetAddress IPAddress = InetAddress.getByName("localhost");
		// DatagramPacket sendPacket = new DatagramPacket(send, send.length,
		// IPAddress, 9876);
		//
		// clientSocket.send(sendPacket);
		//
		// DatagramPacket receivePacket = new DatagramPacket(receive,
		// receive.length);
		// clientSocket.receive(receivePacket);
		//
		// String received = new String(receive, 0, receive.length);
		// System.out.println("CLIENT: Received msg.\n\t" + received);
		// int pos = received.lastIndexOf(">");
		// received = received.substring(0, pos + 1);
		//
		// if (received.equals(in))
		// System.out.println("ACK received");
		//
		// System.out.println("CLIENT: Waiting for reply ... ");
		// receive = new byte[1024];
		// receivePacket = new DatagramPacket(receive, receive.length);
		// clientSocket.receive(receivePacket);
		//
		// received = new String(receive, 0, receive.length);
		// System.out.println("CLIENT: Received msg.\n\t" + received);
		//
		// clientSocket.close();
	}

}
