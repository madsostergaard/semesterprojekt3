package serverside;

public class TestClient {
	public static void main(String[] args) {
		Client client = new Client();
		String uuid = "0943d433-3566-4caa-829c-1f19eda428de"; // mener det er Per :-)
		String status = client.sendRequests(uuid);
		System.out.println("[Testprogram] Svar fra server: "+status);
	}
}
