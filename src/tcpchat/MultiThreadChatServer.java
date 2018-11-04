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

/*
 * A chat server that delivers public and private messages.
 */
import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
 * A chat server that delivers public and private messages.
 */
public class MultiThreadChatServer {

    // The server socket.
    private static ServerSocket serverSocket = null;
    // The client socket.
    private static Socket clientSocket = null;

    // This chat server can accept up to maxClientsCount client's connections.
    private static final int maxClientsCount = 10;
    private static final clientThread[] threads = new clientThread[maxClientsCount];

    public static void main(String args[]) {

        // The default port number.
        int portNumber = 2222;
        if (args.length < 1) {
            System.out.println("Usage: java MultiThreadChatServerSync <portNumber>\n" + "Now using port number=" + portNumber);
        } 
        else {
            portNumber = Integer.valueOf(args[0]).intValue();
        }

      //Open a server socket on the portNumber 

        try {
            serverSocket = new ServerSocket(portNumber);
        } 
        catch (IOException e) {
            System.out.println(e);
        }

        /*
         * Create a client socket for each connection and pass it to a new client
         * thread.
         */
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                int i;
                for (i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == null) {
                        threads[i] = new clientThread(clientSocket, threads);
                        threads[i].start();
                        break;
                    }
                }
                if (i == maxClientsCount) {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("Server too busy. Try later.");
                    os.close();
                    clientSocket.close();
                }
            } 
            catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}

//Client thread
class clientThread extends Thread {

    private String clientName = null;
    private DataInputStream is = null;
    private PrintStream os = null;
    private Socket clientSocket = null;
    private final clientThread[] threads;
    private int maxClientsCount;

    public clientThread(Socket clientSocket, clientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
    }
    
    Thread.UncaughtExceptionHandler disconnectHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread clientThread, Throwable exception) {
            System.out.println("This shit is real");
        }
    };

    @Override
    public void run() {
        int maxClientsCount = this.maxClientsCount;
        clientThread[] threads = this.threads;

        try {
            // Create input and output streams for this client.      
            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());
            String name;
            while (true) {
                os.println("Enter your name.");
                name = is.readLine().trim();
                if (name.indexOf('@') == -1 && !name.equals("")) {
                    break;
                } 
                else {
                    os.println("The name should not contain '@' character or no character");
                }
            }

            //New client connected
            os.println("Welcome " + name + " to our chat room.\nTo leave enter /quit in a new line.");
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] == this) {
                        clientName = "@" + name;
                        break;
                    }
                }
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] != this) {
                        threads[i].os.println("*** A new user " + name + " has entered the chat room !!! ***");
                    }
                }
            }
            //Start the conversation
            while (true) {
                String line = is.readLine();
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();
                String serverTime = dateFormat.format(cal.getTime());
                if (line.startsWith("/quit")) {
                    break;
                }
                //Private message
                if (line.startsWith("@")) {
                    String[] words = line.split("\\s", 2);
                    if (words.length > 1 && words[1] != null) {
                        words[1] = words[1].trim();
                        if (!words[1].isEmpty()) {
                            synchronized (this) {
                                for (int i = 0; i < maxClientsCount; i++) {
                                    if (threads[i] != null && threads[i] != this && threads[i].clientName != null
                                    && threads[i].clientName.equals(words[0])) {
                                        threads[i].os.println("<" + name + ">(private): " + words[1]);
                                        /*
                                         * Echo this message to let the client know the private
                                         * message was sent.
                                         */
                                        this.os.println(">" + name + "> " + words[1]);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } 
                else {
                  //Send message to all client
                    synchronized (this) {
                        for (int i = 0; i < maxClientsCount; i++) {
                            if (threads[i] != null && threads[i].clientName != null && threads[i] != this) {
                                threads[i].os.println("<" + name + "> " + serverTime + " : " + line);
                            }
                        }
                    }
                }
            }
            synchronized (this) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();
                String serverTime = dateFormat.format(cal.getTime());                
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] != this && threads[i].clientName != null) {
                        threads[i].os.println("*** The user " + name + " has left chat room at " + serverTime);
                    }
                }
            }
            os.println("*** Bye " + name + " ***");

            //Clean up after client disconnected
            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
            }
            //Close streams and socket
            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println(e + " Client disconnected");
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] != null && threads[i] != this && threads[i].clientName != null) {
                    threads[i].os.println("*** The user " + clientName + " has been disconnected from the server");
                }
            }
        }
            
//        These line will alert the whole room even if Client disconnected properly
//
//        for (int i = 0; i < maxClientsCount; i++) {
//            if (threads[i] != null && threads[i] != this && threads[i].clientName != null) {
//                threads[i].os.println("*** The user " + clientName + " has been disconnected from the server");
//            }
//        }
    }
}