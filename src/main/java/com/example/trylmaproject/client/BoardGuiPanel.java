package com.example.trylmaproject.client;

import javax.swing.*;
import java.awt.*;

public class BoardGuiPanel extends JPanel
{
//	private final int playersCount;
	private BoardField[][] board;
	final static int DEFAULT_BOARD_WIDTH = 25;
	final static int DEFAULT_BOARD_HEIGHT = 17;

	public BoardGuiPanel(int playersCount)
	{
//		this.playersCount = playersCount;
		//TODO Change object to specified class
		board = new BoardField[DEFAULT_BOARD_WIDTH][DEFAULT_BOARD_HEIGHT];
		this.setLayout(new GridLayout(DEFAULT_BOARD_HEIGHT,DEFAULT_BOARD_WIDTH));
		boardCreation();

	}

	private void boardCreation()
	{
		for(int _height = 0; _height < DEFAULT_BOARD_HEIGHT; _height++)
			for(int _width = 0;_width < DEFAULT_BOARD_WIDTH; _width++)
			{
				this.board[_width][_height] = new BoardField();
				this.add(this.board[_width][_height]);
			}
	}

}
