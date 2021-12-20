package com.example.trylmaproject.client;

import javax.swing.*;
import java.awt.*;
import java.security.SecureRandom;

public class BoardField extends JPanel
{
	public static final SecureRandom generator = new SecureRandom();
	public BoardField()
	{
		Color color = new Color(generator.nextInt(256),generator.nextInt(256), generator.nextInt(256));
		this.setBackground(color);
	}
}
