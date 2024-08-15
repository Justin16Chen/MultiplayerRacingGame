package gameplay;

import java.awt.*;
import java.awt.geom.*;

public class PlayerSprite {
    
    public int playerID;
    private double x, y;
    private int width, height;
    private Color color;
    private double speed = 5;

    public PlayerSprite(double x, double y, int width, int height, Color color) {
        this.x = x - width * 0.5;
        this.y = y - height;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public void drawSprite(Graphics2D g) {
        Rectangle2D.Double rect = new Rectangle.Double(x, y, width, height);
        g.setColor(color);
        g.fill(rect);
    }

    public void moveX(double n) {
        x += n;
    }
    public void moveY(double n) {
        y += n;
    }
    public void setX(double n) {
        x = n;
    }
    public void setY(double n) {
        y = n;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public double getSpeed() {
        return speed;
    }
}