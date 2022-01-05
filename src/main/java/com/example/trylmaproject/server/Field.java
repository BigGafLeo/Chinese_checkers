package com.example.trylmaproject.server;

import javafx.scene.paint.Color;

import java.io.Serializable;


/**
 * Klasa przechowująca informacje o pojedynczym polu na planszy
 * do gry w chińskie warcaby
 * @author Mateusz Teplicki
 */
public class Field implements Serializable {
    private int playerNumber;
    private Color color;
    private Color[] colorList = {Color.BLACK, Color.GREEN, Color.BLUE, Color.YELLOW, Color.RED, Color.PURPLE, Color.BROWN};

    public Color getColor() {
        return color;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    /**
     * Metoda przydzielająca do danego pola numer gracza wraz z kolorem
     * @param playerNumber
     */
    public void setPlayerNumber(int playerNumber){
        if(playerNumber > 7 || playerNumber < 0){
            this.playerNumber = 0;
        }
        else {
            this.playerNumber = playerNumber;
        }
        color = colorList[this.playerNumber];
    }
}
