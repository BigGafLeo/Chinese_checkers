package com.example.trylmaproject.client;

import com.example.trylmaproject.server.Field;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.Serializable;

import static com.example.trylmaproject.client.DrawnCircles.getDefaultRadius;
import static com.example.trylmaproject.client.DrawnCircles.setDefaultRadius;

/**
 * Klasa implementująca planszę do grania
 * @author Mateusz Teplicki, Karol Dzwonkowski
 * @see BoardGuiFrame
 */

public class BoardGuiPanel extends JPanel
{
    //Domyślne wymiary planszy
    private int borderSize = 10;
    public final static int DEFAULT_BOARD_WIDTH = 13 * getDefaultRadius() + 20 + 24;
    public final static int DEFAULT_BOARD_HEIGHT = 17 * getDefaultRadius() + 20 + 32;

    //Tablice zawierające informacje o ruchu wykonanym przez gracza
    private int[] pawnToMove;
    private int[] fieldToMove;

    //Zmienne odpowiadające za sterowanie grą
    private boolean isYourTurn = false;
    public boolean moveSignal;

    //Plansza logiczna
    Field[][] board;

    //Plansza obiektów rysowanych
    DrawnCircles[][] drawnBoard;

    /**
     * Rozmiar Y dwuwymiarowej tablicy {@link #board}
     */
    private int boardYSize;
    /**
     * Rozmiar X dwuwymiarowej tablicy {@link #board}
     */
    private int boardXSize;

    /**
     * Przerwa w pikselach pomiędzy sąsiednimi polami w poziomie
     */
    private int gapBetweenFields = 2;

    //Numer gracza, dla którego rysowana jest plansza
    private int playerNumber;

    //Kółko, którym gracz rusza (jedynie wizualnie)
    public Ellipse2D motionCircle;

    /**
     * Konstruktor planszy.
     * @param board plansza
     * @param playerNumber numer gracza, dla którego rysowana jest plansza
     */
    public BoardGuiPanel(Field[][] board, int playerNumber)
    {
        this.board = board;
        this.playerNumber = playerNumber;
        drawnBoard = new DrawnCircles[board.length][board[0].length];
        boardToDrawnBoard(board);
        boardYSize=drawnBoard.length;
        boardXSize=drawnBoard[0].length;

        //Obliczanie wielkości panelu uzależnione od wielkości okna
        double panelSizeX = (this.getWidth() - borderSize - boardXSize * gapBetweenFields) / 13;
        double panelSizeY = (this.getHeight() - borderSize) / 17;

        //Ustawianie wielkości pól uzależnione od wielkości panelu
        if(panelSizeY > panelSizeX)
            setDefaultRadius( (int) panelSizeX);
        else
            setDefaultRadius( (int) panelSizeY);

        //Ustawienie podstawowych własności Panelu
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setPreferredSize(new Dimension(DEFAULT_BOARD_WIDTH,DEFAULT_BOARD_HEIGHT));
        addMouseListener(new MouseHandler());
        addMouseMotionListener(new MouseMotionHandler());
        repaint();
    }

