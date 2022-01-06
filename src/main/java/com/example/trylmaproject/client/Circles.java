package com.example.trylmaproject.client;

import javafx.scene.shape.Circle;

import javax.swing.*;
import java.awt.*;

public class Circles extends JPanel {
    Circle circle;
    private Color color;

    public Circles(int x, int y, int radius)
    {
        circle = new Circle(x,y,radius);
        setBackground(Color.WHITE);
    }
    public boolean isHit(int x, int y)
    {
        return circle.getBoundsInLocal().contains(x,y);
    }
}
