/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lnm_hearts.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author NghiaTruongNgoc
 */
public class Hand extends ArrayList<Card>{
    public static final int NORTH = 1, EAST = 2, SOUTH = 3, WEST = 4;
    
    private final int cardGap = 22;
    private int position;
    private RoundRectangle2D border;
    private boolean active = false;
    private Dimension screenSize;
    private int points = 0, roundPoints = 0;
    private boolean showPoints = false;
    
    public Hand(Dimension d)
    {
        this(SOUTH, d);
    }
    
    public Hand(int position, Dimension d)
    {
        this.position = position;
        screenSize = d;
        
        switch (position)
        {
            case NORTH: border = new RoundRectangle2D.Float(d.width/6.0f, 5.0f,
                    d.height * 2.0f/3.0f, 100.0f, 30.0f, 30.0f);
                break;
            case EAST: border = new RoundRectangle2D.Float(d.width - 105.0f,
                    d.width/6.0f, 100.0f, d.width * 2.0f/3.0f, 30.0f, 30.0f);
                break;
            case SOUTH: border = new RoundRectangle2D.Float(d.width/6.0f,
                    d.height - 105.0f, d.height * 2.0f/3.0f, 100.0f, 30.0f, 30.0f);
                break;
            case WEST: border = new RoundRectangle2D.Float(5.0f, d.width/6.0f,
                    100.0f, d.width * 2.0f/3.0f, 30.0f, 30.0f);
                break;
            default: throw new IllegalArgumentException("Position must be one " +
                    "of the following valid constants: NORTH, EAST, SOUTH, or WEST");
        }
    }
    
    public void paint(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        
        if (showPoints && this.size() == 0)
        {
            g2.setFont(new Font("Calibri", Font.BOLD, 22));
            g2.setColor(Color.white);
            int x = (position == NORTH || position == SOUTH ?
                screenSize.width / 2 - 60 : (position == EAST ? 5 : screenSize.width - 130));
            int y = (position == WEST || position == EAST ?
                screenSize.height / 2 - 10 : (position == NORTH ? 20 : screenSize.height - 40));
            g2.drawString("Round score:", x, y);
            g2.drawString(Integer.toString(roundPoints), x, y + 25);
        }
        else
        {
            g2.setStroke(new BasicStroke(2));

            if (active)
                g2.setPaint(new GradientPaint((float)border.getX(), (float)border.getY(), new Color(90, 230, 30),
                    (float)(border.getX() + border.getWidth()), (float)(border.getY() + border.getHeight()), new Color(90, 230, 30, 180)));
            else
                g2.setPaint(new GradientPaint((float)border.getX(), (float)border.getY(), new Color(46, 184, 0),
                    (float)(border.getX() + border.getWidth()), (float)(border.getY() + border.getHeight()), new Color(0, 184, 46, 0)));
            g2.fill(border);

            if (active)
                g2.setColor(Color.white);
            else
                g2.setColor(Color.blue);
            g2.draw(border);

            switch (position)
            {
                case NORTH: //intentional fall-through
                case EAST:
                    for (int i = this.size() - 1; i >= 0; i--)
                        this.get(i).paint(g);
                    break;

                case SOUTH: //intentional fall-through
                case WEST:
                    for (int i = 0; i < this.size(); i++)
                        this.get(i).paint(g);
                    break;
            }
        }
    }
    
    @Override
    public boolean add(Card card)
    {
        super.add(card);
        
        card.setOrientation(position);
        arrangeCards();
        
        return true;
    }
    
    @Override
    public boolean remove(Object card)
    {
        super.remove(card);
        arrangeCards();
        
        return true;
    }
    
    public Card[] getSelectedCards()
    {
        int numSelected = 0;
        for (Card c : this)
            if (c.isSelected())
                numSelected++;
        Card[] cards = new Card[numSelected];
        
        int curr = 0;
        for (Card c : this)
        {
            if (c.isSelected())
            {
                cards[curr] = c;
                curr++;
            }
        }
        
        return cards;
    }
    
    public void sort()
    {
        Collections.sort(this, new CardComparer());
        if (position == NORTH || position == EAST)
            Collections.reverse(this);
        
        arrangeCards();
    }
    
    public boolean containsSuit(int suit)
    {
        for (Card c : this)
            if (c.getSuit() == suit)
                return true;
        return false;
    }
    
    private void arrangeCards()
    {
        switch (position)
        {
            case NORTH:
                for (int i = 0; i < this.size(); i++)
                    this.get(i).setLocation(screenSize.width / 2 -
                            (this.get(i).getWidth() / 2) -
                            (this.size() * cardGap / 2) + ((i+1/2) * cardGap), 7);
                break;
            case SOUTH:
                for (int i = 0; i < this.size(); i++)
                    this.get(i).setLocation(screenSize.width / 2 -
                            (this.get(i).getWidth() / 2) -
                            (this.size() * cardGap / 2) + ((i+1/2) * cardGap),
                            screenSize.height - 7 - this.get(i).getHeight());
                break;
            case WEST:
                for (int i = 0; i < this.size(); i++)
                    this.get(i).setLocation(7, screenSize.height / 2 -
                            (this.get(i).getHeight() / 2) -
                            (this.size() * cardGap / 2) + ((i+1/2) * cardGap));
                break;
            case EAST:
                for (int i = 0; i < this.size(); i++)
                    this.get(i).setLocation(screenSize.width - 7 - this.get(i).getHeight(),
                            screenSize.height / 2 - (this.get(i).getHeight() / 2) -
                            (this.size() * cardGap / 2) + ((i+1/2) * cardGap));
                break;
        }
    }
    
    public void setActive(boolean active)
    {
        this.active = active;
    }
    
    public boolean isActive()
    {
        return active;
    }
    
    public int getPosition()
    {
        return position;
    }
    
    public void setPoints(int points)
    {
        this.points = points;
    }
    
    public int getPoints()
    {
        return points;
    }
    
    public void setRoundPoints(int roundPoints)
    {
        this.roundPoints = roundPoints;
    }
    
    public int getRoundPoints()
    {
        return roundPoints;
    }
    
    public void showPoints()
    {
        showPoints = true;
    }
    
    class CardComparer implements Comparator<Card>
    {
        public int compare(Card c1, Card c2)
        {
            if ((c1.getValue() == 1 || c2.getValue() == 1))
                if (c1.getSuit() == c2.getSuit())
                    return (c1.getValue() == 1) ? 1 : -1;
            return (c1.getUniqueIndex() > c2.getUniqueIndex()) ? 1 : -1;
        }
    }
}
