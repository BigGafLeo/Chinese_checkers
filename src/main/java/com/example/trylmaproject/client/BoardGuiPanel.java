package com.example.trylmaproject.client;

import com.example.trylmaproject.server.Field;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class BoardGuiPanel extends JPanel
{

    public final static int DEFAULT_BOARD_WIDTH = 13 * Field.DEFAULT_RADIUS * 2;
    public final static int DEFAULT_BOARD_HEIGHT = 17 * Field.DEFAULT_RADIUS * 2;
    private int[] pawnToMove;
    private int[] fieldToMove;
    private boolean isYourTurn = false;
    Field[][] board;
    private int playerNumber;
    public boolean moveSignal;

    public BoardGuiPanel(Field[][] board, int playerNumber)
    {
        this.board = board;
        this.playerNumber = playerNumber;

        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(DEFAULT_BOARD_WIDTH,DEFAULT_BOARD_HEIGHT));
        addMouseListener(new MouseHandler());
        repaint();
    }

    public void panelRepaint(Field[][] board)
    {
        this.board = board;
        repaint();
    }

    public void paintComponent(Graphics g)
    {
        Graphics2D g2D = (Graphics2D) g;
        for(int i = 0; i<17;i++)
            for(int j = 0; j<25;j++)
            {
                if(board[i][j] != null)
                {
                    g2D.draw(board[i][j].fieldDrawing(i*Field.DEFAULT_RADIUS,((double) j)/2*Field.DEFAULT_RADIUS));
                    if (board[i][j].getPlayerNumber()!=0)
                    {
                        g2D.setPaint(colorForPlayer(board[i][j].getPlayerNumber()));
                        g2D.fill(board[i][j].fieldDrawing(i*Field.DEFAULT_RADIUS,((double) j)/2*Field.DEFAULT_RADIUS));
                        g2D.setPaint(Color.BLACK);
                    }
                }
            }

    }

    private Color colorForPlayer(int playerNumber)
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


    private int[] findPawn(Point2D point)
    {
        for(int i = 0 ; i<17; i++) {
            for (int j = 0 ; j<25; j++) {
                if(board[i][j] != null && board[i][j].getCircle().contains(point) && board[i][j].getPlayerNumber() == playerNumber)
                {
                    return new int[]{i,j};
                }
            }
        }
        return null;
    }
    private int[] findEmptyField(Point2D point)
    {
        for(int i = 0 ; i<17; i++)
            for (int j = 0 ; j<25; j++)
                if(board[i][j] != null && board[i][j].getCircle().contains(point) && board[i][j].getPlayerNumber() == 0)
                {
                    return new int[]{i,j};
                }
        return null;
    }

    private class MouseHandler extends MouseAdapter
    {
        public void mousePressed(MouseEvent event)
        {

            if(pawnToMove == null) {
                pawnToMove = findPawn(event.getPoint());
            }
            else if(fieldToMove ==null){
                fieldToMove = findEmptyField(event.getPoint());
            }

            if(pawnToMove != null && fieldToMove != null)
            {
                synchronized (this){
                    notifyAll();
                }
                moveSignal = true;
            }
        }
    }
    public String getMoveFromPanel()
    {
        String temp = "RUCH: " + pawnToMove[0] + " " + pawnToMove[1] + " " + fieldToMove[0] + " " + fieldToMove[1];
        pawnToMove = null;
        fieldToMove = null;
        return temp;
    }
}

