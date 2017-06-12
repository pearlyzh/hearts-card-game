package lnm_hearts.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import lnm_hearts.business.MyBot;
import lnm_hearts.model.Card;
import lnm_hearts.model.Deck;
import lnm_hearts.model.Hand;
import lnm_hearts.model.Player;

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
    //private Hand[] hands;

    private List<Player> players = new ArrayList<Player>();
    private int activeHand = -1;
    private ImageIcon[] arrowIcons;

    private int[] roundScore;
    //private boolean firstTrick = true;
    //private boolean heartsBroken = false;
    private int trickWinner = -1;
    private int animateSpeed = 0;

    private Timer dealTimer, bubbleFadeTimer, animateTimer, handTimer;
    private int dealTimerIteration = 0;
    private boolean swapState = false;

//    /private TextBubble textBubble;
    private InforPopup inforPopup;
    private boolean showScore = false;
    public boolean isServer = false;
    public boolean isClient = false;
    private ServerSocket listener = null;
    private BufferedReader is;
    private BufferedWriter os;
    private Socket socketOfServer = null;
    private int slot = 3;
    public boolean started = false;
    public String serverAddress = "";
    private ServerInitializer serverInitializer;

    public MainPanel(JFrame parent) {

        super();
        this.parent = parent;
        setPreferredSize(DIM);
        // /setBackground(new Color(0, 127, 0));
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
        roundScore = new int[4];

        //textBubble = new TextBubble();
        inforPopup = new InforPopup(DIM);

//        players.add(new MyBot(Hand.WEST, DIM));
//        players.add(new MyBot(Hand.NORTH, DIM));
//        players.add(new MyBot(Hand.EAST, DIM));
        //players.add(new MyBot(Hand.NORTH, DIM));
        dealTimer = new Timer(60, this);
        bubbleFadeTimer = new Timer(60, this);
        bubbleFadeTimer.setInitialDelay(1000);
        animateTimer = new Timer(10, this);
        animateTimer.setInitialDelay(1000);
        handTimer = new Timer(800, this);
        serverInitializer = new ServerInitializer();
    }

    public void createServer() {
        try {
            listener = new ServerSocket(7777);
            System.out.println("Tao server------------------------------");

        } catch (IOException ex) {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
//            try {
//               // socketOfServer = listener.accept();
//            } catch (IOException ex) {
//                Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
//            }

        System.out.println("Tao xong server------------------------------");
        players.add(new Player(Hand.SOUTH, DIM, null));
        isServer = true;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        //Graphics2D g2 = (Graphics2D)getGraphics();
        Image backgroundImage = new ImageIcon("src/lnm_hearts/gui/images/background.jpg").getImage();
        g2.drawImage(backgroundImage, null, null);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //textBubble.paint(g2);
        inforPopup.paint(g2);
        for (Player player : players) {
            player.draw(g2);
        }

        if (showScore) {
            g2.setColor(Color.white);
            g2.setFont(new Font("Calibri", Font.BOLD, 28));
            g2.drawString("West: " + players.get(Hand.WEST).getHandHolder().getPoints(), 200, 350);
            g2.drawString("North: " + players.get(Hand.NORTH).getHandHolder().getPoints(), 200, 380);
            g2.drawString("East: " + players.get(Hand.EAST).getHandHolder().getPoints(), 200, 410);
            g2.drawString("You: " + players.get(Hand.SOUTH).getHandHolder().getPoints(), 200, 440);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isServer) {
            System.out.println("perform2");
            if (listener == null) {
                createServer();
                serverInitializer.start();
            }
            if (started) {
                while (slot > 0) {
                    System.out.println("Add BOT o vi tri " + (3 - slot));
                    players.add(new MyBot(4 - slot, DIM, socketOfServer));
                    slot--;
                }
            }

            if (e.getSource().equals(dealButton)) {
                showScore = false;
                remove(dealButton);
                repaint();
                deck.shuffle(Deck.SHUFFLE_REGULAR);
                dealTimer.start();
            } else if (e.getSource().equals(swapButton)) {
                MyBot.swapCardPlayers(players);
                for (int i = 0; i < players.size(); i++) {
                    players.get(i).getHandHolder().sort();
                }
                remove(swapButton);
                swapState = false;
                add(okButton);
                validate();
                repaint();
            } else if (e.getSource().equals(dealTimer)) {
                System.out.println("Deal Time");
                deck.get(dealTimerIteration).setUp(true);
                players.get(dealTimerIteration % 4).getHandHolder().add(deck.get(dealTimerIteration));
                repaint();
                dealTimerIteration++;

                if (dealTimerIteration == 52) {
                    dealTimer.stop();
                    dealTimerIteration = 0;

                    for (int i = 0; i < players.size(); i++) {
                        players.get(i).getHandHolder().sort();
                    }
                    activeHand = Hand.SOUTH;
                    players.get(Hand.SOUTH).setActive(true);
                    swapState = true;
                    add(swapButton);
                    swapButton.setEnabled(false);
                    this.validate();

                    for (int i = 0; i < players.size(); i++) {
                        if (players.get(i) instanceof MyBot) {
                            System.out.println("Player" + i + "is a bot");
                            int[] cardsToSwap = players.get(i).cardsToSwap();
                            for (int k = 0; k < 3; k++) {
                                players.get(i).getHandHolder().get(cardsToSwap[k]).setSelected(true);
                            }
                        }
                    }
                }
            } else if (e.getSource().equals(okButton)) {
                remove(okButton);
                validate();

                for (int i = 0; i < players.size(); i++) {
                    if ((players.get(i).getHandHolder().get(0).getValue() == 2
                            && players.get(i).getHandHolder().get(0).getSuit() == Card.CLUB)
                            || (players.get(i).getHandHolder().get(players.get(i)
                                    .getHandHolder().size() - 1).getValue() == 2
                            && players.get(i).getHandHolder().get(players.get(i)
                                    .getHandHolder().size() - 1).getSuit() == Card.CLUB)) {
                        if (i == Hand.SOUTH) {
                            activeHand = i;
                            //hands[i].setActive(true);
                            System.out.println(i + "co con 2 chuong");
                            players.get(i).setActive(true);
                        } else {
                            Player.trickStarter = i;
                            System.out.println(i + "co con 2 chuong");
                            //trickStarter = i;
                            for (int k = i; k != Hand.SOUTH; k++) {
                                System.out.println(k + "danh bai ne");
                                activeHand = k;
                                players.get(k).playCard();
                                if (k == 3) {
                                    break;
                                }
                            }
                            activeHand = Hand.SOUTH;
                            players.get(Hand.SOUTH).getHandHolder().setActive(true);
                        }

                        break;
                    }
                }

                repaint();
            } else if (e.getSource().equals(bubbleFadeTimer)) {
                //textBubble.setAlpha(textBubble.getAlpha() - 10);
                inforPopup.setAlpha(inforPopup.getAlpha() - 5);
                if (inforPopup.getAlpha() <= 0) {
                    inforPopup.setAlpha(0);
                    bubbleFadeTimer.stop();
                }

                repaint();
            } else if (e.getSource().equals(animateTimer)) {
                boolean shouldStop = true;
                switch (trickWinner) {
                    case 0: // intentional fall-through
                    case 2:
                        for (Card c : Player.trick) {
                            c.setY(c.getY() + (trickWinner == 2 ? -animateSpeed : animateSpeed));
                        }
                        if (trickWinner == 2) {
                            if (Player.trick[3].getY() + Player.trick[3].getHeight() > 0) {
                                shouldStop = false;
                            }
                        } else {
                            if (Player.trick[1].getY() < DIM.height) {
                                shouldStop = false;
                            }
                        }
                        break;
                    case 1: // intentional fall-through
                    case 3:
                        for (Card c : Player.trick) {
                            c.setX(c.getX() + (trickWinner == 1 ? -animateSpeed : animateSpeed));
                        }
                        if (trickWinner == 1) {
                            if (Player.trick[2].getX() + Player.trick[2].getHeight() > 0) {
                                shouldStop = false;
                            }
                        } else {
                            if (Player.trick[0].getX() < DIM.width) {
                                shouldStop = false;
                            }
                        }
                        break;
                }
                animateSpeed += 3;
                if (shouldStop) {
                    animateSpeed = 0;
                    animateTimer.stop();

                    for (int i = 0; i < players.size(); i++) {
                        players.get(i).getHandHolder().remove(Player.trick[i]);
                        Player.trick[i] = null;
                    }

                    if (players.get(Hand.WEST).getHandHolder().size() == 0) {
                        boolean shotTheMoon = false;
                        int moonShooter = -1;
                        for (int i = 0; i < roundScore.length; i++) {
                            if (roundScore[i] == 26) {
                                shotTheMoon = true;
                                moonShooter = i;
                                roundScore[i] = 0;
                            }
                        }

                        if (shotTheMoon) {
                            for (int i = 0; i < players.size(); i++) {
                                if (i == moonShooter) {
                                    players.get(i).getHandHolder().setRoundPoints(0);
                                } else {
                                    players.get(i).getHandHolder().setRoundPoints(26);
                                    players.get(i).getHandHolder().setPoints(players.get(i).getHandHolder().getPoints() + 26);
                                }
                                players.get(i).getHandHolder().showPoints();
                            }
                        } else {
                            for (int i = 0; i < players.size(); i++) {
                                players.get(i).getHandHolder().setRoundPoints(roundScore[i]);
                                players.get(i).getHandHolder().setPoints(players.get(i).getHandHolder().getPoints() + roundScore[i]);
                                players.get(i).getHandHolder().showPoints();
                                roundScore[i] = 0;
                            }
                        }

                        Player.firstTrick = true;
                        Player.heartsBroken = false;

                        showScore = true;
                        add(dealButton);
                        this.validate();

                        repaint();
                        return;
                    }

                    if (trickWinner == Hand.SOUTH) {
                        activeHand = trickWinner;
                        players.get(activeHand).getHandHolder().setActive(true);
                    } else {
                        Player.trickStarter = trickWinner;
                        Player.trickStarter = trickWinner;
                        for (int k = trickWinner; k != Hand.SOUTH; k++) {
                            activeHand = k;
                            players.get(k).playCard();
                            if (k == 3) {
                                break;
                            }
                        }
                        activeHand = Hand.SOUTH;
                        players.get(Hand.SOUTH).getHandHolder().setActive(true);
                    }
                }

                repaint();
            } else if (e.getSource().equals(handTimer)) {
                handTimer.stop();

                repaint();
            }
        }
        if (isClient) {
            try {
                System.out.println("Client se ket noi do");
                clientAction();
            } catch (IOException ex) {
                Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void clientAction() throws IOException {
        Socket socket = null;
        System.out.println("Client chuan bi ket noi");
        try {
            System.out.println("Sap ket noi");
            socket = new Socket("localhost", 7777);
            System.out.println("Client da ket noi");
        } catch (IOException ex) {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedReader in;
        PrintWriter out;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    private int getCardOver(Hand hand, MouseEvent e) {
        int cardOver = -1;

        boolean regularHand = (hand.getPosition() == Hand.WEST || hand.getPosition() == Hand.SOUTH);
        int start = (regularHand ? 0 : hand.size() - 1);
        int end = (regularHand ? hand.size() : -1);
        int increment = (regularHand ? 1 : -1);
        for (int i = start; i != end; i += increment) {
            hand.get(i).setMouseOver(false);

            if (hand.get(i).getOrientation() == Hand.NORTH
                    || hand.get(i).getOrientation() == Hand.SOUTH) {
                if (hand.get(i).getBounds().contains(e.getX(), e.getY())) {
                    cardOver = i;
                }
            } else {
                Rectangle sideways = new Rectangle(hand.get(i).getX(),
                        hand.get(i).getY(),
                        hand.get(i).getHeight(),
                        hand.get(i).getWidth());
                if (sideways.contains(e.getX(), e.getY())) {
                    cardOver = i;
                }
            }
        }

        return cardOver;
    }

    private String getError(Card c) {
        if (activeHand == Player.trickStarter) {
            if (Player.firstTrick && (c.getValue() != 2 || c.getSuit() != Card.CLUB)) {
                return "You must play 2 of clubs on the first trick.";
            }
            if (c.getSuit() == Card.HEART && !Player.heartsBroken
                    && (players.get(activeHand).getHandHolder().containsSuit(Card.CLUB)
                    || players.get(activeHand).getHandHolder().containsSuit(Card.SPADE)
                    || players.get(activeHand).getHandHolder().containsSuit(Card.DIAMOND))) {
                return "Hearts must be broken before playing a heart.";
            }
        } else {
            if (c.getSuit() != Player.trick[Player.trickStarter].getSuit()
                    && players.get(activeHand).getHandHolder().containsSuit(Player.trick[Player.trickStarter].getSuit())) {
                return "You must follow suit.";
            }
        }

        if (Player.firstTrick && c.getPointValue() > 0) {
            return "You cannot play a point card on first trick.";
        }

        return null;
    }

    public void mouseMoved(MouseEvent e) {
        if (players.size() == 4) {
            int cardOver = this.getCardOver(players.get(Hand.SOUTH).getHandHolder(), e);
            if (cardOver != -1) {
                players.get(Hand.SOUTH).getHandHolder().get(cardOver).setMouseOver(true);
            }

            repaint();
        }
    }

    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    public void mousePressed(MouseEvent e) {
        System.out.println("bam chuot");
        if (activeHand > -1 && activeHand < players.size()) {
            System.out.println("cho bam");
            int cardOver = this.getCardOver(players.get(Hand.SOUTH).getHandHolder(), e);
            if (cardOver != -1) {
                if (swapState) {
                    if (players.get(Hand.SOUTH).getHandHolder().get(cardOver).isSelected()) {
                        players.get(Hand.SOUTH).getHandHolder().get(cardOver).setSelected(false);
                    } else {
                        if (players.get(Hand.SOUTH).getHandHolder().getSelectedCards().length < 3) {
                            players.get(Hand.SOUTH).getHandHolder().get(cardOver).setSelected(true);
                        }
                    }
                    System.out.println("DA CHON DC " + players.get(Hand.SOUTH).getHandHolder().getSelectedCards().length);
                    swapButton.setEnabled(
                            players.get(Hand.SOUTH).getHandHolder()
                                    .getSelectedCards().length == 3);
                } else {
                    Card c = players.get(Hand.SOUTH).getHandHolder().get(cardOver);

                    if (Player.trickStarter == -1) {
                        Player.trickStarter = activeHand;
                        //MyBot.trickStarter = activeHand;
                    }

                    // check if selected card is valid
                    String error = getError(c);
                    if (error != null) {
                        //textBubble.setText(error);
                        //textBubble.setAlpha(255);

                        inforPopup.setText(error);
                        inforPopup.setAlpha(255);
                        repaint();
                        if (bubbleFadeTimer.isRunning()) {
                            bubbleFadeTimer.restart();
                        } else {
                            bubbleFadeTimer.start();
                        }
                        return;
                    }

                    //textBubble.setAlpha(0);
                    inforPopup.setAlpha(0);

                    Player.trick[activeHand] = c;
                    if (!Player.heartsBroken && c.getPointValue() > 0) {
                        Player.heartsBroken = true;
                    }

                    players.get(Hand.SOUTH).getHandHolder().setToPlay(cardOver);

                    if ((Player.trickStarter == Hand.SOUTH && activeHand == Hand.EAST)
                            || activeHand == Player.trickStarter - 1) {
                        players.get(activeHand).getHandHolder().setActive(false);
                        activeHand = -1;
                        if (Player.firstTrick) {
                            Player.firstTrick = false;
                        }

                        trickWinner = Player.trickStarter;
                        int highValue = Player.trick[Player.trickStarter].getValue();
                        for (int i = 0; i < Player.trick.length; i++) {
                            if (highValue == 1) {
                                break;
                            }
                            if (i != Player.trickStarter
                                    && Player.trick[i].getSuit() == Player.trick[Player.trickStarter].getSuit()
                                    && (Player.trick[i].getValue() > highValue
                                    || Player.trick[i].getValue() == 1)) {
                                highValue = Player.trick[i].getValue();
                                trickWinner = i;
                            }
                        }

                        System.out.println("nguoi 1 thanh la" + trickWinner);

                        Player.trickStarter = -1;

                        for (int i = 0; i < Player.trick.length; i++) {
                            roundScore[trickWinner] += Player.trick[i].getPointValue();
                        }

                        animateTimer.start();
                    } else { //chua du 4 la bai danh xuong
                        players.get(activeHand).getHandHolder().setActive(false);
                        activeHand = -1;

                        for (int k = Player.trickStarter + 1; k != Player.trickStarter; k++) {
                            if (k > 3) {
                                k = 0;
                            }
                            if (k == Player.trickStarter) {
                                break;
                            }
                            activeHand = k;
                            players.get(activeHand).playCard();
                        }

                        players.get(activeHand).getHandHolder().setActive(false);
                        activeHand = -1;
                        if (Player.firstTrick) {
                            Player.firstTrick = false;
                        }

                        trickWinner = Player.trickStarter;
                        int highValue = Player.trick[Player.trickStarter].getValue();
                        for (int i = 0; i < Player.trick.length; i++) {
                            if (highValue == 1) {
                                break;
                            }
                            if (i != Player.trickStarter
                                    && Player.trick[i].getSuit() == Player.trick[Player.trickStarter].getSuit()
                                    && (Player.trick[i].getValue() > highValue
                                    || Player.trick[i].getValue() == 1)) {
                                highValue = Player.trick[i].getValue();
                                trickWinner = i;
                            }
                        }

                        System.out.println("nguoi 2 thanh la" + trickWinner);

                        Player.trickStarter = -1;

                        for (int i = 0; i < Player.trick.length; i++) {
                            roundScore[trickWinner] += Player.trick[i].getPointValue();
                        }

                        animateTimer.start();
                    }
                }
            }

            repaint();
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    private class ServerInitializer extends Thread {

        @Override
        public void run() {
            try {
                while (!started) {
                    System.out.println("Dang cho thang thu " + (3 - slot));
                    Socket socket = listener.accept();
                    System.out.println("Da ket noi");
                    slot--;

                    if (slot <= 0) {
                        started = true;
                        listener.close();
                        return;
                    }
                    players.add(new Player(3 - slot, DIM, socket));
                }

            } catch (Exception ex) {
                System.out.println("nat---- " + ex);
            } finally {
                try {
                    listener.close();
                } catch (IOException ex) {
                    Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
