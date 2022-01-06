package com.example.trylmaproject.client.test;

import com.example.trylmaproject.client.BoardGuiFrame;
import com.example.trylmaproject.client.QueFrame;

import javax.swing.*;
import java.awt.*;

public class QueGuiTest {
    public static void main(String[] args) {
        EventQueue.invokeLater(()->{
            JFrame frame = new QueFrame(1);
            frame.setDefaultCloseOperation(3);
            frame.setTitle("Test");
            frame.setVisible(true);
            frame.setLocationRelativeTo((Component) null);
        });
    }
}
