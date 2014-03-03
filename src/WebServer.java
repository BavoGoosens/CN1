import java.net.*;
import java.io.*;

/**
 * This class implements a simple HTTP webserver which supports both the 1.0 and 1.1 version of the protocol.
 * 
 * @author Sander Geijsen and Bavo Goosens.
 *
 */
public class WebServer implements Runnable{

	/**
	 * This variable holds port on which the server listens for incoming connections.
	 */
	private ServerSocket port;
	
	/**
	 * This method constructs the webserver and sets the listening port on the user supplied value.
	 * 
	 * @param port
	 * 		  An integer representing the port on which the server listens. 
	 */
	public WebServer(int port){
		try {
			this.setPort(new ServerSocket(port));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method starts the server.
	 */
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

	/**
	 * 
	 * @return
	 */
	public ServerSocket getPort() {
		return port;
	}

	/**
	 * 
	 * @param port
	 */
	private void setPort(ServerSocket port) {
		this.port = port;
	}

}
