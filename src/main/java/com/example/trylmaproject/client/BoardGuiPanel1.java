package com.example.trylmaproject.client;

import javax.swing.*;
import java.awt.*;

public class BoardGuiPanel1 extends JPanel
{
//	private final int playersCount;
	private BoardField[][] board;
	final static int DEFAULT_BOARD_WIDTH = 25;
	final static int DEFAULT_BOARD_HEIGHT = 17;

	public BoardGuiPanel1(int playersCount)
	{
//		this.playersCount = playersCount;
		//TODO Change object to specified class
		board = new BoardField[DEFAULT_BOARD_WIDTH][DEFAULT_BOARD_HEIGHT];
		this.setLayout(new GridLayout(DEFAULT_BOARD_HEIGHT,DEFAULT_BOARD_WIDTH));
		boardCreation();
		addingPlayers(playersCount);
	}

	/**
	 * Tworzenie tablicy oraz pplanszy wiersz po wierszu.
	 */

	private void boardCreation()
	{

		for(int _width = 0;_width < DEFAULT_BOARD_WIDTH; _width++)
		{
			if (_width != 12)
				this.board[_width][0] = new BoardField(FieldType.Locked);
			else
				this.board[_width][0] = new BoardField(FieldType.Empty);
			this.add(this.board[_width][0]);
		}

		for(int _width = 0;_width < DEFAULT_BOARD_WIDTH; _width++)
		{
			if (_width != 11 && _width != 13)
				this.board[_width][1] = new BoardField(FieldType.Locked);
			else
				this.board[_width][1] = new BoardField(FieldType.Empty);
			this.add(this.board[_width][1]);
		}

		for(int _width = 0;_width < DEFAULT_BOARD_WIDTH; _width++)
		{
			if (_width != 10 && _width != 12 && _width != 14)
				this.board[_width][2] = new BoardField(FieldType.Locked);
			else
				this.board[_width][2] = new BoardField(FieldType.Empty);
			this.add(this.board[_width][2]);
		}

		for(int _width = 0;_width < DEFAULT_BOARD_WIDTH; _width++)
		{
			if (_width != 9 && _width != 11 && _width != 13 && _width !=15)
				this.board[_width][3] = new BoardField(FieldType.Locked);
			else
				this.board[_width][3] = new BoardField(FieldType.Empty);
			this.add(this.board[_width][3]);
		}

		for(int _width = 0;_width < DEFAULT_BOARD_WIDTH; _width++)
		{
			if (_width % 2 != 0)
				this.board[_width][4] = new BoardField(FieldType.Locked);
			else
				this.board[_width][4] = new BoardField(FieldType.Empty);
			this.add(this.board[_width][4]);
		}

		for(int _width = 0;_width < DEFAULT_BOARD_WIDTH; _width++)
		{
			if (_width % 2 == 0)
				this.board[_width][5] = new BoardField(FieldType.Locked);
			else
				this.board[_width][5] = new BoardField(FieldType.Empty);
			this.add(this.board[_width][5]);
		}

		for(int _width = 0;_width < DEFAULT_BOARD_WIDTH; _width++)
		{
			if (_width != 2 && _width != 4 && _width != 6 && _width != 8 && _width != 10
					&& _width != 12 && _width != 14 && _width != 16 && _width != 18
					&& _width != 20 && _width != 22)
				this.board[_width][6] = new BoardField(FieldType.Locked);
			else
				this.board[_width][6] = new BoardField(FieldType.Empty);
			this.add(this.board[_width][6]);
		}

		for(int _width = 0;_width < DEFAULT_BOARD_WIDTH; _width++)
		{
			if (_width != 3 && _width != 5 && _width != 7 && _width != 9 && _width != 11
					&& _width != 13 && _width != 15 && _width != 17 && _width != 19 && _width != 21)
				this.board[_width][7] = new BoardField(FieldType.Locked);
			else
				this.board[_width][7] = new BoardField(FieldType.Empty);
			this.add(this.board[_width][7]);
		}

		for(int _width = 0;_width < DEFAULT_BOARD_WIDTH; _width++)
		{
			if (_width != 4 && _width != 6 && _width != 8 && _width != 10 && _width != 12
					&& _width != 14 && _width != 16 && _width != 18 && _width != 20)
				this.board[_width][8] = new BoardField(FieldType.Locked);
			else
				this.board[_width][8] = new BoardField(FieldType.Empty);
			this.add(this.board[_width][8]);
		}

		for(int _width = 0;_width < DEFAULT_BOARD_WIDTH; _width++)
		{
			if (_width != 3 && _width != 5 && _width != 7 && _width != 9 && _width != 11
					&& _width != 13 && _width != 15 && _width != 17 && _width != 19 && _width != 21)
				this.board[_width][9] = new BoardField(FieldType.Locked);
			else
				this.board[_width][9] = new BoardField(FieldType.Empty);
			this.add(this.board[_width][9]);
		}

		for(int _width = 0;_width < DEFAULT_BOARD_WIDTH; _width++)
		{
			if (_width != 2 && _width != 4 && _width != 6 && _width != 8 && _width != 10
					&& _width != 12 && _width != 14 && _width != 16 && _width != 18
					&& _width != 20 && _width != 22)
				this.board[_width][10] = new BoardField(FieldType.Locked);
			else
				this.board[_width][10] = new BoardField(FieldType.Empty);
			this.add(this.board[_width][10]);
		}

		for(int _width = 0;_width < DEFAULT_BOARD_WIDTH; _width++)
		{
			if (_width % 2 == 0)
				this.board[_width][11] = new BoardField(FieldType.Locked);
			else
				this.board[_width][11] = new BoardField(FieldType.Empty);
			this.add(this.board[_width][11]);
		}

		for(int _width = 0;_width < DEFAULT_BOARD_WIDTH; _width++)
		{
			if (_width % 2 != 0)
				this.board[_width][12] = new BoardField(FieldType.Locked);
			else
				this.board[_width][12] = new BoardField(FieldType.Empty);
			this.add(this.board[_width][12]);
		}

		for(int _width = 0;_width < DEFAULT_BOARD_WIDTH; _width++)
		{
			if (_width != 9 && _width != 11 && _width != 13 && _width !=15)
				this.board[_width][13] = new BoardField(FieldType.Locked);
			else
				this.board[_width][13] = new BoardField(FieldType.Empty);
			this.add(this.board[_width][13]);
		}

		for(int _width = 0;_width < DEFAULT_BOARD_WIDTH; _width++)
		{
			if (_width != 10 && _width != 12 && _width != 14)
				this.board[_width][14] = new BoardField(FieldType.Locked);
			else
				this.board[_width][14] = new BoardField(FieldType.Empty);
			this.add(this.board[_width][14]);
		}

		for(int _width = 0;_width < DEFAULT_BOARD_WIDTH; _width++)
		{
			if (_width != 11 && _width != 13)
				this.board[_width][15] = new BoardField(FieldType.Locked);
			else
				this.board[_width][15] = new BoardField(FieldType.Empty);
			this.add(this.board[_width][15]);
		}

		for(int _width = 0;_width < DEFAULT_BOARD_WIDTH; _width++)
		{
			if (_width != 12)
				this.board[_width][16] = new BoardField(FieldType.Locked);
			else
				this.board[_width][16] = new BoardField(FieldType.Empty);
			this.add(this.board[_width][16]);
		}
	}
	private void addingPlayers(int playersCount)
	{
		switch (playersCount) {
			case 2:

				break;
			case 3:

				break;
			case 4:

				break;
			case 6:

				break;
		}
	}
}
