package com.example.trylmaprojectdatabase.server;

import com.example.trylmaproject.exceptions.IllegalMoveException;
import com.example.trylmaproject.exceptions.IllegalNumberOfPlayers;
import com.example.trylmaproject.server.Board;
import com.example.trylmaproject.server.Game;
import com.example.trylmaproject.server.Player;
import com.example.trylmaprojectdatabase.database.SpringJdbcConfig;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameDatabase extends Game {
    private int typeOfGame = 0;
    public static final int NEW_GAME = 1;
    public static final int LOADED_GAME = 2;
    protected SpringJdbcConfig jdbc;
    protected int id_gry;
    private int maxPlayersInLoadedGame;

    /**
     * @param serverSocketNumber port, na którym będzie działać serwer
     * @throws IOException jęsli pojawi się bład I/0
     */
    public GameDatabase(int serverSocketNumber) throws IOException {
        super(serverSocketNumber);
    }



    @Override
    public void run() {
        jdbc = new SpringJdbcConfig();
        id_gry = jdbc.createGame();

        ExecutorService playerCreatorThread = Executors.newFixedThreadPool(1);
        playerCreatorThread.execute(new Game.PlayerCreator(){
            @Override
            public void run() {
                ExecutorService threads = Executors.newFixedThreadPool(6);
                while(playerNumber< Board.MAX_PLAYERS && !GAME_STARTED){
                    //Czekanie na graczy i dodawanie ich do tablicy
                    try {
                        threads.execute(players[playerNumber] = new PlayerThreadDB(serverSocket.accept(), ++playerNumber));
                        if(GAME_STARTED) playerNumber--;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

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


    class PlayerThreadDB extends Game.PlayerThread{
        PlayerThreadDB(Socket socket, int number){
            super(socket,number);
        }

        @Override
        public void run() {
            //Jeśli gra się zaczęła, nie pozwalaj na start kolejnych wątków
            if(GAME_STARTED) {
                IS_ACTIVE = false;
                return;
            }

            if(localNumber > 1){
                while(typeOfGame == 0){
                    synchronized (this){
                        try {
                            wait(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


            if(typeOfGame == LOADED_GAME && localNumber > maxPlayersInLoadedGame){
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

        private boolean isAbleToStart(){
            if(typeOfGame == LOADED_GAME){
                if(playerNumber >= maxPlayersInLoadedGame) return true;
                else return false;
            }
            if(playerNumber < 2  || playerNumber == 5  || playerNumber > 6) return false;
            return true;
        }

        public void prepareForGame(Scanner in, ObjectOutputStream oos) throws IOException{
            //Wyślij numer gracza klientowi
            String line = "NUMER: " + player.number;
            oos.writeObject(line);

            String message;
            if(player.number == 1){
                oos.writeObject(jdbc.getTable("SELECT id_gry, liczba_ruchów FROM gra"));//tablica id_gry, liczba_ruchów
                do{
                    message = in.nextLine();
                } while(message == null);
                if(message.equals("WCZYTAJ_GRĘ")){
                    typeOfGame = LOADED_GAME;
                    var id = in.nextLine();
                    var moveNumber = in.nextLine();
                    try {
                        board = jdbc.getSavedBoard(Integer.parseInt(id), Integer.parseInt(moveNumber));
                    } catch (IllegalNumberOfPlayers e) {
                        e.printStackTrace();
                    }
                    maxPlayersInLoadedGame = board.getNumberOfPlayers();
                }
                else{
                    typeOfGame = NEW_GAME;
                }

            }

            //TODO obsługa niepopranych imion w kliencie
            //Pobierz imię od gracza
            do {
                oos.writeObject("IMIE:");
                player.name = in.nextLine();
            } while (player.name.isBlank());
            jdbc.addPlayer(id_gry, player.number, player.name);

            //Czekaj, aż gracz numer jeden da poprawny sygnał do startu
            if(player.number == 1){
                while(true){
                    line = in.nextLine();
                    //Jeśli komunikat to nie start albo liczba graczy jest niepoprawna
                    //(inna niż 2, 3, 4 lub 6), zwróć klientowi sygnał "JESZCZE_RAZ"
                    if(!line.equals("START") || !isAbleToStart()){
                        oos.writeObject("JESZCZE_RAZ");
                    }
                    else{
                        //Zaaakceptuj ruch i wyjdź z pętli
                        oos.writeObject("AKCEPTACJA");
                        break;
                    }
                }
                jdbc.startGame(id_gry);
                GAME_STARTED = true;

                //Tworzenie nowej planszy
                try{board = new Board(playerNumber);}
                catch (IllegalNumberOfPlayers i){
                    i.printStackTrace();
                }
                //Obudź administratora, który jest zablokowany wait() w metodzie run()
                synchronized(GameDatabase.this){
                    GameDatabase.this.notify();
                }
            }
        }

        @Override
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
                                jdbc.addMove(id_gry, player.number, commandArray);
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
    }


}
