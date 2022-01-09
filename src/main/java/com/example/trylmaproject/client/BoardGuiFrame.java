package com.example.trylmaproject.client;

import com.example.trylmaproject.server.Field;
import com.example.trylmaproject.server.Player;

import javax.swing.*;
import java.awt.*;

public class BoardGuiFrame extends JFrame
{
	private BoardGuiPanel panel;
	private JPanel extraPanel;
	private JTextArea communication;
	private JTextArea playersList;
	private int playerNumber;
	private JButton skipButton;
	private String[] scoreTable;
	private boolean breakSignal = false;
	private boolean turn = false;
	private int homManyPlayers;
	private Player[] players;

	public BoardGuiFrame(int playerNumber, Field[][] board,Player[] players)
	{
		this.players = players;
		this.setLayout(new BorderLayout());
		this.playerNumber = playerNumber;
		homManyPlayers = 0;

		panel = new BoardGuiPanel(board, playerNumber);
		this.add(panel,BorderLayout.CENTER);

		extraPanel = new JPanel();
		extraPanel.setLayout(new BorderLayout());
		extraPanel.setPreferredSize(new Dimension(250,1000));

//		String list;
//		list = players[0].name;
//		for(int i = 1; i < players.length; i++)
//		{
//
//		}
		playersList = new JTextArea(12,1);
		playersList.setEditable(false);
		playersList.setLineWrap(false);

		int i = 0;
		//TODO Zmienić textarea na komponent z małymi text fieldami! i pokolorować tak jak na dole
		while(players[i] != null) {

			playersList.append(players[i].name);
			playersList.setForeground(panel.colorForPlayer(players[i].number));
			playersList.append("\n");
			i++;
		}

		extraPanel.add(playersList,BorderLayout.NORTH);

		communication = new JTextArea();
		communication.setEditable(false);
		communication.setLineWrap(true);
		communication.setText("Lolol\ndsad\ndasdas\ndjasbdl");
		extraPanel.add(communication,BorderLayout.CENTER);

		skipButton = new JButton("Skończ turę");
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
		extraPanel.add(skipButton,BorderLayout.SOUTH);
		this.add(extraPanel,BorderLayout.EAST);

		pack();

		scoreTable = new String[6];
		setDefaultCloseOperation(3);
		setTitle("Warcaby gracz: " + playerNumber);
		setVisible(true);
		setLocationRelativeTo(null);
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

	public void setPlayerList(Player[] players){
		this.players = players;
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


