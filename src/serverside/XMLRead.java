package serverside;
import java.io.*;
import java.net.*;
import java.util.UUID;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class XMLHandler extends DefaultHandler {
	private final String XMLNAME = "indkaldelser";
	private final String ITEMNAME = "indkaldelse";
	private final String[] PARAMS = { "uuid", "hospitalid", "antalindkaldelser" };
	private final String[] ITEMS = { "datotid", "titel", "url" };
	private String[] items = new String[ITEMS.length];
	private String[] params = new String[PARAMS.length];
	private String out = "";
	private int antalIndkaldelser = 0;
	private int indeks = -1;
	private int paramIndeks = -1;

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
	}

	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {

		if (ITEMNAME.equals(localName)) {
			out += "Indkaldelser:";
			for (int i = 0; i < items.length; i++)
				out += "\t" + items[i];
			out += "\t";
		}
		for (int k = 0; k < params.length; k++) {
			if (PARAMS[k].equals(localName))
				out += params[k] + "\t";
		}
		if (XMLNAME.equals(localName)) {
			//out += "Antal indkaldelser i XML besked: " + antalBeskeder();
		}
		indeks = -1;
		paramIndeks = -1;
	}

	public String getOutput() {
		String output = out;
		out = "";
		return output;
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
		writer.writeStart("request");
		writer.writeTag("uuid", UUID.randomUUID().toString(), true);
		// writer.writeTag("hospitalid", "3", true);
		// writer.writeTag("antalindkaldelser", "2", true);
		// writer.writeStart("indkaldelser");
		// writer.writeStart("indkaldelse");
		// writer.writeTag("datotid", "15/13/2018 15:00", true);
		// writer.writeTag("titel", "MRI exam", true);
		// writer.writeTag("url", "su3.eduhost.dk", true);
		// writer.writeEnd("indkaldelse");
		// writer.writeStart("indkaldelse");
		// writer.writeTag("datotid", "15/13/2018 15:00", true);
		// writer.writeTag("titel", "MRI exam", true);
		// writer.writeEnd("indkaldelse");
		// writer.writeEnd("indkaldelser");
		writer.writeEnd("request");

		String in = writer.getXML();

		writer.reset();

		// SomeHandler handler = new SomeHandler();
		// MakeParser parser = new MakeParser(handler);

		// InputStream is = new ByteArrayInputStream(in.getBytes());
		byte[] send = new byte[1024];
		byte[] receive = new byte[1024];

		send = in.getBytes();

		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("localhost");
		DatagramPacket sendPacket = new DatagramPacket(send, send.length, IPAddress, 9876);

		clientSocket.send(sendPacket);

		DatagramPacket receivePacket = new DatagramPacket(receive, receive.length);
		clientSocket.receive(receivePacket);

		String received = new String(receive, 0, receive.length);
		System.out.println("CLIENT: Received msg.\n\t" + received);
		int pos = received.lastIndexOf(">");
		received = received.substring(0, pos + 1);

		if (received.equals(in))
			System.out.println("ACK received");

		System.out.println("CLIENT: Waiting for reply ... ");
		receive = new byte[1024];
		receivePacket = new DatagramPacket(receive, receive.length);
		clientSocket.receive(receivePacket);

		received = new String(receive, 0, receive.length);
		System.out.println("CLIENT: Received msg.\n\t" + received);

		clientSocket.close();
	}

}
