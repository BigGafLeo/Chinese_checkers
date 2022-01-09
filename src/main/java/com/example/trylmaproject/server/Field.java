package com.example.trylmaproject.server;

import java.awt.geom.Ellipse2D;
import java.io.Serializable;


/**
 * Klasa przechowująca informacje o pojedynczym polu na planszy
 * do gry w chińskie warcaby
 * @author Mateusz Teplicki
 */
public class Field implements Serializable {

    /**
     * Który gracz okupuje dane pole w grze
     */
    private Pawn pawn = null;

    /**
     * Kółko, które będzie rysowane w GUI
     */
    private Ellipse2D.Double circle;
    public static final int DEFAULT_RADIUS = 40;

    /**
     * Zwaraca numer gracza, który okupuje dane stanowisko
     * @return numer gracza
     */
    public int getPlayerNumber() {
        if(pawn == null) return 0;
        return pawn.getPlayerNumber();
    }

    /**
     * Metoda przydzielająca do danego pola numer gracza
     * @param playerNumber
     */
    public void setPlayerNumber(int playerNumber){
        pawn = new Pawn(playerNumber);
    }

    public void setPawn(Pawn pawn){
        this.pawn=pawn;
    }

    public Pawn getPawn(){
        return pawn;
    }

    public Ellipse2D.Double fieldDrawing(double posy, double posx)
    {
        circle = new Ellipse2D.Double(posx,posy,DEFAULT_RADIUS,DEFAULT_RADIUS);
        return circle;
    }
    public Ellipse2D.Double getCircle()
    {
        return circle;
    }

}

/**
 * Klasa implementująca funkcjonalność pionka
 * @see Field
 * @see Board
 */
class Pawn implements Serializable{
    /**
     * Do jakiego gracza należy dany pionek
     */
    private int playerNumber = 0;

    Pawn(int playerNumber){
        this.playerNumber=playerNumber;
    }

    public int getPlayerNumber(){
        return playerNumber;
    }
}
