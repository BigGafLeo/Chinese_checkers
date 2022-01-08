package com.example.trylmaproject.server;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;

import java.awt.geom.Ellipse2D;
import java.io.Serializable;


/**
 * Klasa przechowująca informacje o pojedynczym polu na planszy
 * do gry w chińskie warcaby
 * @author Mateusz Teplicki
 */
public class Field implements Serializable {
    private int posx;
    private int posy;

    /**
     * Który gracz okupuje dane pole w grze
     */
    private Pawn pawn = null;
    private int playerNumber;

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

    public int getPosx(){
        return posx;
    }

    public int getPosy(){
        return posy;
    }

    /**
     * Metoda przydzielająca do danego pola numer gracza wraz z kolorem
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

    Field(int posy, int posx){
        this.posx=posx;
        this.posy=posy;

    }

    /**
     * Czy paramatry zawierają się w kólku
     * @param x
     * @param y
     * @return
     */
    public boolean isHit(int x, int y)
    {
        return circle.getBounds().contains(x,y);
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

class Pawn implements Serializable{
    private int playerNumber = 0;
    boolean IS_ABLE_TO_MOVE_AGAIN = false;

    Pawn(int playerNumber){
        this.playerNumber=playerNumber;
    }

    public int getPlayerNumber(){
        return playerNumber;
    }
}
