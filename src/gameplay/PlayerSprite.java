package gameplay;

import java.awt.*;
import java.awt.geom.*;

public class PlayerSprite {
    
    private double x, y, width, height;
    private Color color;
    private double speed = 5;

    public PlayerSprite(double x, double y, double width, double height, Color color) {
        this.x = x;
        this.y = y;
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
    public double getSpeed() {
        return speed;
    }
}