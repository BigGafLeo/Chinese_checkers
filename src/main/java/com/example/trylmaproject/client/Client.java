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

    public Client(String serverAddress)
    {
        this.serverAddress = serverAddress;

        queFrame = new QueFrame();
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
                if (line.startsWith("IMIE:") && queFrame.isNameReadyToSend())
                    out.println();
            }
        }
    }
}
