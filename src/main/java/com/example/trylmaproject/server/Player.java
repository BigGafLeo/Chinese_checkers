package com.example.trylmaproject.server;

import java.io.Serializable;

/**
 * Prosta klasa przechowująca informację o
 * numerze gracza, jego imieniu oraz o tym, czy wygrał
 * @author Mateusz Teplicki, Karol Dzwonkowski
 */
public class Player implements Serializable {
    public int number;
    public String name;
    public boolean IS_WINNER = false;

    public Player (int number, String name){
        this.number = number;
        this.name = name;
    }

    public Player (){

    }
}
