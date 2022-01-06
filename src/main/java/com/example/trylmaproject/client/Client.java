package com.example.trylmaproject.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    String serverAddress;
    Scanner in;
    PrintWriter out;
    QueFrame queFrame;
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


            while (in.hasNextLine())
            {
                var line = in.nextLine();
                if(line.startsWith("NUMER: "))
                    playerNumber = Integer.parseInt(line.substring(8));
                queFrame = new QueFrame(playerNumber);
                if (line.startsWith("IMIE:") && queFrame.isNameReadyToSend())
                    out.println(queFrame.getNameToServer());
                if(queFrame.isReadyToPlay())
                    out.println("START");
            }
        }
    }
}
