package com.example.trylmaproject.client;

import java.awt.geom.Ellipse2D;

/**
 * Klasa odpowiedzialna za graficzne pola lub pionki.
 * @author Karol Dzwonkowski, Mateusz Teplicki
 */
public class DrawnCircles
{
	//Elementy pól/pionków
	private Ellipse2D.Double drawnCircle;
	private static int DEFAULT_RADIUS = 40;

	//Informacja o pionku znajdującym się na planszy lub jego braku == 0
	private int playerNumber;

	/**
	 * Konstruktor klasy
	 * @param playerNumber numer gracza zajmującego pole lub jego braku == 0
	 */
	public DrawnCircles(int playerNumber)
	{
		this.playerNumber = playerNumber;
	}

	/**
	 * Metoda ustawiająca numer gracza zajmującego pole lub jego braku == 0
	 * @param playerNumber numer gracza
	 */
	public void setPlayerNumber(int playerNumber)
	{
		this.playerNumber = playerNumber;
	}

	/**
	 * Metoda ustawiająca wymiar pionków
	 * @param _DEFAULT_RADIUS nowy wymiar pionków
	 */
	public static void setDefaultRadius(int _DEFAULT_RADIUS)
	{
		DEFAULT_RADIUS = _DEFAULT_RADIUS;
	}

	/**
	 * Metoda zwracająca wymiar pionków
	 * @return wymiar pionków
	 */
	public static int getDefaultRadius()
	{
		return DEFAULT_RADIUS;
	}

	/**
	 * Metoda inicjująca drawnCircle oraz go zwracająca
	 * @param posY pozycja Y lewego górnego pkt koła (kwadratu)
	 * @param posX pozycja X lewego górnego pkt koła (kwadratu)
	 * @return narysowany pionek
	 */
	public Ellipse2D.Double fieldDrawing(double posY, double posX)
	{
		drawnCircle = new Ellipse2D.Double(posX,posY,DEFAULT_RADIUS,DEFAULT_RADIUS);
		return drawnCircle;
	}

	/**
	 * Metoda zwracająca narysowany pionek
	 * @return narysowany pionek
	 */
	public Ellipse2D.Double getCircle()
	{
		return drawnCircle;
	}

	/**
	 * Metoda zwracająca numer gracza zajmującego pole lub jego braku == 0
	 * @return numer gracza zajmującego pole lub jego braku == 0
	 */
	public int getPlayerNumber()
	{
		return playerNumber;
	}


}
