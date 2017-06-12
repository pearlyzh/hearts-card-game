/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lnm_hearts.business;

import java.awt.Dimension;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import lnm_hearts.model.Card;
import lnm_hearts.model.Hand;
import lnm_hearts.model.Player;

/**
 *
 * @author NghiaTruongNgoc
 */
public class MyBot extends Player {
    private Socket socketBOT;

    //public static trickStarter;
    //Ham dung chung de xem cho nao trong cac nguoi choi co con 2 chuong
    public static int getTrickStarter(List<Player> player) {
        for (int i = 0; i < player.size(); i++) {
            Hand hand = player.get(i).getHandHolder();
            if ((hand.get(0).getValue() == 2 && hand.get(0).getSuit() == Card.CLUB)
                    || (hand.get(hand.size() - 1).getValue() == 2
                    && hand.get(hand.size() - 1).getSuit() == Card.CLUB)) {
                return i;
            }
        }
        //khong nen ra day, de cho vui thoy
        return -1;
    }

    public MyBot(int position, Dimension d, Socket socketBOT) {
        this.position = position;
        this.DIM = d;
        handHolder = new Hand(position, DIM);
        this.socketBOT = socketBOT;
    }

    public static void swapCardPlayers(List<Player> players) {
        for (int i = 0; i < players.size(); i++) {
            Card[] selectedCards = players.get(i).getHandHolder().getSelectedCards();
            int destination = i + 1;
            if (destination > 3) {
                destination = 0;
            }

            for (Card c : selectedCards) {
                c.setSelected(false);
                c.setMouseOver(false);
                if (players.get(destination).getPosition() == Hand.SOUTH)//minh danh
                {
                    c.setSelected(true);
                    c.setMouseOver(true);
                }
                players.get(i).getHandHolder().remove(c);
                players.get(destination).getHandHolder().add(c);
            }
        }
    }

    @Override
    public int[] cardsToSwap() {
        System.out.println("Card to swap");
        ArrayList<Integer> cards = new ArrayList<Integer>(3);

        for (int i = 0; i < handHolder.size(); i++) {
            if (handHolder.get(i).getSuit() == Card.SPADE
                    && (handHolder.get(i).getValue() >= 12 || handHolder.get(i).getValue() == 1)) {
                cards.add(i);
            }
        }

        // Look for high hearts, then diamonds, then clubs
        int[] targetSuits = {Card.HEART, Card.DIAMOND, Card.CLUB};
        for (int targetSuit : targetSuits) {
            // North and east hands are sorted backwards, so use different indices.
            int i = 0;
            int increment = 1;
            for (; i < handHolder.size(); i += increment) {
                if (handHolder.get(i).getSuit() == targetSuit
                        && (handHolder.get(i).getValue() >= 11 || handHolder.get(i).getValue() == 1)) {
                    cards.add(i);
                }
            }
        }

        // Ensure that the computer has chosen at least 3 cards. If not,
        // cards from the end of the hand are added.
        if (cards.size() < 3) {
            // North and east hands are sorted backwards, so use different indices.
            int i = 0;
            int increment = 1;
            while (cards.size() < 3) {
                if (!cards.contains(i)) // Can't pick the same card twice
                {
                    cards.add(i);
                }
                i += increment;
            }
        }

        Object[] a = cards.toArray();
        int[] cardsToSwap = new int[3];
        for (int i = 0; i < 3; i++) {
            cardsToSwap[i] = (Integer) a[i];
        }
        return cardsToSwap;
    }