    /**
     * Funkcja odświeżająca plansze
     * @param board plansza otrzymana od server -> client -> BoardGuiFrame do ponownego narysowania
     */
    public void panelRepaint(Field[][] board)
    {
        this.board = board;
        boardToDrawnBoard(board);
        repaint();
    }
    private void boardToDrawnBoard(Field[][] board)
    {
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++)
            {
                if(board[i][j] != null)
                    if(drawnBoard[i][j] == null)
                        drawnBoard[i][j] = new DrawnCircles(board[i][j].getPlayerNumber());
                    else
                        drawnBoard[i][j].setPlayerNumber(board[i][j].getPlayerNumber());
            }
    }

    /**
     * Metoda rysująca plansze
     * @param g moduł grafiki panelu
     */
    public void paintComponent(Graphics g)
    {
        //Obliczanie wielkości panelu uzależnione od wielkości okna
        double panelSizeX = (this.getWidth() - borderSize - boardXSize * gapBetweenFields) / 13;
        double panelSizeY = (this.getHeight() - borderSize) / 17;

        //Ustawianie wielkości pól uzależnione od wielkości panelu
        if(panelSizeY > panelSizeX)
            setDefaultRadius( (int) panelSizeX);
        else
            setDefaultRadius( (int) panelSizeY);
        super.paintComponent(g);

        //Rysowanie pól planszy oraz pionków
        Graphics2D g2D = (Graphics2D) g;
        for(int i = 0; i< boardYSize;i++)
            for(int j = 0; j< boardXSize;j++)
            {
                if(board[i][j] != null)
                {
                    g2D.draw(drawnBoard[i][j].fieldDrawing(borderSize + i*getDefaultRadius(),borderSize + gapBetweenFields*j + ((double) j)/2*getDefaultRadius()));
                    if (drawnBoard[i][j].getPlayerNumber()!=0)
                    {
                        g2D.setPaint(colorForPlayer(drawnBoard[i][j].getPlayerNumber()));
                        g2D.fill(drawnBoard[i][j].fieldDrawing(borderSize + i*getDefaultRadius(),borderSize + gapBetweenFields*j + ((double) j)/2*getDefaultRadius()));
                        g2D.setPaint(Color.BLACK);
                    }
                }
            }

        //Rysowanie pionka, którym rusza gracz (tylko wizualnie)
        if(motionCircle != null)
        {
            g2D.setPaint(colorForPlayer(playerNumber));
            g2D.draw(motionCircle);
            g2D.fill(motionCircle);
            g2D.setPaint(Color.BLACK);
        }
    }

    /**
     * Metoda statyczna przypasowująca kolor do numeru gracza
     * @param playerNumber numer gracza, dla którego rysowana jest plansza
     * @return kolor zależny od otrzymanego numeru gracza
     */
    public static Color colorForPlayer(int playerNumber)
    {
        return switch (playerNumber) {
            case 1 -> Color.BLUE;
            case 2 -> Color.GREEN;
            case 3 -> Color.RED;
            case 4 -> Color.CYAN;
            case 5 -> Color.magenta;
            case 6 -> Color.pink;
            default -> Color.WHITE;
        };
    }

    /**
     * Metoda ustawiająca zmienną turn odpowiedzialną za kolejkę
     * @param turn zmienna informująca czy jest ruch użytkownika
     */
    public void setIsYourTurn(boolean turn)
    {
        isYourTurn = turn;
    }

    /**
     * Metoda sprawdzająca, czy kliknięto pionek oraz wyznaczająca go
     * @param point punkt, na który kliknięto myszą
     * @return miejsce w tabeli klikniętego pionka
     */
    private int[] findPawn(Point2D point)
    {
        for(int i = 0 ; i<17; i++) {
            for (int j = 0 ; j<25; j++) {
                if(drawnBoard[i][j] != null && drawnBoard[i][j].getCircle().contains(point) && drawnBoard[i][j].getPlayerNumber() == playerNumber)
                {
                    return new int[]{i,j};
                }
            }
        }
        return null;
    }

    /**
     * Metoda sprawdzająca, czy kliknięto pionek innego gracza oraz wyznaczająca go
     * @param point punkt, na który kliknięto myszą
     * @return miejsce w tabeli klikniętego pionka
     */
    private int[] findOtherPlayerPawn(Point2D point)
    {
        for(int i = 0 ; i<17; i++) {
            for (int j = 0 ; j<25; j++) {
                if(drawnBoard[i][j] != null && drawnBoard[i][j].getCircle().contains(point) && drawnBoard[i][j].getPlayerNumber() != playerNumber && drawnBoard[i][j].getPlayerNumber() != 0)
                {
                    return new int[]{i,j};
                }
            }
        }
        return null;
    }
    /**
     * Metoda sprawdzają czy upuszczono pionek na wolne pole
     * @param point punkt, na którym puszczono przycisk myszki
     * @return miejsce w tabeli, na które upuszczono pionek
     */
    private int[] findEmptyField(Point2D point)
    {
        for(int i = 0 ; i<17; i++)
            for (int j = 0 ; j<25; j++)
                if(drawnBoard[i][j] != null && drawnBoard[i][j].getCircle().contains(point) && drawnBoard[i][j].getPlayerNumber() == 0)
                {
                    return new int[]{i,j};
                }
        return null;
    }

    /**
     * Klasa odpowiedzialna za obsługę myszy (kliknięcie / zwolnienia przycisku)
     */
    private class MouseHandler extends MouseAdapter implements Serializable
    {

        //Metoda obłuskująca zdarzenie naciśnięcia lewego przycisku myszy
        public void mousePressed(MouseEvent event)
        {
            if(pawnToMove == null)
            {
                pawnToMove = findPawn(event.getPoint());
            }
        }

        //Metoda obsługująca zdarzenie zwolnienia lewego przycisku myszy
        public void mouseReleased (MouseEvent event)
        {
            if(fieldToMove == null) {
                if (pawnToMove != null) {
                    fieldToMove = findEmptyField(event.getPoint());
                    if(fieldToMove != null)
                    {
                        motionCircle = null;
                        repaint();
                        synchronized (this) {
                            notifyAll();
                        }
                        moveSignal = true;
                    }
                    else
                    {
                        drawnBoard[pawnToMove[0]][pawnToMove[1]].setPlayerNumber(playerNumber);
                        pawnToMove = null;
                        motionCircle = null;
                        repaint();
                    }
                }
            }
        }

    }

    /**
     * Klasa odpowiedzialna za obsługę myszy (przesuwanie myszy)
     */
    private class MouseMotionHandler extends MouseMotionAdapter
    {

        /**
         * Metoda obsługująca zdarzenie ruchu myszy bez kliknięcia.
         * Służy zmianie kursora w zależności, nad jakim komponentem się znajduje
         * @param event event myszy
         */
        @Override
        public void mouseMoved(MouseEvent event) {
            if (isYourTurn) {

                //Gdy kursor znajduje się nad pionkiem gracza
                if (findPawn(event.getPoint()) != null)
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                //Gdy kursor znajduje się nad pionkiem innego gracza
                else if (findOtherPlayerPawn(event.getPoint()) != null)
                    setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

                //Gdy kursor znajduje się w dowolnym innym miejscu, ale jest kolejka gracza
                else
                    setCursor(Cursor.getDefaultCursor());
            }

            //Gdy jest kolejka innego gracza
//            else
//                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }

        /**
         * Metoda obsługująca zdarzenie ruchu myszy po wciśnięciu lewego klawisza myszy
         * @param event event myszy
         */
        @Override
        public void mouseDragged (MouseEvent event)
        {
            if(isYourTurn) {
                if (pawnToMove != null && fieldToMove == null) {
                    motionCircle = new Ellipse2D.Double(event.getX()-((double)getDefaultRadius()/2), event.getY()-((double)getDefaultRadius()/2), getDefaultRadius(), getDefaultRadius());
                    drawnBoard[pawnToMove[0]][pawnToMove[1]].setPlayerNumber(0);
                    repaint();
                }
                if(pawnToMove != null && fieldToMove == null && event.getModifiersEx() == event.MOUSE_RELEASED)
                {
                    fieldToMove = findEmptyField(event.getPoint());
                    motionCircle = null;
                    repaint();
                    synchronized (this) {
                        notifyAll();
                    }
                    moveSignal = true;
                }
            }
        }
    }

    /**
     * Metoda umożliwiająca BoardGuiFrame zebranie informacji o ruchu gracza
     * @return Komunikat dla boardGuiFrame -> client -> server
     */
    public String getMoveFromPanel()
    {
        String temp = "RUCH: " + pawnToMove[1] + " " + pawnToMove[0] + " " + fieldToMove[1] + " " + fieldToMove[0];
        pawnToMove = null;
        fieldToMove = null;
        return temp;
    }
}

