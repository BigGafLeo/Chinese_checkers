package com.example.trylmaproject.client;

import javax.swing.*;
import java.awt.*;
//TODO Naprawić grafikę i ustawić wszystko w jednej lini
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
    private ImagePanel imagePanel;
    private JButton checkName;
    private JButton startGame;
    private JTextField playerName;
    private String nameToServer;
    private boolean nameReadyToSend = false;
    private int playerNumber;
    private boolean readyToPlay;
    int DEFAULT_WIDTH = 938;
    int DEFAULT_HEIGHT = 800;


    public QueFrame(int playerNumber)
    {
        this.setLayout(new BorderLayout());

        this.playerNumber = playerNumber;
        this.setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);

        imagePanel = new ImagePanel();
//
        add(imagePanel,BorderLayout.NORTH);
        imagePanel.repaint();

        setQuePanel();
        this.getContentPane().add(this.quePanel,BorderLayout.CENTER);

        pack();

    }
    private void setQuePanel()
    {
        quePanel = new JPanel();
        quePanel.setLayout(new BoxLayout(quePanel,3));

        playerName = new JTextField();
        playerName.setText("Wprowadź swoją nazwę");
        playerName.setEditable(true);
        quePanel.add(playerName);

        checkName = new JButton();
        checkName.setText("Zatwierdź nazwę gracza");
        quePanel.add(checkName);

        startGame = new JButton();
        if(playerNumber == 1)
            startGame.setText("Rozpoczni gre");
        else
            startGame.setText("Oczekiwanie na hosta");
        quePanel.add(startGame);

        setButtons();
    }

    private void setButtons()
    {
        /**
         * Ustawienie actionlistnerów dla przycisków w gui z uwzględnieniem pierwszego gracza który ma aktywny przycisk początku rozgrywki.
         */
        checkName.addActionListener(event ->
        {
            nameToServer = playerName.getText();
            nameReadyToSend = true;
            synchronized (this){
                notify();
            }
        });
        if(playerNumber == 1)
            startGame.addActionListener(event ->
            {
                readyToPlay = true;
                synchronized (this){
                    notify();
                }
            });
        else
            startGame.addActionListener(event ->
            {
                JOptionPane.showMessageDialog(this,"Jedynie gracz nr 1 może rozpocząć grę.");
            });
    }

    public String getNameToServer() {
        return nameToServer;
    }

    public boolean isNameReadyToSend() {
        synchronized (this){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return nameReadyToSend;
    }

    public boolean isReadyToPlay() {
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return readyToPlay;
    }
}
class ImagePanel extends JPanel
{
    private Image image;
    int IMAGE_WIDTH;
    int IMAGE_HIGHT;
    public ImagePanel()
    {
        image = new ImageIcon("Title.png").getImage();
    }
    public void paintComponent(Graphics g)
    {
        if(image == null)
            return;

        IMAGE_WIDTH = image.getWidth(this);
        IMAGE_HIGHT = image.getHeight(this);
        g.drawImage(image,0,0,null);
    }
    public Dimension getPreferedSize()
    {
        return new Dimension(IMAGE_WIDTH,IMAGE_HIGHT);
    }
}