    @Override
    public void playCard() {
        if (firstTrick) {
            if (trickStarter == position) {
                for (int i = 0; i < handHolder.size(); i++) {
                    if (handHolder.get(i).getSuit() == Card.CLUB
                            && handHolder.get(i).getValue() == 2) {
                        //return i;
                        handHolder.setToPlay(i);
                        trick[position] = handHolder.get(i);
                        if (!Player.heartsBroken && handHolder.get(i).getPointValue() > 0) {
                            Player.heartsBroken = true;
                        }
                        return;
                    }
                }
            } else {
                int highClub = -1;
                int club = -1;
                for (int i = 0; i < handHolder.size(); i++) {
                    if (handHolder.get(i).getSuit() == Card.CLUB) {
                        if (handHolder.get(i).getValue() > highClub) {
                            highClub = handHolder.get(i).getValue();
                            club = i;
                        }
                    }
                }
                if (club != -1) {
                    //return club;
                    handHolder.setToPlay(club);
                    trick[position] = handHolder.get(club);
                    if (!Player.heartsBroken && handHolder.get(club).getPointValue() > 0) {
                        Player.heartsBroken = true;
                    }
                    return;
                }
                int[] suits = {Card.DIAMOND, Card.SPADE, Card.HEART};
                for (int suit : suits) {
                    int high = -1;
                    int highValue = 0;
                    for (int i = 0; i < handHolder.size(); i++) {
                        if (handHolder.get(i).getSuit() == suit
                                && handHolder.get(i).getValue() > highValue
                                && handHolder.get(i).getPointValue() != 13) {
                            high = i;
                            highValue = handHolder.get(i).getValue();
                        }
                    }
                    if (high != -1) {
                        //return high;
                        handHolder.setToPlay(high);
                        trick[position] = handHolder.get(high);
                        if (!Player.heartsBroken && handHolder.get(high).getPointValue() > 0) {
                            Player.heartsBroken = true;
                        }
                        return;
                    }
                }
            }
        } else {
            if (trickStarter == position) {
                int[] suits = {Card.SPADE, Card.DIAMOND, Card.CLUB, Card.HEART};
                for (int suit : suits) {
                    int low = -1;
                    int lowValue = 100;
                    for (int i = 0; i < handHolder.size(); i++) {
                        if (handHolder.get(i).getSuit() == suit
                                && handHolder.get(i).getValue() < lowValue) {
                            low = i;
                            lowValue = handHolder.get(i).getValue();
                        }
                    }
                    if (low != -1) {
                        //return low;
                        handHolder.setToPlay(low);
                        trick[position] = handHolder.get(low);
                        if (!Player.heartsBroken && handHolder.get(low).getPointValue() > 0) {
                            Player.heartsBroken = true;
                        }
                        return;
                    }
                }
            } else {
                int startSuit = trick[trickStarter].getSuit();
                int startValue = trick[trickStarter].getValue();

                if (handHolder.containsSuit(startSuit)) {
                    int low = 0;
                    int lowValue = 100;
                    for (int i = 0; i < handHolder.size(); i++) {
                        if (handHolder.get(i).getSuit() == startSuit
                                && handHolder.get(i).getValue() < lowValue) {
                            low = i;
                            lowValue = handHolder.get(i).getValue();
                        }
                    }
                    //return low;
                    handHolder.setToPlay(low);
                    trick[position] = handHolder.get(low);
                    if (!Player.heartsBroken && handHolder.get(low).getPointValue() > 0) {
                        Player.heartsBroken = true;
                    }
                    return;
                } else {
                    for (int i = 0; i < handHolder.size(); i++) {
                        if (handHolder.get(i).getSuit() == Card.SPADE
                                && handHolder.get(i).getValue() == 12) //return i;
                        {
                            handHolder.setToPlay(i);
                            trick[position] = handHolder.get(i);
                            if (!Player.heartsBroken && handHolder.get(i).getPointValue() > 0) {
                                Player.heartsBroken = true;
                            }
                            return;
                        }
                    }

                    int[] suits = {Card.HEART, Card.DIAMOND, Card.CLUB, Card.SPADE};
                    for (int suit : suits) {
                        int high = -1;
                        int highValue = 0;
                        for (int i = 0; i < handHolder.size(); i++) {
                            if (handHolder.get(i).getSuit() == suit
                                    && handHolder.get(i).getValue() > highValue) {
                                high = i;
                                highValue = handHolder.get(i).getValue();
                            }
                        }
                        if (high != -1) //return high;
                        {
                            handHolder.setToPlay(high);
                            trick[position] = handHolder.get(high);
                            if (!Player.heartsBroken && handHolder.get(high).getPointValue() > 0) {
                                Player.heartsBroken = true;
                            }
                            return;
                        }
                    }
                }
            }
        }
        return; //never reach at these
    }
}
