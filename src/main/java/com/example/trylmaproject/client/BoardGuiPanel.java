package com.example.trylmaproject.client;

import com.example.trylmaproject.server.Board;
import com.example.trylmaproject.server.Field;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class BoardGuiPanel extends JPanel
{

    public final static int DEFAULT_BOARD_WIDTH = 13 * Field.DEFAULT_RADIUS * 2 + 12 * Field.DEFAULT_RADIUS;
    public final static int DEFAULT_BOARD_HEIGHT = 17 * Field.DEFAULT_RADIUS * 2 + 16 * Field.DEFAULT_RADIUS;
    public BoardGuiPanel(Board board)
    {
        setPreferredSize(new Dimension(DEFAULT_BOARD_WIDTH,DEFAULT_BOARD_HEIGHT));
    }

    protected void paintComponent(Graphics g,Board board)
    {
        Graphics2D g2D = (Graphics2D) g;
        Field[][] tempBoard = board.getBoard();
        for(int i = 0; i<24;i++)
            for(int j = 0; j<16;j++)
            {
                if(tempBoard[i][j] != null)
                {
                    g2D.draw((Shape) tempBoard[i][j].fieldDrawing(j*Field.DEFAULT_RADIUS,i*Field.DEFAULT_RADIUS));
                }
            }

    }
}
//public class BoardGuiPanel extends JPanel
//{
//    public final static int DEFAULT_PAWN_RADIUS = 15;
//    public final static int DEFAULT_BOARD_WIDTH = 13 * DEFAULT_PAWN_RADIUS * 2 + 12 * DEFAULT_PAWN_RADIUS;
//    public final static int DEFAULT_BOARD_HEIGHT = 17 * DEFAULT_PAWN_RADIUS * 2 + 16 * DEFAULT_PAWN_RADIUS;
//
//    public BoardGuiPanel()
//    {
//        this.setSize(DEFAULT_BOARD_WIDTH, DEFAULT_BOARD_HEIGHT);
//        add(new DrawEmptyBoard());
//    }
//    class DrawEmptyBoard extends JComponent
//    {
//        public void paintComponent(Graphics g)
//        {
//            int centerX = 19 * DEFAULT_PAWN_RADIUS;
//            int centerY = 1 * DEFAULT_PAWN_RADIUS;
//
//
//            Graphics2D g2 = (Graphics2D) g;
//            Ellipse2D circle = new Ellipse2D.Double();
//            circle.setFrameFromCenter(centerX,centerY,centerX + DEFAULT_PAWN_RADIUS,centerY + DEFAULT_PAWN_RADIUS);
//            g2.draw(circle);
//        }
//    }
//}
