/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lnm_hearts.model;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;


/**
 *
 * @author NghiaTruongNgoc
 */
public class Card extends GameElement {
    /** The suit constant that indicates a club. */
    public final static int CLUB = 1;
    /** The suit constant that indicates a spade. */
    public final static int SPADE = 2;
    /** The suit constant that indicates a diamond. */
    public final static int DIAMOND = 3;
    /** The suit constant that indicates a heart. */
    public final static int HEART = 4;
    
    private final int value, suit;
    private int orientation = Hand.SOUTH;
    private boolean isUp = false, mouseOver = false, selected = false;
    private boolean toPlay = false;
    private Image upImage, downImage;
    private AffineTransform transform;
    
    /**
     * Creates a new <code>Card</code> from the indicated value and suit.
     * 
     * @param value The desired value. It must be between 1 and 13, inclusive. 
     * 1 represents an ace and 11-13 represent jack, queen, and king, respectively.
     * Once the value of a <code>Card</code> is assigned, it cannot be changed.
     * @param suit The desired suit. It must be one of the four <code>Card</code> constants.
     * Once the suit of a <code>Card</code> is assigned, it cannot be changed.
     */
    public Card(int value, int suit)
    {
        this.value = value;
        if (value < 1 || value > 13)
            throw new IllegalArgumentException("Value must be between 1 and 13, inclusive.");
        this.suit = suit;
        orientation = Hand.SOUTH;
        setSize(72, 96);
        
        char s;
        switch (suit)
        {
            case HEART: s = 'H';
                break;
            case DIAMOND: s = 'D';
                break;
            case SPADE: s = 'S';
                break;
            case CLUB: s = 'C';
                break;
            default: throw new IllegalArgumentException("Suit must be a valid " +
                    "<code>Card</code> suit constant: <code>CLUB</code>, <code>SPADE</code>, " +
                    "<code>DIAMOND</code>, or <code>HEART</code>.");
        }
        
        upImage = new ImageIcon("src/lnm_hearts/gui/images/" + Integer.toString(value) + s + ".png").getImage();

        if (upImage == null)
        {
            System.out.println("src/lnm_hearts/gui/images/" + Integer.toString(value) + s + ".png");
        }
        else
        {
            System.out.println("src/lnm_hearts/gui/images/" + Integer.toString(value) + s + ".png" + "   =====nghia dep trai");
        }
        downImage = new ImageIcon("src/lnm_hearts/gui/images/back.png").getImage();
        
        transform = new AffineTransform();
    }

    public boolean isToPlay() {
        return toPlay;
    }

    public void setToPlay(boolean toPlay) {
        this.toPlay = toPlay;
    }
    
    
    
    
    /**
     * Draws the visual representation of the <code>Card</code> object.
     * 
     * @param g The <code>Graphics</code> context on which the card is displayed.
     * If <code>(g == null)</code> returns true, nothing is drawn.
     */
    @Override
    public void paint(Graphics g)
    {
        if (g == null)
            return;
        
        Graphics2D g2 = (Graphics2D)g;
        
        switch (orientation)
        {
            case Hand.NORTH:
                //isUp = false;
                transform.setToTranslation(getX() + getWidth(), getY() + getHeight());
                transform.rotate(Math.PI);
                break;
            case Hand.EAST:
                //isUp = false;
                transform.setToTranslation(getX(), getY() + getWidth());
                transform.rotate(-Math.PI / 2);
                break;
            case Hand.SOUTH:
                isUp = true;
                transform.setToTranslation(getX(), getY());
                break;
            case Hand.WEST:
                //isUp = false;
                transform.setToTranslation(getX() + getHeight(), getY());
                transform.rotate(Math.PI / 2);
                break;
        }
        
        if (isUp)
            g2.drawImage(upImage, transform, null);
        else
            g2.drawImage(downImage, transform, null);
        
        if (mouseOver)
        {
            g2.setStroke(new BasicStroke(3));
            g2.setColor(Color.black);
            if (orientation == Hand.SOUTH || orientation == Hand.NORTH)
                g2.draw(new Rectangle2D.Float(getX(), getY(), getWidth(), getHeight()));
            else
                g2.draw(new Rectangle2D.Float(getX(), getY(), getHeight(), getWidth()));
        }
    }
    
