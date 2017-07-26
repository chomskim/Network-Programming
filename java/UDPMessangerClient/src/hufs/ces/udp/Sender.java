package hufs.ces.udp;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Sender {
	private InetAddress server;
	private DatagramSocket socket;
	private int port;
	private boolean stopped = false;

	public Sender(InetAddress inet, int port) {
		try {
			this.server = inet;
			this.port = port;
			this.socket = new DatagramSocket();
			this.socket.connect(server, port);

			//byte [] connectStr = "**connect**".getBytes();
			//DatagramPacket output = new DatagramPacket(connectStr, connectStr.length, server, port);
			//this.socket.send(output);
		} 
		catch (SocketException ex) {
			System.err.println(ex);
		}
	}

	public void halt() {
		this.stopped = true; 
	}

	public DatagramSocket getSocket() {
		return this.socket; 
	}

	public void send(String theLine) {
		try {

			byte[] data = theLine.getBytes();
			DatagramPacket output 
			= new DatagramPacket(data, data.length, server, port);
			socket.send(output);

		}  // end try
		catch (IOException ex) {
			System.err.println(ex);
		}

	}  // end run

}
