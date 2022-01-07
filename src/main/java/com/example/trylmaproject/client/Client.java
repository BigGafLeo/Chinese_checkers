package com.example.trylmaproject.client;

import com.example.trylmaproject.server.Field;
import com.example.trylmaproject.server.Game;

import java.awt.*;
import java.io.*;
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
            //in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(),true);
            //oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            //BufferedReader r = new BufferedReader(new InputStreamReader(ois));
            var line = (String)ois.readObject();
            if(line.startsWith("NUMER: "))
                playerNumber = Integer.parseInt(line.substring(7));
            queFrame = new QueFrame(playerNumber);
            queFrameCreaction();
            do{}
            while(!((String)ois.readObject()).startsWith("IMIE:"));
            while(true){
                synchronized (queFrame){
                    queFrame.wait();
                }
                if(queFrame.isNameReadyToSend()){
                    out.println(queFrame.getNameToServer());
                    break;
                }
                else{

                }
            }


            if(playerNumber == 1){
                while(true) {
                    synchronized (queFrame) {
                        queFrame.wait();
                    }
                    if(queFrame.isReadyToPlay()){
                        out.println("START");
                        if (ois.readObject().equals("JESZCZE_RAZ")) {
                        }
                        else break;
                    }
                }
            }
            board = (Field[][])ois.readObject();
            System.out.println("tak");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
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
