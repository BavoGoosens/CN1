public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WebServer wb = new WebServer(8001);
		Thread server = new Thread(wb);
		server.start();
		WebClient wc = new WebClient();
	}
}
