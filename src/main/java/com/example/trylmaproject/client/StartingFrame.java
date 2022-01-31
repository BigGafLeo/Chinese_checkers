package com.example.trylmaproject.client;

import com.example.trylmaprojectdatabase.server.GameDatabase;

import javax.swing.*;
import java.awt.*;

public class StartingFrame extends JFrame
{
	private JPanel panel;
	private JPanel littlePanel;
	private JButton playNewGame;
	private JButton replayGame;
	private JButton startReplay;
	private JComboBox<Integer> gameId;
	private JLabel chosenGame;

	private int typeOfGame = 0;
	private int lastRound = 0;

	private int[][] games;

	int DEFAULT_WIDTH = 400;
	int DEFAULT_HEIGHT = 200;


	public StartingFrame(int[][] games)
	{
		this.games = games;
		this.setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);
		this.setResizable(false);
		setStartReplay();
		this.getContentPane().add(this.panel);
		pack();
	}
	private void setStartReplay()
	{
		panel = new JPanel();
		panel.setLayout(new GridLayout(2,1));

		playNewGame = new JButton("Zagraj nową grę");
		playNewGame.addActionListener(e -> {
			typeOfGame = GameDatabase.NEW_GAME;
			synchronized (this) {
				notify();
			}
		});
		panel.add(playNewGame);

		replayGame = new JButton("Kontynuuj grę");
		replayGame.addActionListener(event->{
			changePanel();
		});
		panel.add(replayGame);
	}
	private void changePanel()
	{
		panel.remove(playNewGame);
		panel.remove(replayGame);

		littlePanel = new JPanel();
		littlePanel.setLayout(new GridLayout(2,2));
		
		gameId = new JComboBox<>();
		gameId.setEditable(false);

		for(int i = 0; i < games.length;i++)
			gameId.addItem(games[i][0]);
		gameId.setSelectedItem(null);

		gameId.addActionListener(e -> {
			for(int i = 0; i<games.length;i++)
				if(games[i][0] == (int)gameId.getSelectedItem()) {
					for (int j = 0; j <= games[i][1]; j++)
						lastRound =  j;
					break;
				}
			chosenGame.setText(Integer.toString(lastRound));
				startReplay.setEnabled(true);
		});

		chosenGame = new JLabel();

		littlePanel.add(new JLabel("Id gry:"));
		littlePanel.add(gameId);
		littlePanel.add(new JLabel("Ostatni ruch:"));
		littlePanel.add(chosenGame);

		panel.setLayout(new GridLayout(2,1));
		panel.add(littlePanel);

		startReplay = new JButton("Kontynuuj grę");
		startReplay.setEnabled(false);
		startReplay.addActionListener(e -> {
			typeOfGame = GameDatabase.LOADED_GAME;
			synchronized (this) {
				notify();
			}
		});
		panel.add(startReplay);
		pack();
		repaint();
	}
	public int getTypeOfGame()
	{
		synchronized (this){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return typeOfGame;
	}
	public int getGameId()
	{
		return (int)gameId.getSelectedItem();
	}
	public int getRoundId()
	{
		return lastRound;
	}

}
