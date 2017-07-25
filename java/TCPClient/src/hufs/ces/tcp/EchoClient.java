package hufs.ces.tcp;
import java.net.*;
import java.io.*;

public class EchoClient {

  public static void main(String[] args) {

    String hostname = "192.168.219.154";

    if (args.length > 0) {
      hostname = args[0];
    }

    PrintWriter out = null;
    BufferedReader networkIn = null;
    Socket theSocket = null;
    try {
      theSocket = new Socket(hostname, 8890);
      networkIn = new BufferedReader(
       new InputStreamReader(theSocket.getInputStream()));
      BufferedReader userIn = new BufferedReader(
       new InputStreamReader(System.in));
      out = new PrintWriter(theSocket.getOutputStream());
      System.out.println("Connected to echo server");

      while (true) {
        String theLine = userIn.readLine();
        if (theLine.equals(".")) break;
        out.println(theLine);
        out.flush();
        System.out.println(networkIn.readLine());
      }
      
    }  // end try
    catch (IOException e) {
      System.err.println(e);
    }
    finally {
      try {
        if (networkIn != null) networkIn.close(); 
        if (out != null) out.close(); 
        if (theSocket != null) theSocket.close();
      }
      catch (IOException e) {}
    }

  }  // end main

}  // end EchoClient
