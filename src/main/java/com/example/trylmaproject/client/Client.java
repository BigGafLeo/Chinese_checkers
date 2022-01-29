package com.example.trylmaproject.client;

import com.example.trylmaproject.server.Field;
import com.example.trylmaproject.server.Player;

import java.io.*;
import java.net.Socket;

public class Client implements Runnable{
    String serverAddress;
    PrintWriter out;
    ObjectInputStream ois;
    public QueFrame queFrame;
    public BoardGuiFrame boardGuiFrame;
    Field[][] board;
    int playerNumber;
    int port;

    public Client(String serverAddress, int port)
    {
        this.serverAddress = serverAddress;
        this.port = port;
    }
    public void run()
    {
        try
        {
            var socket = new Socket(serverAddress,port);
            out = new PrintWriter(socket.getOutputStream(),true);
            ois = new ObjectInputStream(socket.getInputStream());
            var line = (String)ois.readObject();
            if(line.startsWith("NUMER: "))
                playerNumber = Integer.parseInt(line.substring(7));
            queFrame = new QueFrame(playerNumber);
            queFrameCreaction();
            do{}
            while(!((String)ois.readObject()).startsWith("IMIE:"));
            while(true){
                if(queFrame.isNameReadyToSend()){
                    out.println(queFrame.getNameToServer());
                    break;
                }
            }


            if(playerNumber == 1){
                while(true) {
                    if(queFrame.isReadyToPlay()){
                        out.println("START");
                        if (ois.readObject().equals("JESZCZE_RAZ")) {
                        }
                        else break;
                    }
                }
            }
            System.out.println("tak");


            while (true) {

                board = (Field[][])ois.readObject();
                if(boardGuiFrame == null){
                    queFrame.setVisible(false);
                    boardGuiFrame = new BoardGuiFrame(playerNumber,board,(Player[])ois.readObject());
                }
                else {
                    boardGuiFrame.boardRepaint(board);
                    boardGuiFrame.setPlayerList((Player[])ois.readObject());
                }
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
                    boardGuiFrame.setTurn(true);
                    while(true){

//                        synchronized (boardGuiFrame){
//                            try {
//                                boardGuiFrame.wait();
//                                line = boardGuiFrame.getMessage();
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }

                        line = boardGuiFrame.getMessage();
                        if(line.equals("BREAK")){
                            boardGuiFrame.setTurn(false);
                            out.println("POMIN");
                            break;
                        }
                        out.println(line);
                        line = (String)ois.readObject();
                        if (line.equals("AKCEPTACJA")){
                            boardGuiFrame.boardRepaint((Field[][])ois.readObject());
                        }
                        else{
                            boardGuiFrame.boardRepaint((Field[][])ois.readObject());
                        }
                    }
                }
                else boardGuiFrame.setTurn(false);
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void queFrameCreaction()
    {
        queFrame.setDefaultCloseOperation(3);
        queFrame.setTitle("Kolejka gracz: "+ playerNumber);
        queFrame.setVisible(true);
        queFrame.setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 58090);
        client.run();
    }
}
