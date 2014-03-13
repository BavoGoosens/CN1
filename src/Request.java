import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a single request.
 * 
 * @author Sander Geijsen (r0304675) and Bavo Goosens (r0297884).
 * 
 */
public class Request implements Runnable {

	/**
	 * The HTTP version of this request.
	 */
	private String version;

	/**
	 * The type of command this request handles. HEAD | GET | PUT | POST.
	 */
	private String command;

	/**
	 * The connection port which this request uses. Can be closed after a
	 * request is handled using HTTP/1.0
	 */
	private Socket connection;

	/**
	 * The out stream via which the result of the request is returned.
	 */
	private OutputStream outstream;

	/**
	 * The Path to the file that needs to be retrieved/altered/made/ ..... .
	 */
	private String path_to_file;

	/**
	 * The Host. 
	 * This is obliged for HTTP 1.1
	 */
	private String host;

	/**
	 * The Body of the message.
	 */
	private String body; 

	/**
	 * The status code
	 */
	private String status;

	/**
	 * The content that will be sent back
	 */
	private byte[] content;

	private BufferedReader instream; 

	/**
	 * Creates a new request.
	 * 
	 * @param request
	 *            The plain text request
	 * 
	 * @param outstream
	 *            The output stream
	 * 
	 * @param connection
	 *            The connection socket
	 * 
	 * @throws IOException
	 */
	public Request(Socket connection) throws IOException {
		this.connection = connection;
		this.instream = new BufferedReader (new InputStreamReader(connection.getInputStream()));
		this.outstream = this.connection.getOutputStream();
	}

