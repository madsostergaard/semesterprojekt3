package serverside;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

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
	private static DatabaseConnection dtb;
	private static ArrayList<String> ip = new ArrayList<>();
	private static ArrayList<String> types = new ArrayList<>();
	private static final String IPADDRESS = "ip";
	private static final String MESSAGE_TYPES = "msgtype";
	private static final String REPLY = "rpl";
	private static final String NOTICE = "ntc";
	private static final String REQUEST = "rqst";

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
		dtb = DatabaseConnection.getInstance();

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
					String data = handler.getOutput();
					st = new StringTokenizer(data, "\t");
					String uuid = st.nextToken();

					// write response
					int status = 21;
					Notices notices = dtb.getNotices(uuid);
					ArrayList<Notice> list = notices.getNotice();
					int size = list.size();

					// only send one packet
					if (size > 0 && size <= 5) {
						status = 21;

						writer.reset();
						writer.writeStart(REPLY);
						writer.writeTag("uuid", uuid, true);
						writer.writeTag("plc", "03", true);
						writer.writeTag("status", "" + status, true);
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

						sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
						socket.send(sendPacket);
						break;
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

							sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
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
						writer.writeEnd(REPLY);

						sendData = new byte[1024];
						sendData = writer.getXML().getBytes();

						sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
						socket.send(sendPacket);
					}
					break;

				case "begin":
					// send OK:
					IPAddress = receivePacket.getAddress();
					port = receivePacket.getPort();

					writer.reset();
					writer.writeStart(REPLY);
					writer.writeTag("stat", "00", true);
					writer.writeEnd(REPLY);

					sendData = new byte[1024];
					sendData = writer.getXML().getBytes();
					sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
					socket.send(sendPacket);

					// read msg
					received = new String(receiveData, 0, receiveData.length);
					pos = received.lastIndexOf(">");
					received = received.substring(0, pos + 1);
					is = new ByteArrayInputStream(received.getBytes());
					parser.parse(is);
					data = handler.getOutput();
					st = new StringTokenizer(data, "\t");
					uuid = st.nextToken();

					// make threads for each hospital
					for (String s : ip) {
						new Thread() {
							public void sendPacket() {
								writer.reset();
								writer.writeStart(REQUEST);
								writer.writeTag("cmd", "01", true);
								writer.writeTag("prm", uuid, true);
								writer.writeEnd(REQUEST);

								try {
									InetAddress sendAddress = InetAddress.getByName(s);
									int port = 9876; // maybe something more specific

									sendData = new byte[1024];
									sendData = writer.getXML().getBytes();
									sendPacket = new DatagramPacket(sendData, sendData.length, sendAddress, port);

									socket.send(sendPacket);
								} catch (UnknownHostException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}

							public void run() {
								sendPacket();
								Timer timer = new Timer();
								timer.schedule(new TimerTask() {
									public void run() {
										sendPacket();
									}
								}, 5000);
								try {
									receiveData = new byte[1024];
									receivePacket = new DatagramPacket(receiveData, receiveData.length);
									socket.receive(receivePacket);
									timer.cancel();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

						}.start();
					}

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

				default:
					break;
				}
			} else
				continue; // return to waiting position
		}

		socket.close();
	}

}
