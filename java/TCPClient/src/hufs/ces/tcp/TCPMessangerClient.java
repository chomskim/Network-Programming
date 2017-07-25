package hufs.ces.tcp;
import java.net.*;

import java.io.*;

public class TCPMessangerClient {

	final static int DEFAULT_PORT = 7070;
	final static String DEFAULT_HOST = "192.168.219.154";
	//final static String DEFAULT_HOST = "220.67.121.119";
	//final static String DEFAULT_HOST = "localhost";

	public volatile boolean stopClient = false;

	public TCPMessangerClient() {
		
		String hostname = DEFAULT_HOST;

		BufferedReader userIn = null;
		PrintWriter out = null;
		try {
			Socket theSocket = new Socket(hostname, DEFAULT_PORT);
			userIn = new BufferedReader(new InputStreamReader(System.in));
			out = new PrintWriter(theSocket.getOutputStream());
			System.out.println("Connected to Messanger server");

			ReceiverThread receiver = new ReceiverThread(theSocket);
			receiver.start();

			while (true) {
				if (stopClient) {
					receiver.halt();
					break;
				}
				
				String theLine = userIn.readLine();
				if (theLine.equals(".")) {
					out.println("END");
					out.flush();
					receiver.halt();
					break;
				}
				out.println(theLine);
				out.flush();
			}

		}  // end try
		catch (IOException e) {
			System.err.println(e);
		}
		finally {
			try {
				if (userIn != null) userIn.close(); 
				if (out != null) out.close(); 
			}
			catch (Exception e) {}
		}

	}

	public static void main(String[] args) {

		TCPMessangerClient tmc = new TCPMessangerClient();
		
	}  // end main
	class ReceiverThread extends Thread {

		Socket socket;
		private boolean stopped = false;

		public ReceiverThread(Socket socket) throws SocketException {
			this.socket = socket;
		}

		public void halt() {
			stopClient = true;
			this.stopped = true; 
		}

		public void run() {

			BufferedReader networkIn = null;
			try {
				networkIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				while (true) {
					if (stopped) break;

					try {
						String outMsg = networkIn.readLine();
						if (outMsg==null || outMsg.equals("END")) {
							stopClient = true;
							break;
						}
						System.out.println(">>"+outMsg);

					} catch (IOException e) {
						System.err.println(e);
					}
				}  
			} catch (IOException e) {
				System.err.println(e);
			}
			finally {
				try {
					if (networkIn != null) networkIn.close(); 
				}
				catch (IOException e) {}
			}

		}
	}
}  


