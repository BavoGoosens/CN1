import java.net.*;
import java.io.*;


public class WebServer implements Runnable{

	private ServerSocket port;
	
	public WebServer(int port){
		try {
			this.setPort(new ServerSocket(port));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			Socket connect = this.getPort().accept();
			BufferedReader clientin = new BufferedReader(
					new InputStreamReader(connect.getInputStream()));
			DataOutputStream clientout = new DataOutputStream(
					connect.getOutputStream());
			clientout.writeBytes(procesRequest(clientin.readLine()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private String procesRequest(String readLine) {
		// TODO Auto-generated method stub
		return null;
	}

	public ServerSocket getPort() {
		return port;
	}

	private void setPort(ServerSocket port) {
		this.port = port;
	}

}
