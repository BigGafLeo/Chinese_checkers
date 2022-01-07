package com.example.trylmaproject.client;

import com.example.trylmaproject.server.Board;
import com.example.trylmaproject.server.Field;
import javafx.scene.shape.Circle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class BoardGuiPanel extends JPanel
{

    public final static int DEFAULT_BOARD_WIDTH = 13 * Field.DEFAULT_RADIUS * 2;
    public final static int DEFAULT_BOARD_HEIGHT = 17 * Field.DEFAULT_RADIUS * 2;
    private Field pawnToMove;
    private Field fieldMoveTo;
    Field[][] board;
    public BoardGuiPanel(Field[][] board)
    {
        setBackground(Color.WHITE);
        this.board = board;
        setPreferredSize(new Dimension(DEFAULT_BOARD_WIDTH,DEFAULT_BOARD_HEIGHT));
        addMouseListener(new MouseHandler());
        repaint();
    }
    //TODO Ustawić drugą metodę find w której upewniamy się że pole na które chce się ruszyć gracz jest puste
    public Field find(Point2D point)
    {
        for(int i = 0 ; i<17; i++)
            for (int j = 0 ; j<25; j++)
                if(board[i][j].getCircle().contains(point))
                    return board[i][j];
        return null;
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
        switch (playerNumber)
        {
            case 1:
                return Color.BLUE;

            case 2:
                return Color.GREEN;

            case 3:
                return Color.RED;

            case 4:
                return Color.CYAN;

            case 5:
                return Color.magenta;

            case 6:
                return Color.pink;

        }
        return Color.WHITE;
    }

    private class MouseHandler extends MouseAdapter
    {
        public void mousePressed(MouseEvent event)
        {
            if(pawnToMove==null)
                pawnToMove = find(event.getPoint());
            else
                fieldMoveTo = find(event.getPoint());
        }
    }
}

