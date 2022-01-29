package com.example.trylmaproject.client;

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
	private JTextField idRuchu;
	private JTextField idGry;

	private boolean newGame = false;
	private boolean oldGame = false;

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
			newGame = true;

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

		idGry = new JTextField("Id gry:");
		idRuchu = new JTextField("Id ruchu");
		gameId = new JComboBox<>();
		for (int i: games[0]) {
			
		}

		littlePanel.add(idGry);


		panel.setLayout(new GridLayout(2,1));
		panel.add(littlePanel);
		
	}

}
