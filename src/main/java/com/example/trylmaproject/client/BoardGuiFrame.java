package com.example.trylmaproject.client;

import com.example.trylmaproject.server.Field;
import com.example.trylmaproject.server.Player;

import javax.swing.*;
import java.awt.*;

import static com.example.trylmaproject.client.BoardGuiPanel.colorForPlayer;

/**
 * Klasa implementująca Frame dla gry docelowej
 * @author Mateusz Teplicki, Karol Dzwonkowski
 * @see com.example.trylmaproject.client.Client
 */

public class BoardGuiFrame extends JFrame
{

	/**
	 * Elementy występujące w BoardGuiFrame
	 */

	private BoardGuiPanel panel;
	private JPanel extraPanel;
	private JTextArea communication;
	private JPanel playersList;
	private JButton skipButton;
	private TextField[] playersName;

	/**
	 * Zmienne sterujące rozgrywka
	 */

	private boolean breakSignal = false;
	private boolean turn = false;

	/**
	 * Tablica zawierająca po kolei nazwy graczy dodawane wraz z kolejnością wygrywania
	 */
	private String[] scoreTable;

	/**
	 * Informacja ilu graczy występuje w rozgrywce
	 */

	private int homManyPlayers;

	/**
	 * Tablica graczy
	 */

	private Player[] players;

	/**
	 * Konstruktor Frame'a dla planszy. Konstruktor wywołuje niezbędne metody tworzące.
	 * @param playerNumber numer gracza dla którego implementowana jest plansza
	 * @param board tablica dwuwymiarowa która jest przesyłana przez server -> client
	 * @param players tablica graczy
	 */

	public BoardGuiFrame(int playerNumber, Field[][] board,Player[] players)
	{
		this.players = players;
		this.setLayout(new BorderLayout());
		homManyPlayers = 0;

		//Ustawianie oraz dodanie panelu planszy
		panel = new BoardGuiPanel(board, playerNumber);
		this.add(panel,BorderLayout.CENTER);

		//Ustawienie oraz dodanie panelu dodatkowego
		setExtraPanel();
		this.add(extraPanel,BorderLayout.EAST);

		//Ustawienie podstawowych własności Frame'a
		scoreTable = new String[6];
		setDefaultCloseOperation(3);
		setTitle("Warcaby gracz: " + playerNumber);
		setVisible(true);
		setLocationRelativeTo(null);
		pack();
	}

	/**
	 * Metoda inicjująca oraz ustawiająca panel dodatkowy
	 */
	public void setExtraPanel()
	{
		//Ustawienie podstawowych własności panelu
		extraPanel = new JPanel();
		extraPanel.setLayout(new BorderLayout());
		extraPanel.setPreferredSize(new Dimension(250,1000));
		extraPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		//Ustawienie podstawowych własności panelu zawierającego listę graczy
		playersName =  new TextField[players.length];
		playersList = new JPanel(new GridLayout(players.length,1));
		playersList.setBackground(Color.WHITE);
		playersList.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		//Inicjowanie kolejnych TextField'ów dla poszczególnych graczy oraz dopasowanie odpowiadających im kolorów
		int i = 0;
		while(i<6 && players[i] != null) {
			playersName[i] = new TextField(players[i].name);
			playersName[i].setForeground(colorForPlayer(players[i].number));
			playersName[i].setEditable(false);
			playersName[i].setBackground(Color.WHITE);
			playersList.add(playersName[i]);
			i++;
		}

		extraPanel.add(playersList,BorderLayout.NORTH);

		//Ustawienie podstawowych własności TextArea podpowiadającej za podstawową komunikację z użytkownikiem
		communication = new JTextArea();
		communication.setEditable(false);
		communication.setLineWrap(true);
		communication.setText("Gra rozpoczęta\n");
		communication.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		extraPanel.add(communication,BorderLayout.CENTER);

		//Inicjowanie przycisku zakończenia tury wraz z jego nasłuchiwaniem
		skipButton = new JButton("Skończ turę");
		skipButton.addActionListener(event ->
		{
			if(turn){
				breakSignal = true;
				synchronized (panel){
					panel.notify();
				}
			}
			else JOptionPane.showConfirmDialog(null,"To nie twoja kolejka!", "ERROR", JOptionPane.ERROR_MESSAGE);

		});
		extraPanel.add(skipButton,BorderLayout.SOUTH);
	}

	/**
	 * Funkcja odświeżająca plansze
	 * @param board plansza otrzymana od server -> client do ponownego narysowania
	 */
	public void boardRepaint(Field[][] board)
	{
		panel.panelRepaint(board);
	}

	/**
	 * Metoda dodająca kolejnego gracza, który ukończył rozgrywkę
	 * oraz informująca użytkownika o tym przy użyciu metody winnerCommunicat
	 * @param winner nazwa kolejnego gracza, który ukończył rozgrywkę
	 */
	public void whoWinner(String winner)
	{
		scoreTable[homManyPlayers] = winner;
		homManyPlayers++;
		winnerCommunicat(winner);
	}

	/**
	 * Metoda informująca użytkownika o kolejnym graczu, który ukończył rozgrywkę
	 * przy pomocy TextArea communication
	 * @see communication
	 * @param winner nazwa kolejnego gracza, który ukończył rozgrywkę
	 */
	private void winnerCommunicat(String winner)
	{
		communication.append("Gracz " + winner + "zajął " + homManyPlayers + "miejsce!!!\n");
	}

	/**
	 * Metoda ustawiająca zmienną turn odpowiedzialną za kolejkę
	 * @param turn zmienna informująca czy jest ruch użytkownika
	 */
	public void setTurn(boolean turn)
	{
		this.turn = turn;
		panel.setIsYourTurn(turn);
		communication.append("Twoja tura!\n");
	}

	/**
	 * Metoda ustawiająca tablicę graczy
	 * @param players tablica graczy otrzymana od server -> client
	 */
	public void setPlayerList(Player[] players){
		this.players = players;
	}

	/**
	 * Metoda wywoływana, gdy wszyscy gracze ukończą rozgrywkę
	 * @param losser nazwa gracza, który jako ostatni ukończył grę.
	 */
	public void endGame(String losser)
	{
		scoreTable[homManyPlayers] = losser;
		JOptionPane.showConfirmDialog(this, "Koniec gry\n 1." + scoreTable.toString());
	}

	/**
	 * Metoda umożliwiająca klientowi zebranie informacji o ruchu gracza
	 * @return Komunikat dla client -> server
	 */
	public String getMessage() {
		while(true){
			try {
				synchronized (this){
					wait(100);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(breakSignal) {
				breakSignal = false;
				return "BREAK";
			}
			else if(panel.moveSignal){
				panel.moveSignal = false;
				return panel.getMoveFromPanel();
			}
		}
	}
}


