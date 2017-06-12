/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lnm_hearts.model;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.net.Socket;

/**
 *
 * @author NghiaTruongNgoc
 */
public class Player extends Thread {
    protected Hand handHolder;
    protected boolean active; //den luot danh
    protected int position = 0; //vi tri
//    protected Graphics2D graphToDraw;
    public static boolean firstTrick = true;
    public static int trickStarter;
    public static Card[] trick = new Card[4];
    protected Dimension DIM;
    public static boolean heartsBroken = false;
    private Socket socket;
    public Player()
    {
        //handHolder = new Hand(position, DIM);
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    
    public Player(int _postiotion, Dimension dim, Socket socket)
    {
        position = _postiotion;
        DIM = dim;
        handHolder = new Hand(position, DIM);
        this.socket = socket;
    }
    
    public void playCard()
    {
        return;
    }
    
    

//    public Graphics2D getGraphToDraw() {
//        return graphToDraw;
//    }
//
//    public void setGraphToDraw(Graphics2D graphToDraw) {
//        this.graphToDraw = graphToDraw;
//    }

    public int getPosition() {
        return position;
    }
    
    public int[] cardsToSwap()
    {
        return null;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    public Hand getHandHolder() {
        return handHolder;
    }

    public void setHandHolder(Hand handHolder) {
        this.handHolder = handHolder;
    }
    
    public void draw(Graphics2D graphToDraw)
    {
        handHolder.paint(graphToDraw);
    }
}
