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
    private int whoseTurn = 0;

    /**
     * Ile graczy jeszcze nie wygrało
     */
    private int howManyPlayersActive;

    private Thread playerCreatorWorker;

    private ExecutorService playerCreatorThread;

    public Game(int serverSocketNumber) throws IOException {
        serverSocket = new ServerSocket(serverSocketNumber);
    }

    @Override
    public void run() {
        playerCreatorThread = Executors.newFixedThreadPool(1);
        playerCreatorThread.execute(new PlayerCreator());
        while(true){
            synchronized (this){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(GAME_STARTED){
                whoseTurn = (int)(Math.floor(Math.random() * (playerNumber)));
                System.out.println("whoseturn = " + whoseTurn);
                runGame();
            }
        }
    }

    public void runGame(){
        while(true){
            newTurn();
            awakeAllPlayers();
            synchronized (this){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
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
        }
    }

    /**
     * Metoda wyznaczjąca kolejnego gracza, który może wykonać ruch.
     * Nie może on być ani nullem, ani nie może być już zwyciezcą
     */
    void setPlayerTurn(){
        if(players[whoseTurn] != null && !players[whoseTurn].isWinner() && players[whoseTurn].IS_ACTIVE){
            players[whoseTurn].setTurn();
            System.out.println("whoseturnset = " + whoseTurn);
            whoseTurn = (whoseTurn + 1) % playerNumber;
        }
        else{
            whoseTurn = (whoseTurn + 1) % playerNumber;
            System.out.println("whoseturnNOTset = " + whoseTurn);
            setPlayerTurn();
        }
    }

    /**
     * Budzenie wszystkich graczy po skończonej turze
     */
    void awakeAllPlayers(){
        boolean IS_ABLE_TO_START = false;
        do{
            synchronized (this){
                try {
                    wait(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            IS_ABLE_TO_START = true;
            for(PlayerThread thread : players){
                if(thread != null && thread.IS_ACTIVE){
                    if(!thread.IS_WAITING) {
                        IS_ABLE_TO_START = false;
                        break;
                    }
                }
            }
        }
        while(!IS_ABLE_TO_START);

        for(PlayerThread thread : players){
            if(thread != null && thread.IS_ACTIVE){
                synchronized (thread){
                    thread.notify();
                }
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
            if(player != null && !player.isWinner() && player.IS_ACTIVE) counter++;
        }
        return counter;
    }

    /**
     *
     * @param name
     */
    void announceLastWinner(String name){
        for(PlayerThread player : players){
            if(player != null && player.IS_ACTIVE) player.lastWinner = name;
        }
    }

    class PlayerCreator implements Runnable{

        @Override
        public void run() {
            ExecutorService threads = Executors.newFixedThreadPool(6);
            while(playerNumber<6 && !GAME_STARTED){
                //Czekanie na graczy i dodawanie ich do tablicy
                try {
                    threads.execute(players[playerNumber] = new PlayerThread(serverSocket.accept(), ++playerNumber));
                    if(GAME_STARTED) playerNumber--;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
        private boolean IS_ACTIVE = true;
        private boolean IS_WAITING = false;

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
            try {
                System.out.println("czekam");
                IS_WAITING = true;
                wait();
                IS_WAITING = false;

                System.out.println("zaczynam");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            if(GAME_STARTED) {
                IS_ACTIVE = false;
                return;
            }
            try{
                in = new Scanner(socket.getInputStream());
                oos = new ObjectOutputStream(socket.getOutputStream());
                String helper = "NUMER: " + number;
                oos.writeObject(helper);
                System.out.println("NUMER: " + number);
                do {
                    oos.writeObject("IMIE:");
                    System.out.println("IMIE:");
                    name = in.nextLine();
                    System.out.println(name);
                } while (name.isBlank());
                String line;
                if(number == 1){
                    while(true){
                        System.out.println("nastart");
                        line = in.nextLine();
                        if(!line.equals("START") || playerNumber < 2  || playerNumber == 5  || playerNumber > 6 ){
                            oos.writeObject("JESZCZE_RAZ");
                        }
                        else{
                            oos.writeObject("AKCEPTACJA");
                            break;
                        }
                    }
                    System.out.println("START2");
                    GAME_STARTED = true;

                    try{board = new Board(playerNumber);}
                    catch (IllegalNumberOfPlayers i){
                        i.printStackTrace();
                    }
                    synchronized(Game.this){
                        Game.this.notify();
                    }
                }
                while(true){
                    waitForNewTurn();
                    oos.writeObject(board.getBoard());
                    oos.reset();
                    if(GAME_ENDED){
                        oos.writeObject("KONIEC_GRY: " + lastWinner);
                        break;
                    }
                    else{
                        oos.writeObject("GRA");
                    }
                    helper = "ZWYCIEZCA: " + lastWinner;
                    if(!Objects.equals(lastWinner, "")) oos.writeObject(helper);
                    else{
                        oos.writeObject("BRAK_ZWYCIEZCY");
                    }
                    lastWinner = "";

                    if(IS_YOUR_TURN){
                        oos.writeObject("KOLEJKA: TAK");
                        synchronized (board) {
                            while(true){
                                System.out.println("Doszliśmy aż tutaj xd " + playerNumber);
                                String[] commandArray = in.nextLine().split(" ");
                                System.out.println("ale tutaj już nie dx " + playerNumber);
                                if (Objects.equals(commandArray[0], "POMIN")) break;
                                else if (Objects.equals(commandArray[0], "RUCH:")) {
                                    //RUCH: [POZYCJAX_POCZ] [POZYCJAY_POCZ] [POZYCJAX_KONC], [POZYCJAY_KONC]
                                    try {
                                        board.doMove(number, Integer.parseInt(commandArray[1]), Integer.parseInt(commandArray[2]), Integer.parseInt(commandArray[3]), Integer.parseInt(commandArray[4]));
                                        oos.writeObject("AKCEPTACJA");
                                        oos.writeObject(board.getBoard());
                                    } catch (IllegalMoveException exception) {
                                        oos.writeObject("POWTÓRZ");
                                        oos.reset();
                                    }
                                    if(isWinner()){
                                        announceLastWinner(name);
                                        break;
                                    }
                                } else {
                                    oos.writeObject("POWTÓRZ");
                                }
                            }
                            board.deleteMovablePawn();
                            synchronized(Game.this){
                                Game.this.notify();
                            }
                            IS_YOUR_TURN = false;
                        }
                    }
                    else oos.writeObject("KOLEJKA: NIE");
                }
            }
            catch(IOException exception){
                exception.printStackTrace();
            }
        }
    }
}
