import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;

/**
 * This class implements a simple HTTP webserver which supports both the 1.0 and 1.1 version of the protocol.
 * 
 * @author Sander Geijsen (r0304675) and Bavo Goosens (r0297884).
 *
 */
public class WebServer implements Runnable{

	/**
	 * This variable holds port on which the server listens for incoming connections.
	 */
	private ServerSocket listen_port;
	
	private Socket connection_port;
	
	private ExecutorService pool;
	
	/**
	 * This method constructs the webserver and sets the listening port on the user supplied value.
	 * 
	 * @param port
	 * 		  An integer representing the port on which the server listens. 
	 */
	public WebServer(int port){
		try {
			this.setPort(new ServerSocket(port));
			this.pool = Executors.newFixedThreadPool(40);
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
			this.connection_port = this.getPort().accept();
			BufferedReader clientin = new BufferedReader(
					new InputStreamReader(connection_port.getInputStream()));
			DataOutputStream clientout = new DataOutputStream(
					connection_port.getOutputStream());
			clientout.writeBytes(handleRequest(clientin, clientout));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private String handleRequest(BufferedReader clientin,
			DataOutputStream clientout) {
		String req = clientin.readLine();
		if (isValidRequest(req)){
			
		}
	}

	private boolean isValidRequest(String req) {
		return req.matches("(GET|PUT|HEAD|POST) .* HTTP/\\d.\\d");
	}

	/**
	 * 
	 * @return
	 */
	public ServerSocket getPort() {
		return listen_port;
	}

	/**
	 * 
	 * @param port
	 */
	private void setPort(ServerSocket port) {
		this.listen_port = port;
	}

}