    /**
     * Gets the value of the <code>Card</code>. 
     * It must be a number between 1 and 13 where 1 represents an ace and 
     * 11-13 represent jack, queen, and king, respectively.
     * 
     * @return the <code>Card</code>'s value.
     * @see hearts.Card#getPointValue()
     */
    public final int getValue()
    {
        return value;
    }
    
    /**
     * Gets the point value of the <code>Card</code>.
     * In accordance with the rules of Hearts, the queen of spades 
     * is worth 13 points and each heart is worth 1 point.
     * 
     * @return the <code>Card</code>'s point value, either 1 or 13.
     * @see hearts.Card#getValue()
     */
    public final int getPointValue()
    {
        return ((value == 12 && suit == SPADE) ? 13 : (suit == HEART ? 1 : 0));
    }
    
    /**
     * The unique index based on a <code>Card</code>'s value and suit.
     * This method is usually used for sorting purposes. A club has a unique index 
     * from 1-13, a spade 14-26, a diamond 27-39, and a heart 40-52.
     * 
     * @return the <code>Card</code>'s unique index, from 1 to 52.
     */
    public int getUniqueIndex()
    {
        int uniqueIndex = -1; // should never return -1
        switch (suit)
        {
            case CLUB: uniqueIndex = value;
                break;
            case SPADE: uniqueIndex = 13 + value;
                break;
            case DIAMOND: uniqueIndex = 26 + value;
                break;
            case HEART: uniqueIndex = 39 + value;
                break;
        }
        return uniqueIndex;
    }
    
    /**
     * Gets the suit of the <code>Card</code>. 
     * It must equal one of the suit constants: <code>CLUB</code>, 
     * <code>SPADE</code>, <code>DIAMOND</code>, or <code>HEART</code>.
     * 
     * @return the <code>Card</code>'s suit.
     */
    public final int getSuit()
    {
        return suit;
    }
    
    /**
     * Sets the <code>Card</code> as either up or down.
     * This value only affects its visual representation. A <code>Card</code> 
     * set down shows the basic back image and a <code>Card</code> set up shows the
     * image that indicates the value and suit.
     * 
     * @param isUp whether or not the <code>Card</code> is up.
     * @see hearts.Card#isUp()
     */
    public void setUp(boolean isUp)
    {
        this.isUp = isUp;
    }
    
    /**
     * Gets whether or not the card is up.
     * 
     * @return whether or not the <code>Card</code> is up.
     * @see hearts.Card#setUp(boolean)
     */
    public boolean isUp()
    {
        return isUp;
    }
    
    /**
     * Sets whether or not the mouse cursor is hovering over the <code>Card</code>.
     * If set to true, a thin black border is painted around the edge of the
     * <code>Card</code> to indicate its status.
     * 
     * @param mouseOver whether or not the mouse cursor is over the <code>Card</code>.
     * @see hearts.Card#isMouseOver()
     * @see hearts.Card#paint(Graphics)
     */
    public void setMouseOver(boolean mouseOver)
    {
        this.mouseOver = mouseOver;
    }
    
    /**
     * Gets whether or not the mouse cursor is hovering over the <code>Card</code>.
     * This is not calculated; it simply returns the value set by the 
     * <code>setMouseOver(boolean mouseOver)</code> method.
     * 
     * @return whether or not the mouse cursor is over the <code>Card</code>.
     * @see hearts.Card#setMouseOver(boolean)
     * @see hearts.Card#paint(Graphics)
     */
    public boolean isMouseOver()
    {
        return mouseOver;
    }
    
