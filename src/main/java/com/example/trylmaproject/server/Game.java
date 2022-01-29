package com.example.trylmaproject.server;

import com.example.trylmaproject.exceptions.IllegalMoveException;
import com.example.trylmaproject.exceptions.IllegalNumberOfPlayers;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Kontroler gry do chińskich warcabów
 * @author Mateusz Teplicki, Karol Dzwonkowski
 * @see com.example.trylmaproject.client.Client
 */
public class Game implements Runnable{

    protected final ServerSocket serverSocket;

    /**
     * Tablica wątków obsługujących poszczególnych graczy
     */
    protected final PlayerThread[] players = new PlayerThread[Board.MAX_PLAYERS];

    /**
     * Model do gry w chińskie warcaby (patrz: MVC)
     */
    protected Board board;

    /**
     * Licznik graczy, którzy dołączyli do gry
     */
    protected volatile int playerNumber = 0;

    protected boolean GAME_STARTED = false;

    protected boolean GAME_ENDED = false;

    /**
     * Zmienna wskazująca na gracza o danym numerze, który ma teraz ruch
     */
    protected int whoseTurn = 0;

    /**
     * Ilu graczy jeszcze nie wygrało
     */
    private int howManyPlayersActive;

    //-------------------------------------------------------------------------------------------//

    /**
     *
     * @param serverSocketNumber port, na którym będzie działać serwer
     * @throws IOException jęsli pojawi się bład I/0
     */
    public Game(int serverSocketNumber) throws IOException {
        serverSocket = new ServerSocket(serverSocketNumber);
    }

    //-------------------------------------------------------------------------------------------//

    /**
     * Obsługa nowego wątku. Uruchamia klasę {@link PlayerCreator} odpowiedzialną
     * za dodawanie graczy, a na koniec losuję id pierwszego gracza, który rozpocznie grę,
     * i startuję grę {@link #runGame()}
     */
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

