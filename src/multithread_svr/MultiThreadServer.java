/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multithread_svr;

/**
 *
 * @author Khanh
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiThreadServer implements Runnable {
    Socket csocket;
    MultiThreadServer(Socket csocket) {
        this.csocket = csocket;
   }
    public static void main(String args[]) { 
        try {
            ServerSocket ssock = new ServerSocket(1234);
            System.out.println("Listening");
            
            while (true) {
                Socket sock = ssock.accept();
                System.out.println("Connected");
                new Thread(new MultiThreadServer(sock)).start();
            }
        } catch (IOException ex) {
            Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void run() {
        try {
            ObjectInputStream ois = new ObjectInputStream(csocket.getInputStream());
            
            int first = (int) ois.readObject();
            int second = (int) ois.readObject();
            char sym = (char) ois.readObject();
            
            String result = Thread.currentThread().getName() + " : " + first + " " + sym + " " + second + " = " + Calculate(first, second, sym);
            System.out.println(result);
//            ObjectOutputStream oos = new ObjectOutputStream(csocket.getOutputStream());
//            oos.writeObject(result);
            

            csocket.close();
        }catch (IOException e) {
            e.printStackTrace();
      }catch (ClassNotFoundException ex) {
            Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int Calculate(int number1, int number2, char symbol){
        int temp = 0;
        switch(symbol){
            case '+':
                temp = number1 + number2;
                break;
            case '-':
                temp = number1 - number2;
                break;
            case '*':
                temp = number1 * number2;
                break;
            case '/':
                temp = number1 / number2;
                break;
            default:
                return 0;
        }
        return temp;
    }
}