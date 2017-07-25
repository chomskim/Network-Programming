package hufs.ces.tcp;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class TCPMessangerServer{

	final static int DEFAULT_PORT = 7070;

	public static void main(String[] args) {

		int port = DEFAULT_PORT;     

		try {
			ServerSocket server = new ServerSocket(port);
			while (true) {
				System.out.println("Wait for Two Client Connection");
				Socket  con1 = server.accept();
				Socket  con2 = server.accept();
				System.out.println("Connection: Inet " + con1.getInetAddress() 
				+ ":" + con1.getPort()
				+ "		Local"	+ con1.getLocalAddress() 
				+ "	:" + con1.getLocalPort());

				System.out.println("Connection: Inet " + con2.getInetAddress() 
				+ ":" + con2.getPort()
				+ "		Local"	+ con2.getLocalAddress() 
				+ "	:" + con2.getLocalPort());

				Thread mess1Thread = new MessangerThread(con1, con2);
				Thread mess2Thread = new MessangerThread(con2, con1);
				mess1Thread.start();
				mess2Thread.start();
				/*
				try {
					mess1Thread.join();
					mess2Thread.join();
				} catch (InterruptedException e) {
					System.err.println(e);
				}
				con1.close();
				con2.close();
				 */
			}  // end while
		}  // end try
		catch (IOException ex) {
			System.err.println(ex);
		} // end catch
	} // end main
} // end Messanger Server

class MessangerThread extends Thread {
	BufferedReader netIn;
	PrintWriter netOut;

	public MessangerThread(Socket conFrom, Socket conTo) {
		try {
			this.netIn = new BufferedReader(new InputStreamReader(conFrom.getInputStream()));
			this.netOut = new PrintWriter(conTo.getOutputStream());			
		} catch (IOException e) {
			System.err.println(e);
		}
	}
	public void run()  {
		try {     
			while (true) {
				String messageLine = netIn.readLine();
				if (messageLine==null) break;
				netOut.println(messageLine);
				netOut.flush();
			}
		}
		catch (SocketException ex) {
			System.err.println(ex);
		}
		catch (IOException ex) {
			System.err.println(ex);
		}
		try {
			if (netIn != null) netIn.close(); 
			if (netOut != null) netOut.close(); 
		}
		catch (IOException ex) { } 
	}
}