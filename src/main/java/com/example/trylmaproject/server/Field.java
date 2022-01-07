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
    private int playerNumber;
    private Ellipse2D.Double circle;
    private Color color;
    public static final int DEFAULT_RADIUS = 40;
    //TODO Ustawić skalowanie do wielkości okna
    //private Color[] colorList = {Color.BLACK, Color.GREEN, Color.BLUE, Color.YELLOW, Color.RED, Color.PURPLE, Color.BROWN};

    public int getPlayerNumber() {
        return playerNumber;
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
        if(playerNumber > 7 || playerNumber < 0){
            this.playerNumber = 0;
        }
        else {
            this.playerNumber = playerNumber;
        }
        //color = colorList[this.playerNumber];
    }

    Field(int posy, int posx){
        this.posx=posx;
        this.posy=posy;

    }

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
