package com.example.trylmaproject.client;

import javax.swing.*;
import java.awt.*;
import java.security.SecureRandom;

public class BoardField extends JPanel
{
	public static final SecureRandom generator = new SecureRandom();
	private int playerPawn;
	public BoardField( FieldType type)
	{
		switch (type)
		{
			case Empty :
				this.setBackground(Color.WHITE);
				playerPawn = 0;
				break;

			case Locked :
				this.setBackground(Color.BLACK);
				playerPawn = 0;
				break;

//			case Blue ->
//					this.setBackground(Color.BLUE);
//			case Red ->
//					this.setBackground(Color.RED);
//			case Green ->
//					this.setBackground(Color.GREEN);
//			case Pink ->
//					this.setBackground(Color.PINK);
//			case Yellow ->
//					this.setBackground(Color.YELLOW);

		}


	}
}
