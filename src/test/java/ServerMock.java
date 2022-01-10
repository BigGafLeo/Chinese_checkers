import com.example.trylmaproject.exceptions.IllegalMoveException;
import com.example.trylmaproject.exceptions.IllegalNumberOfPlayers;
import com.example.trylmaproject.server.Board;
import com.example.trylmaproject.server.Game;
import com.example.trylmaproject.server.Player;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ServerMock implements Runnable{

    private final ServerSocket serverSocket;
    private final PlayerThread[] players = new PlayerThread[Board.MAX_PLAYERS];
    private Board board;
    private volatile int playerNumber = 0;
    private boolean GAME_STARTED = false;
    private boolean GAME_ENDED = false;
    private int whoseTurn = 0;
    private int howManyPlayersActive;


    public ServerMock(int serverSocketNumber) throws IOException {
        serverSocket = new ServerSocket(serverSocketNumber);
    }


    @Override
    public void run() {
        ExecutorService playerCreatorThread = Executors.newFixedThreadPool(1);
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

    void setPlayerTurn(){
        if(players[whoseTurn] != null && !players[whoseTurn].isWinner() && players[whoseTurn].IS_ACTIVE){
            players[whoseTurn].setTurn();
            whoseTurn = (whoseTurn + 1) % playerNumber;
        }
        else{
            whoseTurn = (whoseTurn + 1) % playerNumber;
            setPlayerTurn();
        }
    }

    void awakeAllPlayers(){
        boolean IS_ABLE_TO_START;
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
        if(players[playerNumber] != null) return players[playerNumber].getName();
        return null;
    }

    public Player[] getAllPlayers(){
        Player[] playerList = new Player[6];
        for(int i = 0; i < Board.MAX_PLAYERS; i++){
            if(players[i] != null && players[i].getPlayer() != null){
                playerList[i] = players[i].getPlayer();
            }
            else{
                playerList[i] = null;
            }
        }
        return playerList;
    }

    int calculateHowManyPlayersActive(){
        int counter = 0;
        for(PlayerThread player : players){
            if(player != null && !player.isWinner() && player.IS_ACTIVE) counter++;
        }
        return counter;
    }

    void announceLastWinner(String name){
        for(PlayerThread player : players){
            if(player != null && player.IS_ACTIVE) player.lastWinner = name;
        }
    }

    class PlayerCreator implements Runnable{

        @Override
        public void run() {
            ExecutorService threads = Executors.newFixedThreadPool(6);
            while(playerNumber< board.MAX_PLAYERS && !GAME_STARTED){
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

        private final Socket socket;
        private String lastWinner;
        private int localNumber;
        public boolean IS_ACTIVE = true;
        private boolean IS_YOUR_TURN = false;
        private boolean IS_WAITING = false;
        private Player player;


        PlayerThread(Socket socket, int number){
            this.socket = socket;
            localNumber = number;
        }


        public void setTurn(){
            IS_YOUR_TURN = true;
        }

        public String getName(){
            return player.name;
        }

        public Player getPlayer(){
            return player;
        }


        public boolean isWinner(){return board.isWinner(player.number);}

        public synchronized void waitForNewTurn(){
            try {
                IS_WAITING = true;
                wait();
                IS_WAITING = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void endTurn(){
            board.resetMoveablePawn();
            synchronized(ServerMock.this){
                ServerMock.this.notify();
            }
            IS_YOUR_TURN = false;
        }

        public void prepareForGame(Scanner in, ObjectOutputStream oos) throws IOException{
            //Wyślij numer gracza klientowi
            String line = "NUMER: " + player.number;
            oos.writeObject(line);

            //TODO obsługa niepopranych imion w kliencie
            //Pobierz imię od gracza
            do {
                oos.writeObject("IMIE:");
                player.name = in.nextLine();
            } while (player.name.isBlank());

            //Czekaj, aż gracz numer jeden da poprawny sygnał do startu
            if(player.number == 1){
                while(true){
                    line = in.nextLine();
                    //Jeśli komunikat to nie start, albo liczba graczy jest niepoprawna
                    //(inna niż 2, 3, 4 lub 6), zwróć klientowi sygnał "JESZCZE_RAZ"
                    if(!line.equals("START") || playerNumber < 2  || playerNumber == 5  || playerNumber > 6 ){
                        oos.writeObject("JESZCZE_RAZ");
                    }
                    else{
                        //Zaaakceptuj ruch i wyjdź z pętli
                        oos.writeObject("AKCEPTACJA");
                        break;
                    }
                }
                GAME_STARTED = true;

                try{board = new Board(playerNumber);}
                catch (IllegalNumberOfPlayers i){
                    i.printStackTrace();
                }
                synchronized(ServerMock.this){
                    ServerMock.this.notify();
                }
            }
        }

        private boolean makeTurn(Scanner in, ObjectOutputStream oos) throws IOException{
            waitForNewTurn();
            oos.writeObject(board.getBoard());
            oos.writeObject(getAllPlayers());
            oos.reset();
            oos.writeObject("KONIEC_GRY: " + "mateusz");
            return true;
        }

        @Override
        public void run() {
            //Jeśli gra się zaczęła, nie pozwalaj na start kolejnych wątków
            if(GAME_STARTED) {
                IS_ACTIVE = false;
                return;
            }
            try{
                player = new Player();
                player.number = localNumber;
                Scanner in = new Scanner(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                prepareForGame(in, oos);
                while(true){
                    if(makeTurn(in, oos)){
                        break;
                    }
                }

            }
            catch(IOException exception){
                exception.printStackTrace();
            }
        }
    }
}

