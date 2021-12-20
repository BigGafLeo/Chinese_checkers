package com.example.trylmaproject.server;

import javafx.scene.paint.Color;

import java.io.Serializable;

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
