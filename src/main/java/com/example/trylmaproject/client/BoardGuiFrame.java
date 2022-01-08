package com.example.trylmaproject.client;

import com.example.trylmaproject.server.Board;
import com.example.trylmaproject.server.Field;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class BoardGuiFrame extends JFrame
{
	private BoardGuiPanel panel;
	private int playerNumber;
	private JButton skipButton;
	private String[] scoreTable;

	public BoardGuiFrame(int playerNumber, Field[][] board)
	{
		this.setLayout(new BorderLayout());
		panel = new BoardGuiPanel(board);
		this.add(panel,BorderLayout.CENTER);

		skipButton = new JButton("Pomiń ture");
		skipButton.addActionListener(event ->
		{
			//TODO Dodać obsługe klawisza
		});
		this.add(skipButton,BorderLayout.SOUTH);
		this.playerNumber = playerNumber;
		pack();
		scoreTable = new String[6];
	}

	public void boardRepaint(Field[][] board)
	{
		panel.panelRepaint(board);
	}
	//TODO Dodać implementacje tych metod
	public void whoWinner(String winner)
	{

	}
	public void isYourTurn(boolean turn)
	{

	}
	public void endGame(String losser)
	{

	}
}
//public class BoardGuiFrame extends JFrame
//{
//	private JPanel boardPanel;
////	private static final int DEFAULT_FIELD_LENGHT = 40;
////	private final static int DEFAULT_HEIGHT = 17*DEFAULT_FIELD_LENGHT;
////	private final static int DEFAULT_WIDTH = 25*DEFAULT_FIELD_LENGHT;
//
//	public BoardGuiFrame(int playerCount)
//	{
//		this.boardPanel = new BoardGuiPanel();
//		this.getContentPane().add(this.boardPanel);
////		this.setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);
////		add(new BoardGuiPanel());
////		add(new DrawComponent());
//		this.setSize(BoardGuiPanel.DEFAULT_BOARD_WIDTH, BoardGuiPanel.DEFAULT_BOARD_HEIGHT);
//	}
//
//
//}
//class DrawComponent extends JComponent
//{
//	public final static int DEFAULT_PAWN_RADIUS = 15;
//	public final static int DEFAULT_BOARD_WIDTH = 13 * DEFAULT_PAWN_RADIUS * 2 + 12 * DEFAULT_PAWN_RADIUS;
//	public final static int DEFAULT_BOARD_HEIGHT = 17 * DEFAULT_PAWN_RADIUS * 2 + 16 * DEFAULT_PAWN_RADIUS;
//	public void paintComponent(Graphics g)
//	{
//		Graphics2D g2 = (Graphics2D) g;
//
//		int centerX = 19 * DEFAULT_PAWN_RADIUS;
//		int centerY = 1 * DEFAULT_PAWN_RADIUS;
//
//		Ellipse2D circle = new Ellipse2D.Double();
//		circle.setFrameFromCenter(centerX,centerY,centerX + DEFAULT_PAWN_RADIUS,centerY + DEFAULT_PAWN_RADIUS);
//		g2.draw(circle);
//	}
//}

