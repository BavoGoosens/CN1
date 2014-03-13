import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;

/**
 * This class implements a simple HTTP webserver which supports both the 1.0 and
 * 1.1 version of the protocol.
 * 
 * @author Sander Geijsen (r0304675) and Bavo Goosens (r0297884).
 * 
 */
public class WebServer implements Runnable {

	/**
	 * This variable holds port on which the server listens for incoming
	 * connections.
	 */
	private ServerSocket listen_port;

	/**
	 * The thread pool which handles all requests.
	 */
	private ExecutorService pool;

	/**
	 * This method constructs the web server and sets the listening port on the
	 * user supplied value.
	 * 
	 * @param port
	 *            An integer represenThisting the port on which the server listens.
	 */
	public WebServer(int port) {
		try {
			this.listen_port = new ServerSocket(port);
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
		Socket connect_port = null;
		while (true) {			
			try {
				connect_port = this.listen_port.accept();
				Request req = new Request(connect_port);
				this.pool.submit(req);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}