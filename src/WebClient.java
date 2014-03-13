import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.corba.se.impl.protocol.RequestCanceledException;

public class WebClient {

	public static void main(String[] args) {
		new WebClient();
	}

	public WebClient() {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the request");
		String request = scan.nextLine();
		String command = parseCommand(request);
		String p = parsePort(request);
		String HTTP_version = parseHTTP(request);
		String hostname = parseDNS(request);
		String path_to_file = parsePath(request);
		path_to_file = (String) path_to_file.subSequence(hostname.length(), path_to_file.length());
		int port = Integer.parseInt(p);
		try {
			Socket socket = openSocket(hostname, port);
			String result = writeToAndReadFromSocket(socket, command + " " + path_to_file + " " 
					+ HTTP_version + "\r\n" + "Host: " + hostname);
			System.out.println(result);
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		scan.close();
	}

	private String parseDNS(String URI) {
		Pattern pattern = Pattern.compile("(www.[a-z]*.\\w*)");
		Matcher matcher = pattern.matcher(URI);
		if (matcher.find())
			return matcher.group();
		return null;
	}

	private String parsePath(String URI) {
		Pattern pattern = Pattern.compile("www.[a-z]*.[a-z]*((/|[a-z]*)*.[a-z]*)");
		Matcher matcher = pattern.matcher(URI);
		if (matcher.find())
			return matcher.group();
		return null;
	}

	private String parseHTTP(String request) {
		Pattern pattern = Pattern.compile("(HTTP/\\d.\\d)");
		Matcher matcher = pattern.matcher(request);
		if (matcher.find())
			return matcher.group();
		return null;
	}

	private String parsePort(String request) {
		Pattern pattern = Pattern.compile("(\\d+)");
		Matcher matcher = pattern.matcher(request);
		if (matcher.find())
			return matcher.group();
		return null;
	}

	private String parseCommand(String request) {
		Pattern pattern = Pattern.compile("(GET|PUT|HEAD|POST)");
		Matcher matcher = pattern.matcher(request);
		if (matcher.find())
			return matcher.group();
		return null;
	}

	private String writeToAndReadFromSocket(Socket socket, String request)
			throws Exception {
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream()));
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			bufferedWriter.write(request);
			bufferedWriter.write("\r\n");
			bufferedWriter.flush();
			StringBuilder sb = new StringBuilder();
			String str;
			while (bufferedReader.ready()) {
				System.out.println(bufferedReader.readLine());
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
	 * @throws IOException 
	 */
	private Socket openSocket(String server, int port) throws IOException {
		Socket socket;
		InetAddress inteAddress = InetAddress.getByName(server);
		SocketAddress socketAddress = new InetSocketAddress(inteAddress, port);
		socket = new Socket();
		socket.connect(socketAddress);
		return socket;
	}

}