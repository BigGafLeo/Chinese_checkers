package com.example.trylmaproject.client;

import com.example.trylmaprojectdatabase.server.GameDatabase;

import javax.swing.*;
import java.awt.*;

/**
 * Klasa Implementująca okno wyboru nowej gry lub kontynuacji gry
 * Umożliwia wybranie idGry
 *  @author Mateusz Teplicki, Karol Dzwonkowski
 */
public class StartingFrame extends JFrame
{
	/**
	 * Komponenty występujące w StartingFrame
	 */
	private JPanel panel;
	private JPanel littlePanel;
	private JButton playNewGame;
	private JButton replayGame;
	private JButton startReplay;
	private JComboBox<Integer> gameId;
	private JLabel chosenGame;

	/**
	 * Zmienne sterujące rozgrywka
	 */
	private int typeOfGame = 0;
	private int lastRound = 0;

	/**
	 * Tablica zapisanych gier wraz z ruchami
	 */
	private int[][] games;

	/**
	 * Predefiniowane wielkości okna
	 */
	int DEFAULT_WIDTH = 400;
	int DEFAULT_HEIGHT = 200;

	/**
	 * Konstruktor Frame'a dla okna startowego.
	 * Konstruktor wywołuje niezbędne metody tworzące.
	 * @param tablica zapisanych gier wraz z ruchami
	 */
	public StartingFrame(int[][] games)
	{
		this.games = games;
		this.setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);
		this.setResizable(false);
		setStartReplay();
		this.getContentPane().add(this.panel);
		pack();
	}
	/**
	 * Metoda przygotowująca panel do dodania go do Frame'a.
	 */
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

	/**
	 * metoda zmieniająca zawartość głównego panelu w przypadku wybrania kontynuacji
	 */
	private void changePanel()
	{
		//usunięcie starych przycisków z panelu
		panel.remove(playNewGame);
		panel.remove(replayGame);

		//stworzenie nowych elementów panelu
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
						lastRound =  games[i][1];
					break;
				}
			chosenGame.setText(Integer.toString(lastRound));
				startReplay.setEnabled(true);
		});

		chosenGame = new JLabel();

		//dodanie nowych elementów panelu
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

	/**
	 * @return 1 - nowa gra, 2 - kontynuacja gry
	 */
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
