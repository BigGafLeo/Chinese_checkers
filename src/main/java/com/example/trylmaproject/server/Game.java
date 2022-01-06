package com.example.trylmaproject.server;

import com.example.trylmaproject.exceptions.IllegalMoveException;
import com.example.trylmaproject.exceptions.IllegalNumberOfPlayers;

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
    private PlayerThread[] players = new PlayerThread[6];

    /**
     * Model do gry w chińskie warcaby (patrz: MVC)
     */
    private Board board;

    /**
     * Licznik graczy, którzy dołączyli do gry
     */
    private volatile int playerNumber = 0;
    private boolean GAME_STARTED = false;
    private boolean GAME_ENDED = false;

    /**
     * Zmienna wskazująca na gracza o danym numerze, który ma teraz ruch
     */
    private int whoseTurn;

    /**
     * Ile graczy jeszcze nie wygrało
     */
    private int howManyPlayersActive;

    public Game(int serverSocketNumber) throws IOException {
        serverSocket = new ServerSocket(serverSocketNumber);
    }

    @Override
    public void run() {
        ExecutorService threads = Executors.newFixedThreadPool(6);
        while(playerNumber<6 && !GAME_STARTED){
            playerNumber++;
            //Czekanie na graczy i dodawanie ich do tablicy
            try {
                threads.execute(players[playerNumber - 1] = new PlayerThread(serverSocket.accept(), playerNumber));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try{board = new Board(playerNumber);}
        catch (IllegalNumberOfPlayers i){
            i.printStackTrace();
        }
        whoseTurn = (int)(Math.random() * playerNumber + 1);
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

    /**
     * Metoda wyznaczjąca kolejnego gracza, który może wykonać ruch.
     * Nie może on być ani nullem, ani nie może być już zwyciezcą
     */
    void setPlayerTurn(){
        if(players[whoseTurn] != null && !players[whoseTurn].isWinner()){
            players[whoseTurn].setTurn();
        }
        else{
            whoseTurn = (whoseTurn + 1) % playerNumber;
            setPlayerTurn();
        }
    }

    /**
     * Budzenie wszystkich graczy po skończonej turze
     */
    void awakeAllPlayers(){
        for(PlayerThread thread : players){
            synchronized (thread){
                thread.notify();
            }
        }
    }

    public String getName(int playerNumber){
        return players[playerNumber].getName();
    }


    /**
     * Obliczanie, ile jeszcze graczy czeka na koniec gry
     * @return ile graczy jeszcze nie wygrało
     */
    int calculateHowManyPlayersActive(){
        int counter = 0;
        for(PlayerThread player : players){
            if(player != null && !player.isWinner()) counter++;
        }
        return counter;
    }

    /**
     *
     * @param name
     */
    void announceLastWinner(String name){
        for(PlayerThread player : players){
            if(player != null) player.lastWinner = name;
        }
    }



    class PlayerThread implements Runnable{

        private ObjectOutputStream oos;
        private final Socket socket;
        private int number;
        private PrintWriter out;
        private Scanner in;
        private String name;
        private String lastWinner;
        private boolean IS_YOUR_TURN = false;

        PlayerThread(Socket socket, int number){
            this.socket = socket;
            this.number = number;
        }

        public void setTurn(){
            IS_YOUR_TURN = true;
        }

        public String getName(){
            return name;
        }

        public boolean isWinner(){return board.isWinner(number);}

        public synchronized void waitForNewTurn(){
            //localTurn++;
            //IS_YOUR_TURN = false;
            //while(localTurn<turn){}
                try {
                    System.out.println("czekam");
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

        }

        @Override
        public void run() {
            try{
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);
                //oos = new ObjectOutputStream(socket.getOutputStream());
                out.println("NUMER: " + number);
                System.out.println("NUMER: " + number);
                do {
                    out.println("IMIE:");
                    System.out.println("IMIE:");
                    name = in.nextLine();
                    System.out.println(name);
                } while (name.isBlank());
                out.println("IMIĘ_ZAAKCEPTOWANE");
                String line;
                if(number == 1){
                    do{
                        System.out.println("nastart");
                        line = in.nextLine();
                        System.out.println(line);
                    }
                    while(line != "START");
                    System.out.println("START");
                    GAME_STARTED = true;
                }
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
