package com.example.trylmaproject.client;

import javax.swing.*;
import java.awt.*;

/**
 * Klasa Implementująca okno oczekiwania na graczy przed grą
 * Umożliwia ono wybranie nazwy gracza
 * @author Mateusz Teplicki, Karol Dzwonkowski
 */
public class QueFrame extends JFrame {

    /**
     * Komponenty występujące w QueFrame
     */
    private JPanel quePanel;
    private JButton checkName;
    private JButton startGame;
    private JTextField playerName;
    private JTextField textField;

    /**
     * Zmienne sterujące rozgrywka
     */
    private boolean readyToPlay;
    private boolean nameReadyToSend = false;
    private String nameToServer;

    /**
     * Numer przypisany do gracza
     */
    private int playerNumber;

    /**
     * Predefiniowane wielkości okna
     */
    int DEFAULT_WIDTH = 400;
    int DEFAULT_HEIGHT = 200;

    /**
     * Konstruktor Frame'a dla kolejki oczekiwania.
     * Konstruktor wywołuje niezbędne metody tworzące.
     * @param playerNumber
     */
    public QueFrame(int playerNumber)
    {
        this.playerNumber = playerNumber;
        this.setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);
        this.setResizable(false);

        //Wywołanie metody przygotowującej panel
        setQuePanel();
        this.getContentPane().add(this.quePanel);

        pack();
    }

    /**
     * Metoda przygotowująca panel do dodania go do Frame'a.
     * Wywołuje metodę tworzącą nasłuch dla przycisków.
     */
    private void setQuePanel()
    {
        quePanel = new JPanel();
        quePanel.setLayout(new GridLayout(4,1));

        //Przygotowanie pola z tekstem
        textField = new JTextField("Wprowadź swoją nazwę:");
        textField.setEditable(false);
        quePanel.add(textField);

        //Przygotowanie pola, w którym gracz wpisuje swoją nazwę
        playerName = new JTextField();
        playerName.setEditable(true);
        quePanel.add(playerName);

        //Przygotowanie przycisku zatwierdzenia nazwy gracza
        checkName = new JButton();
        checkName.setSize(quePanel.getWidth(),checkName.getHeight());
        checkName.setText("Zatwierdź nazwę gracza");
        quePanel.add(checkName);

        //Przygotowanie przycisku rozpoczęcia rozgrywki,
        //Jeżeli gracz jest pierwszy ma przycisk funkcyjny.
        startGame = new JButton();
        if(playerNumber == 1)
            startGame.setText("Rozpoczni gre");
        else {
            startGame.setText("Oczekiwanie na hosta");
            startGame.setEnabled(false);
        }
        startGame.setSize(quePanel.getWidth(),startGame.getHeight());
        quePanel.add(startGame);

        setButtons();
    }

    /**
     * Metoda przygotowująca nasłuchwiacze do przycisków.
     */
    private void setButtons()
    {

        //Przycisk zatwierdzenia imienia
        checkName.addActionListener(event ->
        {
            nameToServer = playerName.getText();
            nameReadyToSend = true;
            synchronized (this){
                notify();
            }
        });

        //Przycisk rozpoczęcia gry dla gracza nr 1
        if(playerNumber == 1)
            startGame.addActionListener(event ->
            {
                readyToPlay = true;
                synchronized (this){
                    notify();
                }
            });
    }

    /**
     * Metoda zwracająca nazwę do serwera
     * @return zwraca nazwę do serwera
     */
    public String getNameToServer() {
        return nameToServer;
    }

    /**
     * Metoda budzi klienta i zezwala na pobranie informacji o imieniu
     * @return wartość dla nasłuchiwacza informująca o gotowości do pobrania imienia
     */
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

    /**
     * Metoda budzi klioenta i zezwala na pobranie informacji o rozpoczęciu gry
     * @return wartość dla nasłuchiwacza informująca o gotowości do gry
     */
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