    /**
     * Odpowiada za przebieg kolejki. Uruchamia metody {@link #newTurn()}, która ustala turę gracza.
     * Następnie budzi wszystkich graczy metodą {@link #awakeAllPlayers()} i czeka na to, aż gracz skończy swoją turę
     * blokiem
     * <pre>
     * {@code
     *  synchronized (this){
     *       try {
     *           wait();
     *       } catch (InterruptedException e) {
     *           e.printStackTrace();
     *       }
     *  }
     * }
     * </pre>
     * @see PlayerThread#endTurn()
     */
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
            whoseTurn = (whoseTurn + 1) % playerNumber;
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
                if(thread != null && !thread.isWinner() && thread.IS_ACTIVE){
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
    protected void announceLastWinner(String name){
        for(PlayerThread player : players){
            if(player != null && player.IS_ACTIVE) player.lastWinner = name;
        }
    }

    //-------------------------------------------------------------------------------------------//

    /**
     * Klasa, która obsługuje dodawanie graczy w nowym wątku
     * @author Mateusz Teplicki, Karol Dzwonkowski
     */
    protected class PlayerCreator implements Runnable{

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

    //-------------------------------------------------------------------------------------------//

    protected class PlayerThread implements Runnable{

        protected final Socket socket;
        protected String lastWinner = "";
        protected int localNumber;
        public boolean IS_ACTIVE = true;
        protected boolean IS_YOUR_TURN = false;
        private boolean IS_WAITING = false;
        protected Player player;

        //-------------------------------------------------------------------------------------------//

        /**
         * Tworzy nowy wątek do obsługi klienta
         * @param socket socket do komunikacji z klientem
         * @param number numer gracza przydzielony przez serwer
         */
        protected PlayerThread(Socket socket, int number){
            this.socket = socket;
            localNumber = number;
        }

        //-------------------------------------------------------------------------------------------//

        /**
         * Ustawia flagę {@link #IS_YOUR_TURN} na <code>true</code>
         * Używaj za każdym razem, gdy zaczyna się twoja kolejka
         */
        public void setTurn(){
            IS_YOUR_TURN = true;
        }

        public String getName(){
            return player.name;
        }

        public Player getPlayer(){
            return player;
        }

        /**
         *
         * @return czy dany gracz już jest zwycięzcą
         */
        public boolean isWinner(){return board.isWinner(player.number);}


        /**
         * Wątek czeka, aż nie rozpocznie się kolejna kolejka.
         * Notify() znajduję się w metodzie {@link #awakeAllPlayers()}
         */
        public synchronized void waitForNewTurn(){
            try {
                IS_WAITING = true;
                wait();
                IS_WAITING = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * Metoda, która resetuje board, i powiadamia administratora o tym, że
         * może wyznaczyć kolejnego gracza
         * Trzeba zawsze używać tego po skończonej turze
         */
        public void endTurn(){
            board.resetMoveablePawn();
            synchronized(Game.this){
                Game.this.notify();
            }
            IS_YOUR_TURN = false;
        }

        /**
         * Początkowa komunikacja pomiędzy klientem a serwerem
         * @param in - wejście komunikacji
         * @param oos - strumień wyjścia komunikacji
         * @throws IOException
         */
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

                //Tworzenie nowej planszy
                try{board = new Board(playerNumber);}
                catch (IllegalNumberOfPlayers i){
                    i.printStackTrace();
                }
                //Obudź administratora, który jest zablokowany wait() w metodzie run()
                synchronized(Game.this){
                    Game.this.notify();
                }
            }
        }

        /**
         * Metoda typu boolowskiego, która pozwala na wykonanie tury
         * w komunikacji klient serwer
         * @param in - wejście komunikacji
         * @param oos - strumień wyjścia komunikacji
         * @return czy gracz w tej turze zakończył swoją grę
         * @throws IOException
         */
        protected boolean makeTurn(Scanner in, ObjectOutputStream oos) throws IOException{

                //Wyślij klientowi tablicę Field[][] do wyrysowania i wyczyść pamięć
                //podręczną
                oos.writeObject(board.getBoard());
                oos.writeObject(getAllPlayers());
                oos.reset();
                if(GAME_ENDED){
                    oos.writeObject("KONIEC_GRY: " + lastWinner);
                    return true;
                }
                else{
                    oos.writeObject("GRA");
                }
                var line = "ZWYCIEZCA: " + lastWinner;
                if(!Objects.equals(lastWinner, "")) oos.writeObject(line);
                else{
                    oos.writeObject("BRAK_ZWYCIEZCY");
                }
                lastWinner = "";

                if(IS_YOUR_TURN){
                    oos.writeObject("KOLEJKA: TAK");
                    synchronized (board) {
                        while(true){
                            //Pobiera ruch od klienta
                            String[] commandArray = in.nextLine().split(" ");
                            if (Objects.equals(commandArray[0], "POMIN")) break;
                            else if (Objects.equals(commandArray[0], "RUCH:")) {
                                //RUCH: [POZYCJAX_POCZ] [POZYCJAY_POCZ] [POZYCJAX_KONC], [POZYCJAY_KONC]
                                try {
                                    board.doMove(player.number, commandArray);
                                    oos.writeObject("AKCEPTACJA");
                                    oos.writeObject(board.getBoard());
                                    oos.reset();
                                } catch (IllegalMoveException exception) {
                                    oos.writeObject("POWTÓRZ");
                                    oos.writeObject(board.getBoard());
                                    oos.reset();
                                }
                                if(isWinner()){
                                    announceLastWinner(player.name);
                                    player.IS_WINNER = true;
                                    endTurn();
                                    return true;
                                }
                            } else {
                                oos.writeObject("POWTÓRZ - coś nie tak z komendą");
                            }
                        }
                        endTurn();
                        return false;
                    }
                }
                else {
                    oos.writeObject("KOLEJKA: NIE");
                    return false;
                }
        }

        //-------------------------------------------------------------------------------------------//

        /**
         * Metoda, która jest odpowiedzialna za przebieg tury i
         * komunikację z danym graczem.
         * Więcej informacji o funkcjonowaniu metody znajduje się w
         * wewnętrznych komentarzach w kodzie
         */
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
                    waitForNewTurn();
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
