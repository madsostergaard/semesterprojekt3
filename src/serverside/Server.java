package serverside;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

public class Server {
	private static Config conf = null;
	private static DatagramSocket socket;
	private static byte[] receiveData;
	private static byte[] sendData;
	private static DatagramPacket receivePacket;
	private static DatagramPacket sendPacket;
	private static String msgType;
	private static XMLHandler handler = new XMLHandler();
	private static MakeParser parser;
	private static XMLWriter writer;
	private static DatabaseConnection dtb;
	private static ArrayList<String> ip = new ArrayList<>(); // do we need this? 
	private static ArrayList<String> types = new ArrayList<>();
	private static final String IPADDRESS = "ip";
	private static final String MESSAGE_TYPES = "msgtype";
	private static final String REPLY = "rpl";
	private static final String NOTICE = "ntc";
	private static final String REQUEST = "rqst";
	private static String outPassword = ""; 
	private static String inPassword = ""; 
	private static String returnDomain = ""; 

	/**
	 * Method for validating the received packet
	 * 
	 * @param p
	 * @return
	 */
	private static boolean validatePacket(DatagramPacket p) throws Exception {
		// check ip address in conf
		// is this even possible?!
		boolean isValid = false;
		String ipAddress = p.getAddress().getHostAddress();
		System.out.println(ipAddress);
		if (ip.contains(ipAddress))
			isValid = true;
		else
			return false;

		// check msg type
		msgType = "";
		String message = new String(p.getData(), 0, p.getLength());
		System.out.println(message);
		for (String s : types) {
			if (message.contains(s)) {
				msgType = s;
				isValid = true;
			}
		}
		
		// check password
		InputStream is = new ByteArrayInputStream(message.getBytes());
		parser.parse(is);
		String[] data = handler.getMsgOutput();
		String id = data[2];
		String info = conf.confValue(id);
		String[] splitted = info.split(",");
		outPassword = splitted[0];
		inPassword = splitted[1];
		returnDomain = splitted[2]; // Is this needed for the response? 
		if(inPassword.equals(data[3])) isValid = true;
		else isValid = false; 
		
		return isValid;
	}
	
	/**
	 * Sends a reply to the client that sent a request
	 * @param notices
	 * @param ip
	 * @param port
	 * @param uuid
	 */
	private static void sendReply(Notices notices, InetAddress ip, int port, String uuid){
		ArrayList<Notice> list = notices.getNotice();
		
		int size = 0; 
		if(list != null) size = list.size();
		int status = 00;
		try{
		// only send one packet
		if (size > 0 && size <= 5) {
			status = 21;

			writer.reset();
			writer.writeStart(REPLY);
			writer.writeTag("uuid", uuid, true);
			writer.writeTag("plc", "03", true);
			writer.writeTag("status", "" + status, true);
			writer.writeTag("pass", outPassword, true);
			writer.writeTag("seq", "01", true);
			writer.writeTag("total", "01", true);
			for (Notice n : list) {
				writer.writeStart(NOTICE);
				ArrayList<String> dt = DateUtil.toXML(n.getDate());
				writer.writeTag("date", dt.get(0), true);
				writer.writeTag("time", dt.get(1), true);
				writer.writeTag("titl", n.getTitle(), true);
				writer.writeTag("url", n.getURL(), true);
				writer.writeEnd(NOTICE);
			}

			writer.writeEnd(REPLY);

			sendData = new byte[1024];
			sendData = writer.getXML().getBytes();

			sendPacket = new DatagramPacket(sendData, sendData.length, ip, port);
			socket.send(sendPacket);
		}

		// send more packets
		else if (size > 5) {
			status = 20;
			int msgCount = (int) Math.ceil(size / 5);
			int k = 0;

			for (int i = 1; i <= msgCount; i++) {
				if (i == msgCount)
					status = 21;
				writer.reset();
				writer.writeStart(REPLY);
				writer.writeTag("uuid", uuid, true);
				writer.writeTag("plc", "03", true);
				writer.writeTag("status", "" + status, true);
				writer.writeTag("pass", outPassword, true);
				writer.writeTag("seq", ""+i, true);
				writer.writeTag("total", ""+msgCount, true);
				while ((k + 1) % 5 != 0 && (k + 1) < size) {
					writer.writeStart(NOTICE);
					ArrayList<String> dt = DateUtil.toXML(list.get(k).getDate());
					writer.writeTag("date", dt.get(0), true);
					writer.writeTag("time", dt.get(1), true);
					writer.writeTag("titl", list.get(k).getTitle(), true);
					writer.writeTag("url", list.get(k).getURL(), true);
					writer.writeEnd(NOTICE);

					k++;
				}
				writer.writeEnd(REPLY);

				sendData = new byte[1024];
				sendData = writer.getXML().getBytes();

				sendPacket = new DatagramPacket(sendData, sendData.length, ip, port);
				socket.send(sendPacket);
			}
		}

		// no notices
		else if (size == 0) {
			status = 01;

			writer.reset();
			writer.writeStart(REPLY);
			writer.writeTag("uuid", uuid, true);
			writer.writeTag("plc", "03", true);
			writer.writeTag("status", "" + status, true);
			writer.writeTag("pass", outPassword, true);
			writer.writeTag("seq", "00", true);
			writer.writeTag("total", "00", true);
			writer.writeEnd(REPLY);

			sendData = new byte[1024];
			sendData = writer.getXML().getBytes();

			sendPacket = new DatagramPacket(sendData, sendData.length, ip, port);
			socket.send(sendPacket);
		}
		} catch(IOException e){
			
		}
	}

	
	public static void main(String[] args) throws Exception {
		// initialization
		conf = new Config("src/konfiguration.conf");
		socket = new DatagramSocket(9876);
		receiveData = new byte[1024];
		sendData = new byte[1024];
		msgType = "";
		dtb = DatabaseConnection.getInstance();

		handler = new XMLHandler();
		parser = new MakeParser(handler);
		writer = new XMLWriter();

		// is this needed? 
		String ips = conf.confValue(IPADDRESS);
		StringTokenizer st = new StringTokenizer(ips, ",");
		while (st.hasMoreTokens()) {
			ip.add(st.nextToken());
		}

		
		String typesFromConfig = conf.confValue(MESSAGE_TYPES);
		st = new StringTokenizer(typesFromConfig, ",");
		while (st.hasMoreTokens()) {
			types.add(st.nextToken());
		}

		// var to handle an exit of the programme.
		boolean running = true;

		// loop to handle requests and stuff
		while (running) {
			receivePacket = new DatagramPacket(receiveData, receiveData.length);
			socket.receive(receivePacket);

			boolean packetIsOk = validatePacket(receivePacket);
			if (packetIsOk) {
				// handle the different msg types
				System.out.println("the packet is OK");
				sendData = new byte[1024];

				switch (msgType) {
				case REQUEST:
					// get senders ip and port number
					InetAddress IPAddress = receivePacket.getAddress();
					int port = receivePacket.getPort();

					// remove unreadable chars
					String received = new String(receivePacket.getData(), 0, receivePacket.getLength());

					// maybe redundant
					int pos = received.lastIndexOf(">");
					received = received.substring(0, pos + 1);

					// convert to input stream and parse
					InputStream is = new ByteArrayInputStream(received.getBytes());
					parser.parse(is);
					String[] data = handler.getMsgOutput();
					String uuid = data[0];

					// write response
					Notices notices = dtb.getNotices(uuid);
					sendReply(notices, IPAddress, port, uuid);
					
					break;

				default:
					break;
				}
			} else
				continue; 
		}

		socket.close();
	}

}