    /**
     * Sets the orientation of the <code>Card</code>.
     * This only affects its visual representation. The <code>Card</code> is 
     * displayed such that the angle of the card logically faces a human player 
     * placed in the given location at a card table.
     * 
     * @param orientation the desired orientation. Must be a valid position 
     * constant from the <code>Hand</code> class: <code>NORTH</code>, 
     * <code>EAST</code>, <code>SOUTH</code> or <code>WEST</code>.
     * @see hearts.Card#getOrientation()
     * @see hearts.Hand
     */
    public void setOrientation(int orientation)
    {
        if (!(orientation == Hand.SOUTH || orientation == Hand.NORTH ||
                orientation == Hand.EAST || orientation == Hand.WEST))
            throw new IllegalArgumentException("Orientation must be one of the " +
                    "valid position constants in the Hand class: NORTH, EAST, " +
                    "SOUTH, or WEST.");
        
        this.orientation = orientation;
        
    }
    
    /**
     * Gets the orientation of the <code>Card</code>.
     * This only affects its visual representation. The <code>Card</code> is 
     * displayed such that the angle of the card logically faces a human player 
     * placed in the given location at a card table. The default orientation 
     * is Hand.SOUTH.
     * 
     * @return orientation the set orientation. Must be a valid position 
     * constant from the <code>Hand</code> class: <code>NORTH</code>, 
     * <code>EAST</code>, <code>SOUTH</code> or <code>WEST</code>.
     * @see hearts.Card#setOrientation(int)
     * @see hearts.Hand
     */
    public int getOrientation()
    {
        return orientation;
    }
    
    /**
     * Sets whether or not the <code>Card</code> is selected.
     * This only affects its visual representation. When a <code>Card</code> 
     * is selected, it is moved in the forward direction 20 pixels. The forward 
     * direction is determined by its orientation. For example, a <code>Card</code> 
     * with a Hand.EAST orientation moves 20 pixels left when selected.
     * 
     * @param selected whether or not the <code>Card</code> is selected.
     * @see hearts.Card#isSelected()
     */
    public void setSelected(boolean selected)
    {
        if (this.selected != selected) // can only be selected once
        {
            this.selected = selected;
            switch (orientation)
            {
                case Hand.SOUTH: setY(getY() + (selected ? -20 : 20));
                    break;
                case Hand.NORTH: setY(getY() - (selected ? -20 : 20));
                    break;
                case Hand.WEST: setX(getX() + (selected ? 20 : -20));
                    break;
                case Hand.EAST: setX(getX() - (selected ? 20 : -20));
                    break;
            }
        }
    }
    
    /**
     * Whether or not the <code>Card</code> is selected.
     * The default value of selected is false.
     * 
     * @return whether or not the <code>Card</code> is selected.
     * @see hearts.Card#setSelected(boolean)
     */
    public boolean isSelected()
    {
        return selected;
    }
    
    /**
     * The textual representation of a <code>Card</code> object. It indicates 
     * the value, suit, and point value.
     * 
     * @return the <code>Card</code>'s textual representation.
     */
    @Override
    public String toString()
    {
        return "Card: [value=" + value + ", suit=" + suit + ", point value=" + getPointValue() +"]";
    }
    
    /**
     * Whether or not two <code>Card</code>s are equal.
     * Two <code>Card</code>s are considered equal if they share the same suit and value.
     * 
     * @param card the <code>Card</code> to compare to
     * @return whether or not the two <code>Card</code>s are equal.
     */
    @Override
    public boolean equals(Object card)
    {
        return (this.getSuit() == ((Card)card).getSuit() && this.getValue() == ((Card)card).getValue());
    }
    
    /**
     * The <code>Card</code>'s hash code.
     * 
     * @return the <code>Card</code>'s hash code.
     */
    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 53 * hash + this.value;
        hash = 53 * hash + this.suit;
        return hash;
    }
}
