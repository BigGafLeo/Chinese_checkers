package com.example.trylmaproject.client.test;

import com.example.trylmaproject.client.BoardGuiFrame;

import javax.swing.*;
import java.awt.*;

public class BordGuiTest {
	public static void main(String[] args) {
		EventQueue.invokeLater(()->{
			JFrame frame = new BoardGuiFrame(2);
			frame.setDefaultCloseOperation(3);
			frame.setTitle("Test");
			frame.setVisible(true);
			frame.setLocationRelativeTo((Component) null);
		});
	}
}
