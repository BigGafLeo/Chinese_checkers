package com.example.trylmaprojectdatabase.server;

import com.example.trylmaproject.exceptions.IllegalMoveException;
import com.example.trylmaproject.exceptions.IllegalNumberOfPlayers;
import com.example.trylmaproject.server.Board;
import com.example.trylmaproject.server.Game;
import com.example.trylmaprojectdatabase.database.SpringJdbcConfig;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameDatabase extends Game {
    protected SpringJdbcConfig jdbc;
    protected int id_gry;

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
                while(playerNumber< board.MAX_PLAYERS && !GAME_STARTED){
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
            jdbc.addPlayer(id_gry, player.number, player.name);

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
