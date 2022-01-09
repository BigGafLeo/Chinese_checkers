package com.example.trylmaproject.server;

import com.example.trylmaproject.exceptions.IllegalMoveException;
import com.example.trylmaproject.exceptions.IllegalNumberOfPlayers;

/**
 * Klasa implementująca model tablicy do chińskich warcabów
 * @author Mateusz Teplicki, Karol Dzwonkowski
 * @see Game
 */
public class Board {

    /**
     * Wymiary schematycznej tablicy do gry
     */
    public final int W = 25, H = 17;
    public final int MAX_PLAYERS = 6, STARTING_POINTS = 10;
    public final int[] DIMENSION = {12, 11, 10, 9, 0, 1, 2, 3, 4, 3, 2, 1, 0, 9, 10, 11, 12};

    /**
     * Ile graczy dołączyło do gry
     */
    private final int numberOfPlayers;

    /**
     * Pole, które wskazuje na blokadę wykonywania ruchów - można jedynie tym pionkiem się ruszać,
     * jeśli nie jest nullem. Blokada zakładana jest, gdy pionek wykona przeskok nad innym pionkiem. Wtedy nie można już
     * operować innymi pionkami, bo jest to niezgodne z zasadami gry
     */
    private Pawn movablePawn = null;

    private boolean isAbleToDoSecondMove = true;
    /**
     * Dwuwymiarowa tablica do gry o wymiarach 25 na 17, gdzie pola są rozstawione po
     * przekątnych.
     * @see <a href="https://github.com/HarryZalessky/Chinese-Checkers/blob/master/docs/fig5.png">github.com/HarryZalessky/Chinese-Checkers</a>
     */
    private final Field[][] board = new Field[H][W];

    /**
     * 6-elementowa lista pól startowych graczy
     */
    private final Field[][] startingFields = new Field[MAX_PLAYERS][STARTING_POINTS];

    /**
     * Lista wskazująca, gdzie dany gracz o adresie w tablicy [player - 1] zaczynał grę
     */
    private final int[] playerStartingField = new int[MAX_PLAYERS];

    /**
     * Zwraca przechowywaną tablicę
     * @return przechowywana tablica {@link Field}
     */
    public Field[][] getBoard() {
        return board;
    }

    public void doMove(int player, String[] commandArray) throws IllegalMoveException{
        doMove(player, Integer.parseInt(commandArray[1]), Integer.parseInt(commandArray[2]), Integer.parseInt(commandArray[3]), Integer.parseInt(commandArray[4]));
    }

    /**
     * Metoda pozwalająca na wykonanie ruchu na planszy
     * zgodnie z zasadami gry w chińskie warcaby. Więcej informacji o zasadach na stronie na dole
     * @param player numer gracza, który wykonuje ruch
     * @param startX współrzędna x przesuwanego pionka na tablicy
     * @param startY współrzędna y przesuwanego pionka na tablicy
     * @param endX   współrzędna x końcowej pozycji pionka na tablicy
     * @param endY   współrzędna y końcowej pozycji pionka na tablicy
     * @throws IllegalMoveException
     * @see <a href="https://www.wikihow.com/Play-Chinese-Checkers"">www.wikihow.com/Play-Chinese-Checkers</a>
     */
    public void doMove(int player, int startX, int startY, int endX, int endY) throws IllegalMoveException {


        if (board[startY][startX] != null && board[endY][endX] != null && isAbleToDoSecondMove){
            if(board[startY][startX].getPlayerNumber() == player && board[endY][endX].getPlayerNumber() == 0){
                if(isInEndingField(player, startX, startY) && !isInEndingField(player, endX, endY)){
                    throw new IllegalMoveException();
                }
                else{
                    int diffX = Math.abs(startX - endX);
                    int diffY = Math.abs(startY - endY);
                    if(diffX == 1 && diffY == 1 && movablePawn == null){
                        board[endY][endX].setPawn(board[startY][startX].getPawn());
                        board[startY][startX].setPawn(null);
                        isAbleToDoSecondMove = false;
                    }
                    else if((diffX == 2 && diffY == 2) || (diffX == 4 && diffY == 0)){
                        int middleX = (startX + endX) / 2;
                        int middleY = (startY + endY) / 2;
                        if(board[middleY][middleX].getPlayerNumber() != 0 && isAbleToMovePawn(board[startY][startX].getPawn())){
                            board[endY][endX].setPawn(board[startY][startX].getPawn());
                            board[startY][startX].setPawn(null);
                            setMovablePawn(board[endY][endX].getPawn());
                            isAbleToDoSecondMove = true;
                        }
                        else throw new IllegalMoveException();
                    }
                    else throw new IllegalMoveException();
                }
            }
            else throw new IllegalMoveException();
        }
        else throw new IllegalMoveException();
        setMovablePawn(board[endY][endX].getPawn());
    }

    /**
     * Sprawdza, czy
     * @param player
     * @param posx
     * @param posy
     * @return
     */
    private boolean isInEndingField(int player, int posx, int posy){
        int playerEndingField = (playerStartingField[player-1] + (MAX_PLAYERS / 2)) % MAX_PLAYERS;
        for(Field f : startingFields[playerEndingField]){
            if(f == board[posy][posx]){
                return true;
            }
        }
        return false;
    }

    /**
     * Sprawdza, czy na innym pionku nie ma blokady ruchu
     * @param pawn sprawdzany pionek
     * @return czy na innym pionku nie ma blokady ruchu
     */
    public boolean isAbleToMovePawn(Pawn pawn){
        if(movablePawn == null) return true;
        return movablePawn.equals(pawn);
    }

