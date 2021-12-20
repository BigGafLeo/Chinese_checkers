package com.example.trylmaproject.server;

import com.example.trylmaproject.exceptions.IllegalMoveException;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Kontroler gry do chińskich warcabów
 * @author Mateusz Teplicki
 */
public class Game implements Runnable{

    private final ServerSocket serverSocket;

    /**
     * Tablica wątków obsługujących poszczególnych graczy
     */
    protected PlayerThread[] players = new PlayerThread[6];

    /**
     * Model do gry w chińskie warcaby (patrz: MVC)
     */
    private Board board;

    /**
     * Licznik graczy, którzy dołączyli do gry
     */
    private int playerNumber = 0;
    private final boolean GAME_STARTED = false;
    private boolean GAME_ENDED = false;

    /**
     * Zmienna wskazująca na gracza o danym numerze, który ma teraz ruch
     */
    private int whoseTurn = 0;

    /**
     * Ile graczy jeszcze nie wygrało
     */
    private int howManyPlayersActive;

    Game(int serverSocketNumber) throws IOException {
        serverSocket = new ServerSocket(serverSocketNumber);
    }

    public void run() {
        ExecutorService threads = Executors.newFixedThreadPool(6);
        while(playerNumber<6 || !GAME_STARTED){
            playerNumber++;
            try {
                threads.execute(players[playerNumber] = new PlayerThread(serverSocket.accept(), playerNumber));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        board = new Board(playerNumber);
        newTurn();
    }

    public void newTurn(){
        if(!GAME_ENDED){
            howManyPlayersActive = calculateHowManyPlayersActive();

            if(howManyPlayersActive < 2){
                GAME_ENDED = true;
            }
            else{
                setPlayerTurn();
            }
            awakeAllPlayers();
        }
    }

    void setPlayerTurn(){
        if(players[whoseTurn] != null && !players[whoseTurn].isWinner()){
            players[whoseTurn].setTurn();
        }
        else{
            whoseTurn = (whoseTurn + 1) % playerNumber;
            setPlayerTurn();
        }
    }

    void awakeAllPlayers(){
        for(PlayerThread thread : players){
            thread.notify();
        }
    }

    int calculateHowManyPlayersActive(){
        int counter = 0;
        for(PlayerThread player : players){
            if(player != null && !player.isWinner()) counter++;
        }
        return counter;
    }

    void announceLastWinner(String name){
        for(PlayerThread player : players){
            if(player != null) player.lastWinner = name;
        }
    }



    class PlayerThread implements Runnable{

        private ObjectOutputStream oos;
        private final Socket socket;
        private final int number;
        private PrintWriter out;
        private Scanner in;
        private String name;
        private String lastWinner;
        private boolean IS_YOUR_TURN = false;

        PlayerThread(Socket socket, int number){
            this.socket=socket;
            this.number=number;
        }

        public void setTurn(){
            IS_YOUR_TURN = true;
        }

        public boolean isWinner(){return board.isWinner(number);}

        public void waitForNewTurn(){
            //localTurn++;
            //IS_YOUR_TURN = false;
            //while(localTurn<turn){}
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try{
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream());
                oos = new ObjectOutputStream(socket.getOutputStream());
                do {
                    out.println("IMIĘ:");
                    name = in.nextLine();
                    if (name == null) return;
                } while (name.isBlank());
                out.println("IMIĘ_ZAAKCEPTOWANE");
                out.println("NUMER: " + number);
                while(true){
                    waitForNewTurn();
                    oos.writeObject(board.getBoard());
                    if(GAME_ENDED){
                        out.println("KONIEC_GRY: " + lastWinner);
                        break;
                    }
                    if(!Objects.equals(lastWinner, "")) out.println("ZWYCIEZCA: " + lastWinner);
                    else out.println("BRAK_ZWYCIEZCY");
                    lastWinner = "";

                    if(IS_YOUR_TURN){
                        out.println("KOLEJKA: TAK");
                        while(true){

                            String[] commandArray = in.nextLine().split(" ");
                            synchronized (board) {
                                if (Objects.equals(commandArray[0], "POMIN")) break;
                                else if (Objects.equals(commandArray[0], "RUCH:")) {
                                    try {
                                        board.doMove(number, Integer.parseInt(commandArray[1]), Integer.parseInt(commandArray[2]), Integer.parseInt(commandArray[3]), Integer.parseInt(commandArray[4]));
                                    } catch (IllegalMoveException exception) {
                                        out.println("POWTÓRZ");
                                    }
                                    out.println("AKCEPTACJA");
                                    if(isWinner()){
                                        announceLastWinner(name);
                                    }
                                    newTurn();
                                } else {
                                    out.println("POWTÓRZ");
                                }
                            }
                        }
                    }
                }
            }
            catch(IOException exception){
                exception.printStackTrace();
            }
        }
    }
}
