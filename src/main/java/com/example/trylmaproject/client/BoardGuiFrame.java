package com.example.trylmaproject.client;

import com.example.trylmaproject.server.Field;

import javax.swing.*;
import java.awt.*;

public class BoardGuiFrame extends JFrame
{
	private BoardGuiPanel panel;
	private int playerNumber;
	private JButton skipButton;
	private String[] scoreTable;
	private boolean breakSignal = false;
	private boolean turn = false;
	private int homManyPlayers;

	public BoardGuiFrame(int playerNumber, Field[][] board)
	{
		this.setLayout(new BorderLayout());
		this.playerNumber = playerNumber;
		homManyPlayers = 0;

		panel = new BoardGuiPanel(board, playerNumber);
		this.add(panel,BorderLayout.CENTER);

		skipButton = new JButton("Pomiń ture");
		skipButton.addActionListener(event ->
		{
			if(turn){
				breakSignal = true;
				synchronized (panel){
					panel.notify();
				}
			}
			else JOptionPane.showConfirmDialog(null,"To nie twoja kolejka!", "ERROR", JOptionPane.ERROR_MESSAGE);

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

	public void whoWinner(String winner)
	{
		scoreTable[homManyPlayers] = winner;
		homManyPlayers++;
	}
	public void setTurn(boolean turn)
	{
		this.turn = turn;
		panel.setIsYourTurn(turn);
	}
	public void endGame(String losser)
	{
		scoreTable[homManyPlayers] = losser;
		JOptionPane.showConfirmDialog(this, "Koniec gry\n 1." + scoreTable.toString());
	}

	public String getMessage() {
		while(true){
			try {
				synchronized (this){
					wait(100);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(breakSignal) {
				breakSignal = false;
				return "BREAK";
			}
			else if(panel.moveSignal){
				panel.moveSignal = false;
				return panel.getMoveFromPanel();
			}
		}

	}

}


