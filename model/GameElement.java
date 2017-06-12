/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lnm_hearts.model;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author NghiaTruongNgoc
 */
public class GameElement {
    public final static int LEFT = -1, RIGHT = 1;
    
    private int width = 50, height = 50;
    private int x = 0, y = 0, direction = RIGHT;
    private Image image;
    
    public void paint(Graphics g)
    {
        if (getDirection() == RIGHT)
            g.drawImage(image, x, y, width, height, null);
        else
            g.drawImage(image, x + width, y, x, y + height,
            0, 0, image.getWidth(null), image.getHeight(null), null);
    }
    
    public void setImage(Image image)
    {
    	this.image = image;
    }
    
    public Image getImage()
    {
    	return image;
    }
    
    public void setX(int x)
    {
        this.x = x;
    }
    
    public int getX()
    {
        return x;
    }
    
    public void setY(int y)
    {
        this.y = y;
    }
    
    public int getY()
    {
        return y;
    }
    
    public void setLocation(int x, int y)
    {
        setX(x);
        setY(y);
    }
    
    public void setLocation(Point location)
    {
        setX((int)location.getX());
        setY((int)location.getY());
    }
    
    public Point getLocation()
    {
        return new Point(x, y);
    }
    
    public void setWidth(int width)
    {
    	this.width = width;
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public void setHeight(int height)
    {
    	this.height = height;
    }
    
    public int getHeight()
    {
        return height;
    }
    
    public void setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
    }
    
    public Dimension getSize()
    {
        return new Dimension(width, height);
    }
    
    public Rectangle getBounds()
    {
        return new Rectangle(x, y, width, height);
    }
    
    public void setDirection(int direction)
    {
        this.direction = direction;
    }
    
    public int getDirection()
    {
        return direction;
    }
}
