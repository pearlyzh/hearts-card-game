/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lnm_hearts.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import lnm_hearts.model.Card;
import lnm_hearts.model.Deck;
import lnm_hearts.model.Hand;

/**
 *
 * @author NghiaTruongNgoc
 */
public class MainPanel extends JPanel implements MouseListener,
        MouseMotionListener, ActionListener {
    private static final Dimension DIM = new Dimension(650, 650);
    private JFrame parent;
    private JButton dealButton, swapButton, okButton;
    private Deck deck;
    private Hand[] hands;
    private int activeHand = -1;
    private ImageIcon[] arrowIcons;
    
    private int trickStarter = -1;
    private Card[] trick;
    private int[] roundScore;
    private boolean firstTrick = true;
    private boolean heartsBroken = false;
    private int trickWinner = -1;
    private int animateSpeed = 0;
    
    private Timer dealTimer, bubbleFadeTimer, animateTimer, handTimer;
    private int dealTimerIteration = 0;
    private boolean swapState = false;
    
    private TextBubble textBubble;
    private boolean showScore = false;
    
    public MainPanel(JFrame parent)
    {
        super();
        this.parent = parent;
        setPreferredSize(DIM);
        setBackground(new Color(0, 127, 0));
        addMouseListener(this);
        addMouseMotionListener(this);
        
        arrowIcons = new ImageIcon[4];
        arrowIcons[0] = new ImageIcon("images/arrowUp.png");
        arrowIcons[1] = new ImageIcon("images/arrowRight.png");
        arrowIcons[2] = new ImageIcon("images/arrowDown.png");
        arrowIcons[3] = new ImageIcon("images/arrowLeft.png");
        
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, DIM.height / 2 - 100));
        
        dealButton = new JButton("Deal", new ImageIcon("images/heartIcon.png"));
        dealButton.addActionListener(this);
        add(dealButton);
        
        swapButton = new JButton("Swap", arrowIcons[3]);
        swapButton.addActionListener(this);
        
        okButton = new JButton("Alright", new ImageIcon("images/thumbsup.png"));
        okButton.addActionListener(this);
        
        deck = new Deck();
        
        trick = new Card[4];
        roundScore = new int[4];
        
        textBubble = new TextBubble();
        
        hands = new Hand[4];
        hands[0] = new Hand(Hand.WEST, DIM);
        hands[1] = new Hand(Hand.NORTH, DIM);
        hands[2] = new Hand(Hand.EAST, DIM);
        hands[3] = new Hand(Hand.SOUTH, DIM);
        
        dealTimer = new Timer(60, this);
        bubbleFadeTimer = new Timer(60, this);
        bubbleFadeTimer.setInitialDelay(1000);
        animateTimer = new Timer(10, this);
        animateTimer.setInitialDelay(1000);
        handTimer = new Timer(800, this);
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        textBubble.paint(g2);
        for (Hand h : hands)
            h.paint(g2);
        
        if (showScore)
        {
            g2.setColor(Color.white);
            g2.setFont(new Font("Calibri", Font.BOLD, 28));
            g2.drawString("West: " + hands[0].getPoints(), 200, 350);
            g2.drawString("North: " + hands[1].getPoints(), 200, 380);
            g2.drawString("East: " + hands[2].getPoints(), 200, 410);
            g2.drawString("You: " + hands[3].getPoints(), 200, 440);
        }
    }
    
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource().equals(dealButton))
        {
            showScore = false;
            remove(dealButton);
            repaint();
            deck.shuffle(Deck.SHUFFLE_REGULAR);
            dealTimer.start();
        }
        else if (e.getSource().equals(swapButton))
        {
            if (hands[activeHand].getSelectedCards().length == 3)
            {
                hands[activeHand].setActive(false);
                activeHand = -1;
                
                Card[] playerReceivingCards = hands[2].getSelectedCards();
                for (int i = 0; i < hands.length; i++)
                {
                    Card[] selectedCards = hands[i].getSelectedCards();
                    int destination = (i == 3 ? 0 : i + 1);
                    for (Card c : selectedCards)
                    {
                        c.setSelected(false);
                        c.setMouseOver(false);
                        hands[i].remove(c);
                        hands[destination].add(c);
                    }
                }
                
                for (Hand h : hands)
                    h.sort();
                
                for (Card c : playerReceivingCards)
                    c.setSelected(true);
                
                remove(swapButton);
                swapState = false;
                add(okButton);
                validate();
                
                repaint();
            }
        }
        else if (e.getSource().equals(dealTimer))
        {
            deck.get(dealTimerIteration).setUp(true);
            hands[dealTimerIteration % 4].add(deck.get(dealTimerIteration));
            repaint();
            dealTimerIteration++;
            
            if (dealTimerIteration == 52)
            {
                dealTimer.stop();
                dealTimerIteration = 0;
                
                for (int i = 0; i < hands.length; i++)
                    hands[i].sort();
                
                hands[3].setActive(true);
                activeHand = 3;
                swapState = true;
                add(swapButton);
                swapButton.setEnabled(false);
                this.validate();
                for (int i = 0; i < 3; i++)
                {
                    int[] cardsToSwap = cardsToSwap(i);
                    for (int k = 0; k < 3; k++)
                        hands[i].get(cardsToSwap[k]).setSelected(true);
                }
            }
        }
        else if (e.getSource().equals(okButton))
        {
            remove(okButton);
            validate();
            
            for (Card c : hands[3])
                c.setSelected(false);
            
            for (int i = 0; i < hands.length; i++)
            {
                if ((hands[i].get(0).getValue() == 2 && hands[i].get(0).getSuit() == Card.CLUB) ||
                        (hands[i].get(hands[i].size() - 1).getValue() == 2 &&
                        hands[i].get(hands[i].size() - 1).getSuit() == Card.CLUB))
                {
                    if (i == 3)
                    {
                        activeHand = i;
                        hands[i].setActive(true);
                    }
                    else
                    {
                        trickStarter = i;
                        for (int k = i; k != 3; k++)
                        {
                            activeHand = k;
                            Card c = hands[k].get(cardToPlay(k));
                            
                            trick[k] = c;
                            if (!heartsBroken && c.getPointValue() > 0)
                                heartsBroken = true;
                            
                            if (getError(c) != null)
                            {
                                System.out.println(getError(c));
                                JOptionPane.showConfirmDialog(null, "Congratulations! " +
                                        "You made the computer blow its brains out!\n" +
                                        "Because of your sadistic ways, the program will now close. " +
                                        "Have a nice day!",
                                        "Artificial Intelligence Error!",
                                        JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE);
                                System.exit(0);
                            }
                            
                            
                            
                            switch (hands[k].getPosition())
                            {
                                case Hand.SOUTH: // intentional fall-through
                                case Hand.NORTH:
                                    c.setLocation(DIM.width / 2 - (c.getWidth() / 2),
                                        DIM.height / 2 - c.getHeight() - (c.getWidth() / 2) - 12 +
                                        (hands[k].getPosition() == Hand.NORTH ? 0 : c.getHeight() + c.getWidth()));
                                    break;
                                case Hand.WEST: // intentional fall-through
                                case Hand.EAST:
                                    c.setLocation(DIM.width / 2 - c.getHeight() - (c.getWidth() / 2) +
                                            (hands[k].getPosition() == Hand.WEST ? 0 : c.getHeight() + c.getWidth()), 
                                            DIM.height / 2 - (c.getHeight() / 2));
                                    break;
                            }
                        }
                        activeHand = 3;
                        hands[3].setActive(true);
                    }
                    
                    break;
                }
            }
            
            repaint();
        }
        else if (e.getSource().equals(bubbleFadeTimer))
        {
            textBubble.setAlpha(textBubble.getAlpha() - 10);
            if (textBubble.getAlpha() <= 0)
            {
                textBubble.setAlpha(0);
                bubbleFadeTimer.stop();
            }
            
            repaint();
        }
        else if (e.getSource().equals(animateTimer))
        {
            boolean shouldStop = true;
            switch (trickWinner)
            {
                case 1: // intentional fall-through
                case 3:
                    for (Card c : trick)
                        c.setY(c.getY() + (trickWinner == 1 ? -animateSpeed : animateSpeed));
                    if (trickWinner == 1)
                    {
                        if (trick[3].getY() + trick[3].getHeight() > 0)
                            shouldStop = false;
                    }
                    else
                    {
                        if (trick[1].getY() < DIM.height)
                            shouldStop = false;
                    }
                    break;
                case 0: // intentional fall-through
                case 2:
                    for (Card c : trick)
                        c.setX(c.getX() + (trickWinner == 0 ? -animateSpeed : animateSpeed));
                    if (trickWinner == 0)
                    {
                        if (trick[2].getX() + trick[2].getHeight() > 0)
                            shouldStop = false;
                    }
                    else
                    {
                        if (trick[0].getX() < DIM.width)
                            shouldStop = false;
                    }
                    break;
            }
            animateSpeed += 6;
            if (shouldStop)
            {
                animateSpeed = 0;
                animateTimer.stop();
                
                for (int i = 0; i < hands.length; i++)
                {
                    hands[i].remove(trick[i]);
                    trick[i] = null;
                }
                
                if (hands[0].size() == 0)
                {
                    boolean shotTheMoon = false;
                    int moonShooter = -1;
                    for (int i = 0; i < roundScore.length; i++)
                    {
                        if (roundScore[i] == 26)
                        {
                            shotTheMoon = true;
                            moonShooter = i;
                            roundScore[i] = 0;
                        }
                    }
                    
                    if (shotTheMoon)
                    {
                        for (int i = 0; i < hands.length; i++)
                        {
                            if (i == moonShooter)
                                hands[i].setRoundPoints(0);
                            else
                            {
                                hands[i].setRoundPoints(26);
                                hands[i].setPoints(hands[i].getPoints() + 26);
                            }
                            hands[i].showPoints();
                        }
                    }
                    else
                    {
                        for (int i = 0; i < hands.length; i++)
                        {
                            hands[i].setRoundPoints(roundScore[i]);
                            hands[i].setPoints(hands[i].getPoints() + roundScore[i]);
                            hands[i].showPoints();
                            roundScore[i] = 0;
                        }
                    }
                    
                    firstTrick = true;
                    heartsBroken = false;
                    
                    showScore = true;
                    add(dealButton);
                    this.validate();
                    
                    repaint();
                    return;
                }
                
                if (trickWinner == 3)
                {
                    activeHand = trickWinner;
                    hands[activeHand].setActive(true);
                }
                else
                {
                    trickStarter = trickWinner;
                    for (int k = trickWinner; k != 3; k++)
                    {
                        activeHand = k;
                        Card c = hands[k].get(cardToPlay(k));

                        trick[k] = c;
                        if (!heartsBroken && c.getPointValue() > 0)
                            heartsBroken = true;

                        if (getError(c) != null)
                        {
                            System.out.println(getError(c));
                            JOptionPane.showConfirmDialog(null, "Congratulations! " +
                                    "You made the computer blow its brains out!\n" +
                                    "Because of your sadistic ways, the program will now close. " +
                                    "Have a nice day!",
                                    "Artificial Intelligence Error!",
                                    JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE);
                            System.exit(0);
                        }
                        
                        switch (hands[k].getPosition())
                        {
                            case Hand.SOUTH: // intentional fall-through
                            case Hand.NORTH:
                                c.setLocation(DIM.width / 2 - (c.getWidth() / 2),
                                    DIM.height / 2 - c.getHeight() - (c.getWidth() / 2) - 12 +
                                    (hands[k].getPosition() == Hand.NORTH ? 0 : c.getHeight() + c.getWidth()));
                                break;
                            case Hand.WEST: // intentional fall-through
                            case Hand.EAST:
                                c.setLocation(DIM.width / 2 - c.getHeight() - (c.getWidth() / 2) +
                                        (hands[k].getPosition() == Hand.WEST ? 0 : c.getHeight() + c.getWidth()), 
                                        DIM.height / 2 - (c.getHeight() / 2));
                                break;
                        }
                    }
                    activeHand = 3;
                    hands[3].setActive(true);
                }
            }
            
            repaint();
        }
        else if (e.getSource().equals(handTimer))
        {
            handTimer.stop();
            
            repaint();
        }
    }
    
    private int[] cardsToSwap(int hand)
    {
        ArrayList<Integer> cards = new ArrayList<Integer>(3);
        
        for (int i = 0; i < hands[hand].size(); i++)
            if (hands[hand].get(i).getSuit() == Card.SPADE &&
            (hands[hand].get(i).getValue() >= 12 || hands[hand].get(i).getValue() == 1))
                cards.add(i);
        
        // Look for high hearts, then diamonds, then clubs
        int[] targetSuits = {Card.HEART, Card.DIAMOND, Card.CLUB};
        for (int targetSuit : targetSuits)
        {
            // North and east hands are sorted backwards, so use different indices.
            int i = (hand == 0 ? hands[hand].size() - 1 : 0);
            int increment = (hand == 0 ? -1 : 1);
            for (; (hand == 0 ? i >= 0 : i < hands[hand].size()); i += increment)
            {
                if (hands[hand].get(i).getSuit() == targetSuit && 
                        (hands[hand].get(i).getValue() >= 12 || hands[hand].get(i).getValue() == 1))
                    cards.add(i);
            }
        }
        
        // Ensure that the computer has chosen at least 3 cards. If not,
        // cards from the end of the hand are added.
        if (cards.size() < 3)
        {
            // North and east hands are sorted backwards, so use different indices.
            int i = (hand == 0 ? hands[hand].size() - 1 : 0);
            int increment = (hand == 0 ? -1 : 1);
            while (cards.size() < 3)
            {
                if (!cards.contains(i)) // Can't pick the same card twice
                    cards.add(i);
                i += increment;
            }
        }
        
        Object[] a = cards.toArray();
        int[] cardsToSwap = new int[3];
        for (int i = 0; i < 3; i++)
            cardsToSwap[i] = (Integer)a[i];
        return cardsToSwap;
    }
    
    private int cardToPlay(int hand)
    {
        if (firstTrick)
        {
            if (activeHand == trickStarter)
            {
                for (int i = 0; i < hands[hand].size(); i++)
                {
                    if (hands[hand].get(i).getSuit() == Card.CLUB && 
                        hands[hand].get(i).getValue() == 2)
                        return i;
                }
            }
            else
            {
                int highClub = -1;
                int club = -1;
                for (int i = 0; i < hands[hand].size(); i++)
                {
                    if (hands[hand].get(i).getSuit() == Card.CLUB)
                    {
                        if (hands[hand].get(i).getValue() > highClub)
                        {
                            highClub = hands[hand].get(i).getValue();
                            club = i;
                        }
                    }
                }
                if (club != -1)
                    return club;
                
                int[] suits = {Card.DIAMOND, Card.SPADE, Card.HEART};
                for (int suit : suits)
                {
                    int high = -1;
                    int highValue = 0;
                    for (int i = 0; i < hands[hand].size(); i++)
                    {
                        if (hands[hand].get(i).getSuit() == suit &&
                                hands[hand].get(i).getValue() > highValue &&
                                hands[hand].get(i).getPointValue() != 13)
                        {
                            high = i;
                            highValue = hands[hand].get(i).getValue();
                        }
                    }
                    if (high != -1)
                        return high;
                }
            }
        }
        else
        {
            if (activeHand == trickStarter)
            {
                int[] suits = {Card.SPADE, Card.DIAMOND, Card.CLUB, Card.HEART};
                for (int suit : suits)
                {
                    int low = -1;
                    int lowValue = 100;
                    for (int i = 0; i < hands[hand].size(); i++)
                    {
                        if (hands[hand].get(i).getSuit() == suit &&
                                hands[hand].get(i).getValue() < lowValue)
                        {
                            low = i;
                            lowValue = hands[hand].get(i).getValue();
                        }
                    }
                    if (low != -1)
                        return low;
                }
            }
            else
            {
                int startSuit = trick[trickStarter].getSuit();
                int startValue = trick[trickStarter].getValue();
                
                if (hands[hand].containsSuit(startSuit))
                {
                    int low = 0;
                    int lowValue = 100;
                    for (int i = 0; i < hands[hand].size(); i++)
                    {
                        if (hands[hand].get(i).getSuit() == startSuit &&
                                hands[hand].get(i).getValue() < lowValue)
                        {
                            low = i;
                            lowValue = hands[hand].get(i).getValue();
                        }
                    }
                    return low;
                }
                else
                {
                    for (int i = 0; i < hands[hand].size(); i++)
                        if (hands[hand].get(i).getSuit() == Card.SPADE && 
                            hands[hand].get(i).getValue() == 12)
                            return i;
                    
                    int[] suits = {Card.HEART, Card.DIAMOND, Card.CLUB, Card.SPADE};
                    for (int suit : suits)
                    {
                        int high = -1;
                        int highValue = 0;
                        for (int i = 0; i < hands[hand].size(); i++)
                        {
                            if (hands[hand].get(i).getSuit() == suit && 
                                    hands[hand].get(i).getValue() > highValue)
                            {
                                high = i;
                                highValue = hands[hand].get(i).getValue();
                            }
                        }
                        if (high != -1)
                            return high;
                    }
                }
            }
        }
        
        
        return -1; // should never reach this point
    }
    
    private int getCardOver(Hand hand, MouseEvent e)
    {
        int cardOver = -1;
        
        boolean regularHand = (hand.getPosition() == Hand.WEST || hand.getPosition() == Hand.SOUTH);
        int start = (regularHand ? 0 : hand.size() - 1);
        int end = (regularHand ? hand.size() : -1);
        int increment = (regularHand ? 1 : -1);
        for (int i = start; i != end; i += increment)
        {
            hand.get(i).setMouseOver(false);
            
            if (hand.get(i).getOrientation() == Hand.NORTH ||
                    hand.get(i).getOrientation() == Hand.SOUTH)
            {
                if (hand.get(i).getBounds().contains(e.getX(), e.getY()))
                    cardOver = i;
            }
            else
            {
                Rectangle sideways = new Rectangle(hand.get(i).getX(),
                        hand.get(i).getY(),
                        hand.get(i).getHeight(),
                        hand.get(i).getWidth());
                if (sideways.contains(e.getX(), e.getY()))
                    cardOver = i;
            }
        }
        
        return cardOver;
    }
    
    private String getError(Card c)
    {
        if (activeHand == trickStarter)
        {
            if (firstTrick && (c.getValue() != 2 || c.getSuit() != Card.CLUB))
                return "You must play 2 of clubs on the first trick.";
            if (c.getSuit() == Card.HEART && !heartsBroken && 
                    (hands[activeHand].containsSuit(Card.CLUB) || 
                     hands[activeHand].containsSuit(Card.SPADE) ||
                     hands[activeHand].containsSuit(Card.DIAMOND)))
                return "Hearts must be broken before playing a heart.";
        }
        else
        {
            if (c.getSuit() != trick[trickStarter].getSuit() &&
                    hands[activeHand].containsSuit(trick[trickStarter].getSuit()))
                return "You must follow suit.";
        }
        
        if (firstTrick && c.getPointValue() > 0)
            return "You cannot play a point card on first trick.";
        
        return null;
    }
    
    public void mouseMoved(MouseEvent e)
    {
        if (activeHand > -1 && activeHand < hands.length)
        {
            int cardOver = this.getCardOver(hands[activeHand], e);
            if (cardOver != -1)
                hands[activeHand].get(cardOver).setMouseOver(true);
            
            repaint();
        }
    }
    public void mouseDragged(MouseEvent e)
    {
        mouseMoved(e);
    }
    public void mousePressed(MouseEvent e)
    {
        if (activeHand > -1 && activeHand < hands.length)
        {
            int cardOver = this.getCardOver(hands[activeHand], e);
            if (cardOver != -1)
            {
                if (swapState)
                {
                    if (hands[activeHand].get(cardOver).isSelected())
                        hands[activeHand].get(cardOver).setSelected(false);
                    else
                    {
                        if (hands[activeHand].getSelectedCards().length < 3)
                            hands[activeHand].get(cardOver).setSelected(true);
                    }
                    
                    swapButton.setEnabled(hands[activeHand].getSelectedCards().length == 3);
                }
                else
                {
                    Card c = hands[activeHand].get(cardOver);
                    
                    if (trickStarter == -1)
                        trickStarter = activeHand;
                    
                    // check if selected card is valid
                    String error = getError(c);
                    if (error != null)
                    {
                        textBubble.setText(error);
                        textBubble.setAlpha(255);
                        repaint();
                        if (bubbleFadeTimer.isRunning())
                            bubbleFadeTimer.restart();
                        else
                            bubbleFadeTimer.start();
                        return;
                    }
                    
                    textBubble.setAlpha(0);
                    
                    trick[activeHand] = c;
                    if (!heartsBroken && c.getPointValue() > 0)
                        heartsBroken = true;
                    
                    switch (hands[activeHand].getPosition())
                    {
                        case Hand.SOUTH: // intentional fall-through
                        case Hand.NORTH:
                            c.setLocation(DIM.width / 2 - (c.getWidth() / 2),
                                DIM.height / 2 - c.getHeight() - (c.getWidth() / 2) - 12 +
                                (hands[activeHand].getPosition() == Hand.NORTH ? 0 : c.getHeight() + c.getWidth()));
                            break;
                        case Hand.WEST: // intentional fall-through
                        case Hand.EAST:
                            c.setLocation(DIM.width / 2 - c.getHeight() - (c.getWidth() / 2) +
                                    (hands[activeHand].getPosition() == Hand.WEST ? 0 : c.getHeight() + c.getWidth()), 
                                    DIM.height / 2 - (c.getHeight() / 2));
                            break;
                    }
                    
                    // check if all 4 cards played in a trick
                    if ((trickStarter == 0 && activeHand == 3) || 
                            activeHand == trickStarter - 1)
                    {
                        hands[activeHand].setActive(false);
                        activeHand = -1;
                        if (firstTrick)
                            firstTrick = false;
                        
                        trickWinner = trickStarter;
                        int highValue = trick[trickStarter].getValue();
                        for (int i = 0; i < trick.length; i++)
                        {
                            if (highValue == 1)
                                break;
                            if (i != trickStarter &&
                                    trick[i].getSuit() == trick[trickStarter].getSuit() &&
                                    (trick[i].getValue() > highValue ||
                                    trick[i].getValue() == 1))
                            {
                                highValue = trick[i].getValue();
                                trickWinner = i;
                            }
                        }
                        
                        trickStarter = -1;
                        
                        for (int i = 0; i < trick.length; i++)
                            roundScore[trickWinner] += trick[i].getPointValue();
                        
                        animateTimer.start();
                    }
                    else
                    {
                        hands[activeHand].setActive(false);
                        activeHand = -1;
                        
                        for (int k = 0; k != trickStarter; k++)
                        {
                            activeHand = k;
                            Card c1 = hands[k].get(cardToPlay(k));
                            
                            trick[k] = c1;
                            if (!heartsBroken && c1.getPointValue() > 0)
                                heartsBroken = true;
                            
                            if (getError(c1) != null)
                            {
                                System.out.println(getError(c1));
                                JOptionPane.showConfirmDialog(null, "Congratulations! " +
                                        "You made the computer blow its brains out!\n" +
                                        "Because of your sadistic ways, the program will now close. " +
                                        "Have a nice day!",
                                        "Artificial Intelligence Error!",
                                        JOptionPane.YES_OPTION, JOptionPane.ERROR_MESSAGE);
                                System.exit(0);
                            }
                            
                            switch (hands[k].getPosition())
                            {
                                case Hand.SOUTH: // intentional fall-through
                                case Hand.NORTH:
                                    c1.setLocation(DIM.width / 2 - (c1.getWidth() / 2),
                                        DIM.height / 2 - c1.getHeight() - (c1.getWidth() / 2) - 12 +
                                        (hands[k].getPosition() == Hand.NORTH ? 0 : c1.getHeight() + c1.getWidth()));
                                    break;
                                case Hand.WEST: // intentional fall-through
                                case Hand.EAST:
                                    c1.setLocation(DIM.width / 2 - c1.getHeight() - (c1.getWidth() / 2) +
                                            (hands[k].getPosition() == Hand.WEST ? 0 : c1.getHeight() + c1.getWidth()), 
                                            DIM.height / 2 - (c1.getHeight() / 2));
                                    break;
                            }
                        }
                        hands[activeHand].setActive(false);
                        activeHand = -1;
                        if (firstTrick)
                            firstTrick = false;
                        
                        trickWinner = trickStarter;
                        int highValue = trick[trickStarter].getValue();
                        for (int i = 0; i < trick.length; i++)
                        {
                            if (highValue == 1)
                                break;
                            if (i != trickStarter &&
                                    trick[i].getSuit() == trick[trickStarter].getSuit() &&
                                    (trick[i].getValue() > highValue ||
                                    trick[i].getValue() == 1))
                            {
                                highValue = trick[i].getValue();
                                trickWinner = i;
                            }
                        }
                        
                        trickStarter = -1;
                        
                        for (int i = 0; i < trick.length; i++)
                            roundScore[trickWinner] += trick[i].getPointValue();
                        
                        animateTimer.start();
                    }
                }
            }
            
            repaint();
        }
    }
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    
    class TextBubble
    {
        private final Font font;
        
        private int alpha = 0;
        private boolean visible = false;
        private RoundRectangle2D bubble;
        
        private String text;
        
        public TextBubble()
        {
            font = new Font("Calibri", Font.BOLD, 18);
            bubble = new RoundRectangle2D.Float();
        }
        
        public void paint(Graphics g)
        {
            if (visible)
            {
                Graphics2D g2 = (Graphics2D)g;
                g2.setFont(font);
                FontMetrics fontMetrics = g2.getFontMetrics(font);
                int width = fontMetrics.stringWidth(text);
                bubble.setRoundRect((DIM.width / 2) - (width / 2) - 15, 500,
                        width + 30, 26, 10, 10);

                g2.setColor(new Color(10, 80, 190, alpha));
                g2.fill(bubble);

                g2.setColor(new Color(24, 123, 240, alpha));
                g2.setStroke(new BasicStroke(3));
                g2.draw(bubble);

                g2.setColor(new Color(250, 250, 250, alpha));
                g2.drawString(text, (int)bubble.getX() + 15, (int)bubble.getY() + (int)bubble.getHeight() - 7);
            }
        }
        
        public void setText(String text)
        {
            this.text = text;
        }
        
        public String getText()
        {
            return text;
        }
        
        public void setAlpha(int alpha)
        {
            this.alpha = alpha;
            if (alpha == 0)
                visible = false;
            else
                visible = true;
        }
        
        public int getAlpha()
        {
            return alpha;
        }
    }
}