    /**
     * Ustawia blokadę na pionku, który
     * przeskakuje o dwa pola
     * @param pawn pionek przeskakujący dwa pola
     */
    public void setMovablePawn(Pawn pawn){
        movablePawn = pawn;
    }

    /**
     * Usuwa blokady na pionku. Używaj za każdym razem, gdy
     * gracz kończy swoją turę!
     * @see #isAbleToDoSecondMove
     * @see #movablePawn
     */
    public void resetMoveablePawn(){
        isAbleToDoSecondMove = true;
        movablePawn = null;
    }

    /**
     * Metoda sprawdzająca, czy gracz o podanym numerze już wygrał
     * @param player - id gracza
     * @return czy już wygrał
     */
    public boolean isWinner(int player) {
        if(player > numberOfPlayers || player <= 0) return false;
        int playerEndingField = (playerStartingField[player-1] + (MAX_PLAYERS / 2)) % MAX_PLAYERS;
        for(Field f : startingFields[playerEndingField]){
            if(f.getPlayerNumber() != player) return false;
        }
        return true;
    }

    /**
     * Dodaje do tablicy {@link #startingFields} pola, w który gracze zaczynają
     * i kończą grę. Na tablicy {@link #board} ma on ksztalt piramidy
     * @param number numer pola, zgodnie z konwencję zaczyna się on na godzinie ósmej w tablicy
     * i idzie przeciwnie do ruchu wskazówek zegara
     * @param upperFieldX pozycja x na tablicy {@link #board} szczytu tej piramidy
     * @param upperFieldY pozycja y na tablicy {@link #board} szczytu tej piramidy
     * @param isASC czy piramida się rozszerza wraz ze wzrostem parametru Y
     */
    private void createStartingFields(int number, int upperFieldX, int upperFieldY, boolean isASC) {

        int counter = 0;
        for (int i = 0; i <= 3; i++) {
            for (int w = upperFieldX - i; w <= upperFieldX + i; w += 2) {
                if (isASC) startingFields[number][counter] = board[upperFieldY + i][w];
                else startingFields[number][counter] = board[upperFieldY - i][w];
                counter++;
            }
        }
    }

    /**
     * Konstruktor tworzący nowy model planszy do gry w chińskie warcaby
     * W grę może grać jedynie 2, 3, 4 lub 6 graczy
     * @param numberOfPlayers liczba graczy, którzy przystąpili do gry
     * @throws IllegalNumberOfPlayers liczbq graczy jest nieprawidłowa
     */
    //TODO Zmienić z public Board na private (potrzebne do testów)
    public Board(int numberOfPlayers) throws IllegalNumberOfPlayers {

        this.numberOfPlayers = numberOfPlayers;

        //Tworzenie modelu tablicy, jeszcze bez graczy
        for (int h = 0; h < H; h++) {
            int localDimension = DIMENSION[h];
            for (int w = localDimension; w <= W - localDimension; w += 2) {
                board[h][w] = new Field();
            }
        }

        //Tworzenie listy początkowych pól, gdzie gracze zaczynają i kończą grę
        createStartingFields(0, 3, 9, true);
        createStartingFields(2, 21, 9, true);
        createStartingFields(4, 12, 0, true);
        createStartingFields(1, 12, 16, false);
        createStartingFields(3, 21, 7, false);
        createStartingFields(5, 3, 7, false);


        //W zależności ile graczy dołączyło się do gry, połącz ich z polami startowymi na tablicy
        switch (numberOfPlayers) {
            case 2 -> {
                for (Field f : startingFields[1]) f.setPlayerNumber(1);
                playerStartingField[1 - 1] = 1;
                for (Field f : startingFields[4]) f.setPlayerNumber(2);
                playerStartingField[2 - 1] = 4;
            }
            case 3 -> {
                for (Field f : startingFields[1]) f.setPlayerNumber(1);
                playerStartingField[1 - 1] = 1;
                for (Field f : startingFields[3]) f.setPlayerNumber(2);
                playerStartingField[2 - 1] = 3;
                for (Field f : startingFields[5]) f.setPlayerNumber(3);
                playerStartingField[3 - 1] = 5;
            }
            case 4 -> {
                for (Field f : startingFields[0]) f.setPlayerNumber(1);
                playerStartingField[1 - 1] = 0;
                for (Field f : startingFields[2]) f.setPlayerNumber(2);
                playerStartingField[2 - 1] = 2;
                for (Field f : startingFields[3]) f.setPlayerNumber(3);
                playerStartingField[3 - 1] = 3;
                for (Field f : startingFields[5]) f.setPlayerNumber(4);
                playerStartingField[4 - 1] = 5;
            }
            case 6 -> {
                for (Field f : startingFields[1]) f.setPlayerNumber(1);
                playerStartingField[1 - 1] = 1;
                for (Field f : startingFields[2]) f.setPlayerNumber(2);
                playerStartingField[2 - 1] = 2;
                for (Field f : startingFields[3]) f.setPlayerNumber(3);
                playerStartingField[3 - 1] = 3;
                for (Field f : startingFields[4]) f.setPlayerNumber(4);
                playerStartingField[4 - 1] = 4;
                for (Field f : startingFields[5]) f.setPlayerNumber(5);
                playerStartingField[5 - 1] = 5;
                for (Field f : startingFields[0]) f.setPlayerNumber(6);
                playerStartingField[6 - 1] = 0;
            }
            default -> throw new IllegalNumberOfPlayers();
        }
    }
}

