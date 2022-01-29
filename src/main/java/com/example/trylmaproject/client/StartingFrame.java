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
	private JComboBox<Integer> roundId;

	private int typeOfGame = 0;

	private int[][] games;


	public StartingFrame(int[][] games)
	{
		this.games = games;
		setStartReplay();
		this.getContentPane().add(this.panel);
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

	}
	private void changePanel()
	{
		panel.remove(playNewGame);
		panel.remove(replayGame);

		littlePanel = new JPanel();
		littlePanel.setLayout(new GridLayout(2,2));

		roundId = new JComboBox<>();
		roundId.setEditable(false);
		gameId = new JComboBox<>();
		gameId.setEditable(false);

		for (int i: games[0]) {
			gameId.addItem(i);
		}

		gameId.addActionListener(e -> {
			for(int i = 0; i<games.length;i++)
				if(games[0] == gameId.getSelectedItem())
					roundId.addItem(games[1][i]);
			startReplay.setEnabled(true);
		});

		littlePanel.add(new JTextField("Id gry:"));
		littlePanel.add(gameId);
		littlePanel.add(new JTextField("Id ruchu"));
		littlePanel.add(roundId);

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
		synchronized (this){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return (int)gameId.getSelectedItem();
	}
	public int getRoundId()
	{
		return (int)roundId.getSelectedItem();
	}

}
