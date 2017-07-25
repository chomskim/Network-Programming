package hufs.ces.tcp;
// 
//	EchoServer.java
//
import java.net.*;
import java.io.*;

public class EchoServer {
	public final static int DEFAULT_PORT = 7007;

	public static void main(String[] args) {
		int port = DEFAULT_PORT;     
		if (args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
				if (port < 0 || port >= 65536) {
					System.out.println("Port must between 0 and 65535");
					return;      
				}
			}   
			catch (NumberFormatException ex) {
				// use default port
			}  
		}     
		try {

			ServerSocket server = new ServerSocket(port);

			while (true) {
				Socket  connection = server.accept();        
				System.out.println("Connection.: Inet " + connection.getInetAddress() 
				+ ":" + connection.getPort()
				+ "		Local"	+ connection.getLocalAddress() 
				+ "	:" + connection.getLocalPort());

				Thread echoThread = new EchoThread(connection);
				echoThread.start();
			}  // end while
		}  // end try
		catch (IOException ex) {
			System.err.println(ex);
		} // end catch
	} // end main
} // end Echo Server
class EchoThread extends Thread {
	BufferedReader netIn;
	Writer netOut;
	Socket con;

	public EchoThread(Socket con) {
		try {
			this.con = con;
			this.netIn = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			this.netOut = new OutputStreamWriter(con.getOutputStream());			
		} catch (IOException e) {
			System.err.println(e);
		}
	}
	public void run()  {
		try {     
			while (true) {
				int data = netIn.read();
				if ( data == -1) break;
				netOut.write(data);
				netOut.flush();
			}
		}
		catch (SocketException ex) {
			// the socket closed 
		}
		catch (IOException ex) {
			System.err.println(ex);
		}
		try {
			netIn.close();
			netOut.close();
			con.close();
		}
		catch (IOException ex) { 
		} 
	}
}
