/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lnm_hearts.model;

import java.awt.Graphics;
import java.awt.Point;

/**
 *
 * @author NghiaTruongNgoc
 */
public class Deck {
    public final static int SHUFFLE_LIGHT = 100, SHUFFLE_REGULAR = 500, 
                            SHUFFLE_HEAVY = 1000;
    
    private Card[] deck;
    
    public Deck()
    {
        deck = new Card[52];
        for (int i = 0; i < 52; i++)
        {
            int value = i%13 + 1;
            int suit;
            switch ((int)(i/13))
            {
                case 0: suit = Card.HEART;
                    break;
                case 1: suit = Card.DIAMOND;
                    break;
                case 2: suit = Card.SPADE;
                    break;
                case 3: suit = Card.CLUB;
                    break;
                default: suit = Card.HEART;
            }
            deck[i] = new Card(value, suit);
            //deck[i].setUp(false);
        }
    }
    
    public void paint(Graphics g)
    {
        for (Card c : deck)
            c.paint(g);
    }
    
    public Card get(int index)
    {
        if (index < 0 || index > deck.length - 1)
            throw new IllegalArgumentException("Index must be an integer between 0 and 52, inclusive.");
        else
            return deck[index];
    }
    
    public int size()
    {
        return deck.length;
    }
    
    public synchronized void shuffle(int shuffleType)
    {
        if (!(shuffleType == SHUFFLE_LIGHT || shuffleType == SHUFFLE_REGULAR ||
            shuffleType == SHUFFLE_HEAVY))
        {
            throw new IllegalArgumentException("Shuffle type must be one of " +
                    "the following valid Deck constants: (SHUFFLE_LIGHT, " +
                    "SHUFFLE_REGULAR, or SHUFFLE_HEAVY");
        }
        for (int i = 0; i < shuffleType; i++)
        {
            int r1 = (int)(Math.random() * deck.length);
            int r2 = (int)(Math.random() * deck.length);
            swap(r1, r2);
        }

        for (int i = 0; i < deck.length; i++)
        {
            System.out.println("shuffle");
            deck[i].setUp(false);
            deck[i].setToPlay(false);
        }
    }
    
    private synchronized void swap(int c1, int c2)
    {
        Card temp = deck[c1];
        Point p = deck[c1].getLocation();
        deck[c1] = deck[c2];
        deck[c1].setLocation(deck[c2].getLocation());
        deck[c2] = temp;
        deck[c2].setLocation(p);
    }
}
