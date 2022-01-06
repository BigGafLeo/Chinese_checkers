package com.example.trylmaproject.client;

import com.example.trylmaproject.server.Field;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    String serverAddress;
    Scanner in;
    PrintWriter out;
    ObjectInputStream ois;
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
            var socket = new Socket(serverAddress,59001);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(),true);
            ois = new ObjectInputStream(socket.getInputStream());



            while (in.hasNextLine())
            {
                var line = in.nextLine();
                if(line.startsWith("NUMER: "))
                    playerNumber = Integer.parseInt(line.substring(8));
                queFrame = new QueFrame(playerNumber);
                if (line.startsWith("IMIE:") && queFrame.isNameReadyToSend())
                    out.println(queFrame.getNameToServer());
                if(playerNumber == 1){
                    do{}
                    while(queFrame.isReadyToPlay());
                    out.println("START");
                }
                board = (Field[][])ois.readObject();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
