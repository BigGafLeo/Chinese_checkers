package com.example.trylmaproject.client;

import com.example.trylmaproject.server.Field;
import com.example.trylmaproject.server.Game;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    String serverAddress;
    Scanner in;
    PrintWriter out;
    ObjectInputStream ois;
    ObjectOutputStream oos;
    QueFrame queFrame;
    Field[][] board;
    int playerNumber;

    public Client(String serverAddress)
    {
        this.serverAddress = serverAddress;
    }
    private void run() throws IOException
    {
        try
        {
            var socket = new Socket(serverAddress,59090);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(),true);
            //oos = new ObjectOutputStream(socket.getOutputStream());
            //ois = new ObjectInputStream(socket.getIline = "�� NUMER: 1"nputStream());
            var line = in.nextLine();
            System.out.println(line);
            if(line.startsWith("NUMER: "))
                playerNumber = Integer.parseInt(line.substring(7));
            queFrame = new QueFrame(playerNumber);
            queFrameCreaction();
            do{}
            while(!in.nextLine().startsWith("IMIE:"));
            synchronized (queFrame){
                queFrame.wait();
            }
            out.println(queFrame.getNameToServer());
            System.out.println(queFrame.getNameToServer());
            if(playerNumber == 1){
                synchronized (queFrame){
                    queFrame.wait();
                }
                out.println("START");
            }
            //board = (Field[][])ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void queFrameCreaction()
    {
        queFrame.setDefaultCloseOperation(3);
        queFrame.setTitle("Test");
        queFrame.setVisible(true);
        queFrame.setLocationRelativeTo((Component) null);
    }

    public static void main(String[] args) {
        Client client = new Client("localhost");
        try {
            client.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
