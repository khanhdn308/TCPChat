/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpchat;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Khanh
 */
public class MultiRoomServer{
    public static void main(String[] args) {
        int port = 2221;
        for (int i = 0; i < 2; i++) {
            MultiThreadChatServer.main(args);
        }
    }
 
}
