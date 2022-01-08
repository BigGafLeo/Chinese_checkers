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
		panel = new BoardGuiPanel(board, playerNumber);
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

	public String getMove() {
		synchronized (panel){
			try {
				panel.wait();
				return panel.getMoveFromPanel();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}


