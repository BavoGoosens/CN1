import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Scanner;

public class WebClient {

	public static void main(String[] args) {
		new WebClient();
	}

	public WebClient() {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the request");
		String ServerName = scan.next();
		int port = 80;
		try {
			Socket socket = openSocket(ServerName, port);
			String result = writeToAndReadFromSocket(socket, "GET /\n\n");
			System.out.println(result);
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		scan.close();
	}

	private String writeToAndReadFromSocket(Socket socket, String writeTo)
			throws Exception {
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream()));
			bufferedWriter.write(writeTo);
			bufferedWriter.flush();

			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String str;
			while ((str = bufferedReader.readLine()) != null) {
				sb.append(str + "\n");
			}
			bufferedReader.close();
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Open a socket connection to the given server on the given port.
	 */
	private Socket openSocket(String server, int port) {
		Socket socket;
		InetAddress inteAddress = InetAddress.getByName(server);
		SocketAddress socketAddress = new InetSocketAddress(inteAddress, port);
		socket = new Socket();
		socket.connect(socketAddress);
		return socket;

	}

}