package com.example.trylmaproject.client;

import javax.swing.*;

public class QueFrame extends JFrame {
//    private JPanel quePanel;
//
//    public QueFrame()
//    {
//        this.quePanel = new QuePanel();
//        this.getContentPane().add(this.quePanel);
//        pack();
//    }
    private JPanel quePanel;
    private JButton checkName;
    private JButton startGame;
    private JTextField gameName;
    private JTextField playerName;
    private String nameToServer;
    private boolean nameReadyToSend = false;
    private int playerNumber;
    private boolean readyToPlay;
    int DEFAULT_WIDTH =600;
    int DEFAULT_HEIGHT = 800;

    public QueFrame(int playerNumber)
    {
        this.playerNumber = playerNumber;

        quePanel = new JPanel();
        quePanel.setLayout(new BoxLayout(quePanel,3));
        gameName = new JTextField();
        gameName.setText("Chinese - Checkers");
        gameName.setEditable(false);
        quePanel.add(gameName);
        playerName = new JTextField();
        playerName.setText("Wprowadź swoją nazwę");
        playerName.setEditable(true);
        quePanel.add(playerName);
        checkName = new JButton();
        checkName.setText("Zatwierdź nazwę gracza");
        quePanel.add(checkName);
        startGame = new JButton();
        startGame.setText("Rozpoczni gre");
        quePanel.add(startGame);

        this.setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);
        /**
         * Ustawienie actionlistnerów dla przycisków w gui z uwzględnieniem pierwszego gracza który ma aktywny przycisk początku rozgrywki.
         */
        checkName.addActionListener(event ->
        {
            nameToServer = playerName.getText();
            nameReadyToSend = true;
        });
        if(playerNumber == 1)
            startGame.addActionListener(event ->
            {
                readyToPlay = true;
            });
        else
            startGame.addActionListener(event ->
            {
                JOptionPane.showMessageDialog(this,"Jedynie gracz nr 1 może rozpocząć grę.");
            });

        /**
         * Dodanie przygotowanego panelu do frame'a.
         */
        this.getContentPane().add(this.quePanel);
        pack();

    }
    public String getNameToServer() {
        return nameToServer;
    }

    public boolean isNameReadyToSend() {
        return nameReadyToSend;
    }

    public boolean isReadyToPlay() {
        return readyToPlay;
    }
}
