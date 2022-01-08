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
    BoardGuiFrame boardGuiFrame;
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
            System.out.println("tak");
            queFrame.setVisible(false);


            while (true)
            {
                board = (Field[][])ois.readObject();
                if(boardGuiFrame == null){
                    boardGuiFrame = new BoardGuiFrame(playerNumber,board);
                    boardGuiFrameCreaction();
                }
                else boardGuiFrame.boardRepaint(board);
                line = (String)ois.readObject();
                if(line.startsWith("KONIEC_GRY: ")){
                    boardGuiFrame.endGame(line.substring(12));
                    break;
                }
                line = (String)ois.readObject();
                if(line.startsWith("ZWYCIEZCA: ")){
                    boardGuiFrame.whoWinner(line.substring(11));
                }
                line = (String)ois.readObject();
                if(line.equals("KOLEJKA: TAK")){
                    boardGuiFrame.isYourTurn(true);
                    /*
                    synchronized (boardGuiFrame) {
                        boardGuiFrame.wait();
                    }
                    */
                    while(true){
                        out.println(boardGuiFrame.getMove());
                        line = (String)ois.readObject();
                        if (line.equals("AKCEPTACJA")){
                            boardGuiFrame.isYourTurn(false);
                            break;
                        }
                    }

                }
                else boardGuiFrame.isYourTurn(false);
            }

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
        queFrame.setTitle("Kolejka");
        queFrame.setVisible(true);
        queFrame.setLocationRelativeTo((Component) null);
    }
    private void boardGuiFrameCreaction()
    {
        boardGuiFrame.setDefaultCloseOperation(3);
        boardGuiFrame.setTitle("Warcaby");
        boardGuiFrame.setVisible(true);
        boardGuiFrame.setLocationRelativeTo((Component) null);
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
