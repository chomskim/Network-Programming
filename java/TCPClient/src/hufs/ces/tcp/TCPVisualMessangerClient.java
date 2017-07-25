package hufs.ces.tcp;

import javax.swing.SwingUtilities;

import hufs.ces.tcp.TCPMessangerClient.ReceiverThread;

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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;

public class TCPVisualMessangerClient extends JFrame {

	private static final long serialVersionUID = 1L;
	final static int DEFAULT_PORT = 7070;
	final static String DEFAULT_HOST = "192.168.219.154";
	//final static String DEFAULT_HOST = "220.67.121.119";
	//final static String DEFAULT_HOST = "localhost";


	private static String hostname = DEFAULT_HOST;
	private static int port = DEFAULT_PORT;

	public volatile boolean stopClient = false;

	Socket theSocket = null;
	BufferedReader userIn = null;
	PrintWriter sendMsg = null;

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
						theSocket = new Socket(hostname, DEFAULT_PORT);
						
						userIn = new BufferedReader(new InputStreamReader(System.in));
						sendMsg = new PrintWriter(theSocket.getOutputStream());
						System.out.println("Connected to Messanger server");

						ReceiverThread receiver = new ReceiverThread(theSocket);
						receiver.start();

						jtaMessage.setText(hostname + ":" + port +" connected\n");
						jbtConnect.setEnabled(false);

					} 
					catch (SocketException ex) {
						System.err.println(ex);
					} catch (IOException ex) {
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
						stopClient = true;
						jtfSender.setEnabled(false);
						jtaMessage.append("disconnected\n");
					}
					else {
						sendMsg.println(theLine);
						sendMsg.flush();
					}

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
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				TCPVisualMessangerClient thisClass = new TCPVisualMessangerClient();
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});

	}

	/**
	 * This is the default constructor
	 */
	public TCPVisualMessangerClient() {
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

		Socket socket = null;

		public ReceiverThread(Socket socket) throws SocketException {
			this.socket = socket;
		}

		public void halt() {
			stopClient = true;
		}

		public void run() {

			BufferedReader networkIn = null;
			try {
				networkIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				while (true) {
					if (stopClient) break;

					try {
						String inMsg = networkIn.readLine();
						if (inMsg==null) {
							stopClient = true;
							jtaMessage.append("partner disconnected\n");
							break;
						}
						jtaMessage.append(">>" + inMsg + "\n");
						Thread.yield();

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