	/**
	 * This method actually executes the command.
	 */
	@Override
	public void run(){
		try{
			String request = this.initRequest(instream);
			this.parse(request);
			if ((this.command.equals("GET") || this.command.equals("HEAD")) && this.status == null){
				try {
					this.content = Files.readAllBytes( Paths.get("/home/batman/git/CN1/" + this.path_to_file));
					this.status = "200";
					this.outputRequest();
				} catch (IOException e){
					this.error404();
					this.outputRequest();
				}
			}else if ((this.command.equals("PUT") || this.command.equals("POST")) && this.status == null){
				// PUT or POST
				
			}else{
				// There is something wrong.
			}
		} catch (Exception e){
			System.out.println("Something went horrebly wrong");
		} finally {
			if (this.version.equals("HTTP/1.0")){
				try{
					this.connection.shutdownInput();
					this.connection.shutdownOutput();
					this.connection.close();
					notifyAll();
				} catch (Exception e){}
			} else {
				try {
					this.parse(this.initRequest(this.instream));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * This method parses the full request.
	 * 
	 * @param request
	 *            The request in flat text from.
	 * 
	 * @throws IOException
	 */
	private void parse(String request) throws IOException {
		String[] lines = request.split("\r\n");
		String commandline = lines[0];
		if (!parseHTTP(commandline)){
			this.error400();
			this.outputRequest();
		}
		if (!parseCommand(commandline)) {
			this.error400();
			this.outputRequest();
		}
		this.parsePath(commandline);
		try{
			String hostline = lines[1];
			boolean b = parseHost(hostline);
			if ( this.version.equals("HTTP/1.1") && b == false){
				this.error400();
				this.outputRequest();
			}
			String body = "";
			for (int i = 2; i < lines.length ; i ++){
				body += lines[i] + "\n";
			}
			this.body = body;
		}catch (IndexOutOfBoundsException e){
			if ( this.version.equals("HTTP/1.1")){
				this.error400();
				this.outputRequest();
			}
		}
	}

	/**
	 * This method extracts the host name from the request 
	 * 
	 * @param hostline
	 * 		  A string that contains the host name.
	 * 
	 * @return boolean
	 * 		   A boolean that indicates whether the host has been found or not.
	 */
	private boolean parseHost(String hostline) {
		Pattern pattern = Pattern.compile("Host\\: (.*)");
		Matcher matcher = pattern.matcher(hostline);
		if (matcher.find()){
			this.host = matcher.group();	
			return true;
		}else 
			return false;
	}

	/**
	 * A method that extracts the path to the file that needs to be manipulated.
	 * 
	 * @param commandline
	 *            The request in flat text from.
	 */
	private void parsePath(String commandline) {
		Pattern pattern = Pattern.compile("([a-z]|/)*\\.[a-z]*");
		Matcher matcher = pattern.matcher(commandline);
		if (matcher.find())
			this.path_to_file = matcher.group();
	}

	/**
	 * This method extracts the command from the first line of the request.
	 * 
	 * @param commandline
	 *            The first line of the request (command line)
	 * @return boolean True if the command was recognised.
	 */
	private boolean parseCommand(String commandline) {
		Pattern pattern = Pattern.compile("(GET|PUT|HEAD|POST)");
		Matcher matcher = pattern.matcher(commandline);
		if (matcher.find()) {
			this.command = matcher.group();
			return true;
		} else
			return false;
	}

	/**
	 * A method that extract the HTTP version from the request.
	 * 
	 * @param commandline
	 *            The first line of the request (command line)
	 * 
	 * @return boolean True if the HTTP version was updated. False otherwise.
	 */
	private boolean parseHTTP(String commandline) {
		Pattern pattern = Pattern.compile("HTTP/\\d.\\d");
		Matcher matcher = pattern.matcher(commandline);
		if (matcher.find()) {
			this.version = matcher.group();
			return true;
		} else
			return false;
	}

	/**
	 * This method sets a 400 BAD request status.
	 */
	private void error400() throws IOException {
		this.status = "400";
		String temp  = "<html>"
				+ "<body>"
				+ "<h1> No Host: header received</h1>"
				+ "</body>"
				+ "</html>";
		this.content = temp.getBytes();
	}

	/**
	 * This method sets a 500 Server error status.
	 */
	private void error500(){
		this.status = "500";
		String temp  = "<html>"
				+ "<body>"
				+ "<h1> 500 Server ERROR</h1>"
				+ "</body>"
				+ "</html>";
		this.content = temp.getBytes();
	}

	/**
	 * This method sets a 500 File not found status.
	 */
	private void error404(){
		this.status = "404";
		String temp  = "<html>"
				+ "<body>"
				+ "<h1> 404 File not found </h1>"
				+ "</body>"
				+ "</html>";
		this.content = temp.getBytes();
	}

	/**
	 * This method does the actual delivery of the request.
	 * @throws IOException 
	 */
	private void outputRequest() throws IOException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		this.outstream.write((this.version + " " + this.status + "\r\n").getBytes());
		this.outstream.write((dateFormat.format(Calendar.getInstance().getTime()) + "\r\n").getBytes());
		this.outstream.write(("Content-Type: " + this.getContentType() + "\r\n").getBytes());
		this.outstream.write(("Content-Length: " + content.length + "\r\n").getBytes());
		this.outstream.write(("\r\n").getBytes());
		if (!this.command.equals("HEAD"))
			this.outstream.write(content);
		this.outstream.flush();
	}

	private String getContentType(){
		Pattern pattern = Pattern.compile("\\/[a-z]*\\.([a-z]*)");
		Matcher matcher = pattern.matcher(this.path_to_file);
		String subtype = "";
		if (matcher.find()) 
			subtype = matcher.group();
		if(subtype.equals("jpeg") || subtype.equals("jpg") || subtype.equals("png") || subtype.equals("gif"))
			return "image/"+ subtype ;
		return "text/html";
	}

	private String initRequest(BufferedReader instream) throws IOException{ 
		String request = "";
		while (true) {
			String line = this.instream.readLine();
			if (line.equals("")) {
				break;
			}
			request += line + "\r\n";
		}
		return request;
	}
}