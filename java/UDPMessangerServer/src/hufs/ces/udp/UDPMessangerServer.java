package hufs.ces.udp;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class UDPMessangerServer extends Thread {

	public final static int DEFAULT_PORT = 7770;
	private int bufferSize; // in bytes
	protected DatagramSocket socket;
	private HashMap<SocketAddress,SocketAddress> clientPair = null;
	
	public UDPMessangerServer(int port, int bufferSize) 
	throws SocketException {
		this.bufferSize = bufferSize;
		this.socket = new DatagramSocket(port);
		clientPair = new HashMap<SocketAddress,SocketAddress>();
	}
	public UDPMessangerServer(int port) throws SocketException {
		this(port, 8192);
	}
	public UDPMessangerServer() throws SocketException {
		this(DEFAULT_PORT); 
	}

	public void run() {	  
		byte[] buffer = new byte[bufferSize];
		SocketAddress theSender;
		SocketAddress theReceiver;
		while (true) {
			DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
			try {			
				socket.receive(incoming);
				theSender = incoming.getSocketAddress();

				Set<SocketAddress> keys = new HashSet<SocketAddress>(clientPair.keySet());
				if (!keys.contains(theSender)) { // new comer
					System.out.println(theSender + "	connected");
					clientPair.put(theSender, theSender);

				}
				theReceiver = clientPair.get(theSender);

				if (theReceiver == theSender) { // sender has no partner
					keys.remove(theSender);
					theReceiver = selectReceiver(keys);
					if (theReceiver == null) 
						theReceiver = theSender;
					else { // new Pair is made
						clientPair.put(theSender, theReceiver);
						clientPair.put(theReceiver, theSender);
					}
				}
				respond(incoming, theReceiver);

			}
			catch (IOException e) {
				System.err.println(e);
			}
		} // end while

	}  // end run
	private SocketAddress selectReceiver(Set<SocketAddress> keys) {
		// find first null valued key
		Iterator<SocketAddress> it = keys.iterator();
		while (it.hasNext()) {
			SocketAddress rec = (SocketAddress) it.next();

			if (clientPair.get(rec) == rec)	return rec;

		}
		return null; // nobody free;
		
	}

	public void respond(DatagramPacket packet, SocketAddress receiver) {

		try {
			DatagramPacket outgoing = new DatagramPacket(packet.getData(), packet.getLength(), receiver);
			// For logging 
			byte[] data = new byte[packet.getLength()];
			System.arraycopy(packet.getData(), 0, data, 0, packet.getLength());
			String s = new String(data, "8859_1");
			//System.out.println(receiver +  " says:" + s+ ":");
			// now send
			if (!s.equals("**connect**")) {
				socket.send(outgoing);
			}
		}
		catch (java.io.UnsupportedEncodingException ex) {
			// This shouldn't happen
		}
		catch (IOException ex) {
			System.err.println(ex);
		}

	}

	public static void main(String[] args) {

		try {
			UDPMessangerServer server = new UDPMessangerServer();
			server.start();
		}
		catch (SocketException ex) {
			System.err.println(ex);
		}

	}

}
