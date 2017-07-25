package hufs.ces.udp;

import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;

public class VisualMessangerClient extends JFrame {

	private static final long serialVersionUID = 1L;
	final static int DEFAULT_PORT = 7770;
	final static String DEFAULT_HOST = "127.0.0.1";
	//final static String DEFAULT_HOST = "220.67.121.119";
	//final static String DEFAULT_HOST = "localhost";


	private static String hostname;
	private static int port;

	private InetAddress server;
	private Sender sender = null;
	private ReceiverThread receiver = null;

	private JPanel jContentPane = null;

	private JButton jbtConnect = null;

	private JScrollPane jScrollPane1 = null;

	private JTextArea jtaMessage = null;

	private JPanel jpMessages = null;

	private JPanel jpTextArea = null;

	private JLabel jLabel1 = null;

	private JTextField jtfSender = null;

	/**
	 * This method initializes jbtConnect	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJbtConnect() {
		if (jbtConnect == null) {
			jbtConnect = new JButton();
			jbtConnect.setText("Connect");
			jbtConnect.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {

					jbtConnection();
				}

				private void jbtConnection() {
					try {
						server = InetAddress.getByName(hostname);
						sender = new Sender(server, port);

						jtaMessage.setText(hostname + ":" + port +" connected\n");
						jbtConnect.setEnabled(false);

						receiver = new ReceiverThread(sender.getSocket());
						receiver.start();
					} 
					catch (UnknownHostException ex) {
						// TODO Auto-generated catch block
						System.err.println(ex);
					} 
					catch (SocketException ex) {
						// TODO Auto-generated catch block
						System.err.println(ex);
					}

				}				

			});
		}

		return jbtConnect;
	}

	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPane1.setViewportView(getJtaMessage());
		}
		return jScrollPane1;
	}

	/**
	 * This method initializes jtaMessage	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJtaMessage() {
		if (jtaMessage == null) {
			jtaMessage = new JTextArea();
			jtaMessage.setLineWrap(true);
			jtaMessage.setEditable(false);
		}
		return jtaMessage;
	}

	/**
	 * This method initializes jpMessages	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJpMessages() {
		if (jpMessages == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(1);
			gridLayout.setHgap(4);
			gridLayout.setVgap(4);
			gridLayout.setColumns(2);
			jpMessages = new JPanel();
			jpMessages.setLayout(gridLayout);
			jpMessages.add(getJpTextArea(), null);
		}
		return jpMessages;
	}

	/**
	 * This method initializes jpTextArea	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJpTextArea() {
		if (jpTextArea == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("Messanger");
			jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
			jpTextArea = new JPanel();
			jpTextArea.setLayout(new BorderLayout());
			jpTextArea.add(getJScrollPane1(), BorderLayout.CENTER);
			jpTextArea.add(jLabel1, BorderLayout.NORTH);
			jpTextArea.add(getJtfSender(), BorderLayout.SOUTH);
		}
		return jpTextArea;
	}

	/**
	 * This method initializes jtfSender	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJtfSender() {
		if (jtfSender == null) {
			jtfSender = new JTextField();
			jtfSender.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String theLine = jtfSender.getText();

					jtaMessage.append("<<" + theLine +"\n");					
					jtfSender.setText("");

					if (theLine.equals(".")) {
						jtfSender.setEnabled(false);
						jtaMessage.append("disconnected\n");
					}
					else
						sender.send(theLine);

				}
			});
		}
		return jtfSender;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		hostname = DEFAULT_HOST;
		//hostname = "localhost";
		port = DEFAULT_PORT;

		if (args.length > 0) {
			hostname = args[0];
		}
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				VisualMessangerClient thisClass = new VisualMessangerClient();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});

	}

	/**
	 * This is the default constructor
	 */
	public VisualMessangerClient() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(373, 319);
		this.setContentPane(getJContentPane());
		this.setTitle("Messanger Client");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJpMessages(), BorderLayout.CENTER);
			jContentPane.add(getJbtConnect(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}
	class ReceiverThread extends Thread {

		DatagramSocket socket;
		private boolean stopped = false;

		public ReceiverThread(DatagramSocket ds) throws SocketException {
			this.socket = ds;
			socket.setSoTimeout(10000); // 10 sec.
		}

		public void halt() {
			this.stopped = true; 
		}

		public void run() {

			byte[] buffer = new byte[65507];
			while (true) {
				if (stopped) return;
				DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
				try {
					socket.receive(dp);
					String s = new String(dp.getData(), 0, dp.getLength());

					jtaMessage.append(">>" + s + "\n");
					Thread.yield();
				}
				catch (SocketTimeoutException ex) {
					//System.err.println(ex);
				} 
				catch (IOException ex) {
					System.err.println(ex);
				} 

			}  

		}

	}

}
