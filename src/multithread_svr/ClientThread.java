/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multithread_svr;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Khanh
 */
public class ClientThread extends Thread{
    Socket clientSocket;
    int clientID;
    int first;
    int second;
    char symbol;

    public ClientThread(Socket clientSocket, int clientID, int first, int second, char symbol) {
        this.clientSocket = clientSocket;
        this.clientID = clientID;
        this.first = first;
        this.second = second;
        this.symbol = symbol;
    }
        
    @Override
    public void run(){
        System.out.println("Client no " + clientID + " : " + first + " " + symbol + " " + second);
        try {
            try (ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream())) {
                oos.writeObject(first);
                oos.writeObject(second);
                oos.writeObject(symbol);
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
