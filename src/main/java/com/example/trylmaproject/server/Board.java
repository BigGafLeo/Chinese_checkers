package com.example.trylmaproject.server;

import com.example.trylmaproject.exceptions.IllegalMoveException;

import java.io.Serializable;

/**
 * Klasa implementująca tablicę do chińskich warcabów
 * @author Mateusz Teplicki
 */
public class Board{

    public final int W = 25, H = 17;
    public final int[] DIMENSION = {12,11,10,9,0,1,2,3,4,3,2,1,0,9,10,11,12};

    /**
     * Dwuwymiarowa tablica do gry o wymiarach 25 na 17, gdzie pola są rozstawione po
     * przekątnych.
     */
    private Field[][] board = new Field[H][W];

    public Field[][] getBoard(){
            return board;
    }

    /**
     * Metoda pozwalająca na wykonanie ruchu na planszy
     * @param player numer gracza, który wykonuje ruch
     * @param startX współrzędna x przesuwanego pionka na tablicy
     * @param startY współrzędna y przesuwanego pionka na tablicy
     * @param endX współrzędna x końcowej pozycji pionka na tablicy
     * @param endY współrzędna y końcowej pozycji pionka na tablicy
     * @throws IllegalMoveException
     */
    public void doMove(int player, int startX, int startY, int endX, int endY) throws IllegalMoveException{
        //TODO
    }

    public boolean isWinner(int player){
        //TODO
        return false;
    }

    Board(int numberOfPlayers){
        //TODO
    }
}

