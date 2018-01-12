package serverside;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Client {

	private Config conf;
	private DatagramSocket socket;
	private byte[] receiveData;
	private byte[] sendData;
	private DatagramPacket receivePacket;
	private DatagramPacket sendPacket;
	private ArrayList<String> ips = new ArrayList<>();
	private DatabaseConnection dtb;
	private XMLHandler handler = new XMLHandler();
	private MakeParser parser;
	private XMLWriter writer;
	private final String REQUEST = "rqst";
	private final int PORT = 1234;
	private long timeoutGlobal = 3000; // check this value
	private long timeout = 1000;
	private static String outPassword = "";
	private static String inPassword = "";
	private static String returnDomain = "";
	private static String sendPort = "";
	private static final String THIS_IP = "198.168.239.23";
	private static final String HOSPITAL_ID = "0009";

	public Client(/* portnumber?? */) {
		try {
			socket = new DatagramSocket(PORT);
			socket.setSoTimeout((int) timeout);
			// System.out.println("Socket init: OK");

			conf = new Config("src/konfiguration.conf");
			// System.out.println("Conf init: OK");
			dtb = DatabaseConnection.getInstance();
			// System.out.println("DTB conn init: OK");

			String ip = conf.confValue("ip");
			// System.out.println(ip);
			StringTokenizer st = new StringTokenizer(ip, ",");
			while (st.hasMoreTokens()) {
				String s = st.nextToken();
				// System.out.println(s);
				ips.add(s);
			}

			handler = new XMLHandler();
			parser = new MakeParser(handler);
			writer = new XMLWriter();

			receiveData = new byte[1024];
			sendData = new byte[1024];

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendPacket(String ip, String uuid, String pass) {
		writer.reset();
		writer.writeStart(REQUEST);
		writer.writeTag("cmd", "01", true);
		writer.writeTag("prm", uuid, true);
		writer.writeTag("pass", pass, true);
		writer.writeTag("id", HOSPITAL_ID, true);
		writer.writeEnd(REQUEST);

		try {
			InetAddress sendAddress = InetAddress.getByName(ip);
			int port = new Integer(sendPort); // maybe something more specific

			sendData = new byte[1024];
			sendData = writer.getXML().getBytes();
			// System.out.println(writer.getXML());
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

	private void setParams(String id) {
		// System.out.println(id);
		String info = conf.confValue(id);
		// System.out.println(info);
		String[] splitted = info.split(",");
		outPassword = splitted[0];
		inPassword = splitted[1];
		returnDomain = splitted[2];
		sendPort = splitted[3];
	}

	public String sendRequests(String uuid) {
		// saveLocalNotices(uuid);
		String sessionid = dtb.getSessionFromUUID(uuid);

		int size = ips.size();
		int[][] state = new int[size][2];
		for (int j = 0; j < state.length; j++) {
			for (int i = 0; i < 2; i++) {
				state[j][i] = 0;
				//System.out.print("[" + state[j][i] + "]");
			}
			//System.out.println();
		}

		String status = "";

		for (String ip : ips) {
			if (ip.equals(THIS_IP)) {
				continue;
			}

			String id = conf.confValue(ip);

			setParams(id);

			sendPacket(ip, uuid, outPassword);
		}

		long t1 = System.currentTimeMillis();
		long t2 = 0;

		while ((t2 = System.currentTimeMillis() - t1) < timeoutGlobal) {
			try {
				receivePacket = new DatagramPacket(receiveData, receiveData.length);
				socket.receive(receivePacket);

				String received = new String(receivePacket.getData(), 0, receivePacket.getLength());
				InputStream is = new ByteArrayInputStream(received.getBytes());
				parser.parse(is);
				String[] data = handler.getMsgOutput();
				int total = new Integer(data[5]);
				String place = data[2];
				String pass = data[3];

				//System.out.println("[CLIENT] Received: " + received);

				setParams(place);

				if (!pass.equals(inPassword))
					break;
				// TODO: check for password

				state[new Integer(place) - 1][0] += 1;
				state[new Integer(place) - 1][1] = total;

				// get the notices
				Notices notices = handler.getNotices();

				// save to session table
				dtb.saveSessionNotices(sessionid, notices);

				boolean ok = true;
				for (int k = 0; k < state.length; k++) {
					//System.out.println("[" + state[k][0] + "][" + state[k][1] + "]");
					boolean isOK = (state[k][0] == state[k][1] && !(state[k][1] == 0));
					if (!isOK && k != 2)
						ok = false;
					if (isOK && k != 2)
						status += "" + (k + 1) + ":OK,";
				}
				if (ok)
					break;
			} catch (SocketTimeoutException e2) {
				// protocol timeout:
				for (int k = 0; k < state.length; k++) {
					boolean isOK = (state[k][0] == state[k][1] && !(state[k][1] == 0));

					if (!isOK && k != 2) {
						int place = k + 1;
						setParams("000" + place);
						sendPacket(returnDomain, uuid, outPassword);
					}
				}
			} catch (IOException e) {
				// System.out.println("fejl i modtagelse");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (status.isEmpty())
			status = "error!";

		return status;
	}

}
