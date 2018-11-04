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
public class Client {
    
    public static void sendCalc(String hostName, int port, int first, int second, char sym ) throws IOException{
        try (Socket socket = new Socket(hostName, port); ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            
            oos.writeObject(first);
            oos.writeObject(second);
            oos.writeObject(sym);
            
        }
    }
    
    public static void main(String[] args) {
        int port = 1234;
        String host = "localhost";
        
        int number1[] = {1, 3, 6, 18, 5};
        int number2[] = {2, 4, 8, 6, 8};
        char symbol[] = {'+', '-', '*', '/', '+'};
        
        try {           
            for (int i = 0; i < number1.length; i++) {
                sendCalc(host, port, number1[i], number2[i], symbol[i]);
            }
            System.out.println("Request sent");

//            Socket socket = new Socket("localhost", port);
//            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//            
//            int first = 1;
//            int second = 2;
//            char sym = '+';
//            
//            oos.writeObject(first);
//            oos.writeObject(second);
//            oos.writeObject(sym);
//            
//            oos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
