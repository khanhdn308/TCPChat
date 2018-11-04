/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpchat;

/**
 *
 * @author Khanh
 */
import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MultiThreadChatClient implements Runnable {

    //Client socket
    private static Socket clientSocket = null;
    //Output stream
    private static PrintStream os = null;
    //Input stream
    private static DataInputStream is = null;

    private static BufferedReader inputLine = null;
    private static boolean closed = false;

    public static void main(String[] args) {

        // Default port.
        int portNumber = 2222;
        // Default host.
        String host = "localhost";

        if (args.length < 2) {
            System.out.println("Usage: java MultiThreadChatClient <host> <portNumber>\n" + "Now using host = " + host + ", portNumber = " + portNumber);
        } 
        else {
            host = args[0];
            portNumber = Integer.valueOf(args[1]).intValue();
        }


        // Open a socket on a given host and port. Open input and output streams.
        try {
            clientSocket = new Socket(host, portNumber);
            inputLine = new BufferedReader(new InputStreamReader(System.in));
            os = new PrintStream(clientSocket.getOutputStream());
            is = new DataInputStream(clientSocket.getInputStream());
        }
        catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
        }
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host " + host);
        }

        // Write data to current Socket

        if (clientSocket != null && os != null && is != null) {
            try {
                // Create a thread to read from the server
                new Thread(new MultiThreadChatClient()).start();
                while (!closed) {
                    os.println(inputLine.readLine().trim());
                }
                System.out.println("Exited");
                // Close the output stream, close the input stream, close the socket.         
                os.close();
                is.close();
                clientSocket.close();
            } 
            catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }
    }

    //Thread to read from server
    @Override
    public void run() {
        String responseLine;
        try {
            while ((responseLine = is.readLine()) != null) {
                System.out.println(responseLine);
                if (responseLine.contains("*** Bye")){
                    System.out.println("Enter anything to exit program");
                    break;
                }
            }
            closed = true;
        } 
        catch (IOException e) {
            System.err.println("IOException:  " + e);
        } 
    }
}