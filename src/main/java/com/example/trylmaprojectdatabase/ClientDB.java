package com.example.trylmaprojectdatabase;

import com.example.trylmaproject.client.BoardGuiFrame;
import com.example.trylmaproject.client.Client;
import com.example.trylmaproject.client.QueFrame;
import com.example.trylmaproject.client.StartingFrame;
import com.example.trylmaproject.server.Field;
import com.example.trylmaproject.server.Player;
import com.example.trylmaprojectdatabase.server.GameDatabase;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientDB implements Runnable{
    private String serverAddress;
    private PrintWriter out;
    private ObjectInputStream ois;
    public QueFrame queFrame;
    public StartingFrame startingFrame;
    public BoardGuiFrame boardGuiFrame;
    private Field[][] board;
    private int playerNumber;
    private int port;


    public ClientDB(String serverAddress, int port)
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

            if(playerNumber == 1)
            {
                startingFrameCreation();
                while(true)
                {
                    if(startingFrame.getTypeOfGame() == GameDatabase.LOADED_GAME)
                    {
                        out.println("WCZYTAJ_GRĘ");
                        out.println(startingFrame.getGameId());
                        out.println(startingFrame.getRoundId());
                        startingFrame.setVisible(false);
                        break;
                    }
                    else
                    {
                        out.println("NOWA_GRA");
                        startingFrame.setVisible(false);
                        break;
                    }
                }
            }

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
    public void startingFrameCreation()
    {
        startingFrame.setDefaultCloseOperation(3);
        startingFrame.setTitle("Kontynuuj czy rozpocznij nową grę?");
        startingFrame.setVisible(true);
        startingFrame.setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        com.example.trylmaproject.client.Client client = new com.example.trylmaproject.client.Client("localhost", 58090);
        client.run();
    }
}