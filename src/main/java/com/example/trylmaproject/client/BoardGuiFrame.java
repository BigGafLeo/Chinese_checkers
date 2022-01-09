package com.example.trylmaproject.client;

import com.example.trylmaproject.server.Field;
import com.example.trylmaproject.server.Player;

import javax.swing.*;
import java.awt.*;

import static com.example.trylmaproject.client.BoardGuiPanel.colorForPlayer;

public class BoardGuiFrame extends JFrame
{
	private BoardGuiPanel panel;
	private JPanel extraPanel;
	private JTextArea communication;
	private JPanel playersList;
	private int playerNumber;
	private JButton skipButton;
	private String[] scoreTable;
	private boolean breakSignal = false;
	private boolean turn = false;
	private int homManyPlayers;
	private Player[] players;
	private TextField[] playersName;

	public BoardGuiFrame(int playerNumber, Field[][] board,Player[] players)
	{
		this.players = players;
		this.setLayout(new BorderLayout());
		this.playerNumber = playerNumber;
		homManyPlayers = 0;

		panel = new BoardGuiPanel(board, playerNumber);
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.add(panel,BorderLayout.CENTER);

		extraPanel = new JPanel();
		extraPanel.setLayout(new BorderLayout());
		extraPanel.setPreferredSize(new Dimension(250,1000));
		extraPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		playersName =  new TextField[players.length];
		playersList = new JPanel(new GridLayout(players.length,1));
		playersList.setBackground(Color.WHITE);
		playersList.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		int i = 0;
		while(i<6 && players[i] != null) {
			playersName[i] = new TextField(players[i].name);
			playersName[i].setForeground(colorForPlayer(players[i].number));
			playersName[i].setEditable(false);
			playersName[i].setBackground(Color.WHITE);
			playersList.add(playersName[i]);
			i++;
		}


		extraPanel.add(playersList,BorderLayout.NORTH);

		communication = new JTextArea();
		communication.setEditable(false);
		communication.setLineWrap(true);
		communication.setText("Gra rozpoczęta\n");
		communication.setBorder(BorderFactory.createLineBorder(Color.BLACK));
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
		winnerCommunicat(winner);
	}
	private void winnerCommunicat(String winner)
	{
		communication.append("Gracz " + winner + "zajął " + homManyPlayers + "miejsce!!!\n");
	}
	public void setTurn(boolean turn)
	{
		this.turn = turn;
		panel.setIsYourTurn(turn);
		communication.append("Twoja tura!\n");
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


