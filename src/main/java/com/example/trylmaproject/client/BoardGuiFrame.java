package com.example.trylmaproject.client;

import javax.swing.*;

public class BoardGuiFrame extends JFrame
{
	private JPanel boardPanel;
	private static final int DEFAULT_FIELD_LENGHT = 40;
	private final static int DEFAULT_HEIGHT = 17*DEFAULT_FIELD_LENGHT;
	private final static int DEFAULT_WIDTH = 25*DEFAULT_FIELD_LENGHT;

	public BoardGuiFrame(int playerCount)
	{
		this.boardPanel = new BoardGuiPanel(playerCount);
		this.getContentPane().add(this.boardPanel);
		this.setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);
	}

}
