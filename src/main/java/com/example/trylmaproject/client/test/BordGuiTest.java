package com.example.trylmaproject.client.test;

import com.example.trylmaproject.client.BoardGuiFrame;
import com.example.trylmaproject.exceptions.IllegalNumberOfPlayers;
import com.example.trylmaproject.server.Board;
import com.example.trylmaproject.server.Field;

import javax.swing.*;
import java.awt.*;

public class BordGuiTest {
	public static void main(String[] args) {
		EventQueue.invokeLater(()->{
			Board board = null;
			try {
				board = new Board(2);
			} catch (IllegalNumberOfPlayers e) {
				e.printStackTrace();
			}
			JFrame frame = new BoardGuiFrame(1,board);
			frame.setDefaultCloseOperation(3);
			frame.setTitle("Test");
			frame.setVisible(true);
			frame.setLocationRelativeTo((Component) null);
		});
	}
}
