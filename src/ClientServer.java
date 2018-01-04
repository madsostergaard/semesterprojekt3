import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ClientServer {
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
	private static ArrayList<String> ip = new ArrayList<>();
	private static ArrayList<String> types = new ArrayList<>();
	private static final String IPADDRESS = "ip";
	private static final String MESSAGE_TYPES = "msgtype";
	private static final String REPLY = "reply";
	private static final String NOTICE = "indkaldelse";
	private static final String NOTICES = "indkaldelser";
	private static final String REQUEST = "request";

	/**
	 * Method for validating the received packet
	 * 
	 * @param p
	 * @return
	 */
	private static boolean validatePacket(DatagramPacket p) throws Exception {
		// check ip address in conf
		boolean isValid = false;
		String ipAddress = p.getAddress().getHostAddress();
		System.out.println(ipAddress);
		if (ip.contains(ipAddress))
			isValid = true;
		else
			return false;

		// TODO: check ID and pass

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
		return isValid;
	}

	public static void main(String[] args) throws Exception {
		// initialization
		conf = new Config("konfiguration.conf");
		socket = new DatagramSocket(9876);
		receiveData = new byte[1024];
		sendData = new byte[1024];
		msgType = "";

		handler = new XMLHandler();
		parser = new MakeParser(handler);
		writer = new XMLWriter();

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

		// should also read ids and ports and stuff. TODO: later

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
				case "request":
					sendData = receiveData;
					InetAddress IPAddress = receivePacket.getAddress();
					int port = receivePacket.getPort();
					sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
					socket.send(sendPacket);

					String received = new String(receiveData, 0, receiveData.length);
					int pos = received.lastIndexOf(">");
					received = received.substring(0, pos + 1);
					InputStream is = new ByteArrayInputStream(received.getBytes());
					parser.parse(is);
					String data = handler.getOutput();
					st = new StringTokenizer(data, "\t");
					String uuid = st.nextToken();
					writer.reset();
					writer.writeStart(REPLY);
					writer.writeTag("uuid", uuid, true);
					writer.writeTag("hospitalid", "3", true);
					writer.writeTag("antalindkaldelser", "0", true);
					// TODO: find person in database: ArrayList<String> notices
					// =
					// dtb.getNotices(uuid);
					// int count = notices.size();
					// for (String s : notices){
					// lav noget smart
					writer.writeEnd(REPLY);

					sendData = new byte[1024];
					sendData = writer.getXML().getBytes();

					sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
					socket.send(sendPacket);

					break;

				case "begin":
					sendData = receiveData;
					IPAddress = receivePacket.getAddress();
					port = receivePacket.getPort();
					sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
					socket.send(sendPacket);

					received = new String(receiveData, 0, receiveData.length);
					pos = received.lastIndexOf(">");
					received = received.substring(0, pos + 1);
					is = new ByteArrayInputStream(received.getBytes());
					parser.parse(is);
					data = handler.getOutput();
					st = new StringTokenizer(data, "\t");
					uuid = st.nextToken();

					writer.reset();
					writer.writeStart(REQUEST);
					writer.writeTag("uuid", uuid, true);
					writer.writeTag(NOTICE, "", true);
					writer.writeEnd(REQUEST);

					sendData = new byte[1024];
					sendData = writer.getXML().getBytes();

					for (String s : ip) {
						InetAddress temp = InetAddress.getByName(s);
						if (temp.toString().equals("/127.0.0.1")) { 
							// TODO:
							// skal
																	// være !
							sendPacket = new DatagramPacket(sendData, sendData.length, temp, port);
							socket.send(sendPacket);
						}
					}

					// TODO: check local database and put results into temp
					// database

					break;
				case "reply":
					received = new String(receiveData, 0, receiveData.length);
					pos = received.lastIndexOf(">");
					received = received.substring(0, pos + 1);
					is = new ByteArrayInputStream(received.getBytes());
					parser.parse(is);
					data = handler.getOutput();
					System.out.println(data);

					st = new StringTokenizer(data, "\t");
					uuid = st.nextToken();
					System.out.println(uuid);
					String hospitalID = st.nextToken();
					System.out.println(hospitalID);
					int count = new Integer(st.nextToken());
					System.out.println(count);
					String[][] notices = new String[count][3];
					for (int k = 0; k < notices.length; k++) {
						for (int j = 0; j < notices[k].length; j++) {
							notices[k][j] = "";
						}
					}
					for (int i = 0; i < count; i++) {
						if (st.hasMoreTokens())
							st.nextToken();
						if (st.hasMoreTokens())
							notices[i][0] = st.nextToken();
						if (st.hasMoreTokens())
							notices[i][1] = st.nextToken();
						if (st.hasMoreTokens())
							notices[i][2] = st.nextToken();
					}
					for (int k = 0; k < notices.length; k++) {
						for (int j = 0; j < notices[k].length; j++) {
							System.out.println(notices[k][j]);
						}
					}

					// TODO: save string array to session table

					break;

				case "quit":
					sendData = receiveData;
					IPAddress = receivePacket.getAddress();
					port = receivePacket.getPort();
					sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
					socket.send(sendPacket);

					running = false;

					break;

				default:
					break;
				}
			} else
				continue; // return to waiting position
		}

		socket.close();
	}

}
