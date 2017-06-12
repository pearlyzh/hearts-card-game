/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lnm_hearts.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

/**
 *
 * @author NghiaTruongNgoc
 */
class InforPopup {

        private final Font font;

        private int alpha = 0;
        private boolean visible = false;
        private RoundRectangle2D bubble;
        private Dimension DIM;

        private String text;

        public InforPopup(Dimension dim) {
            DIM = dim;
            font = new Font("Calibri", Font.BOLD, 18);
            bubble = new RoundRectangle2D.Float();
        }

        public void paint(Graphics g) {
            if (visible) {
                //draw here

                Graphics2D g2 = (Graphics2D) g;

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
                g2.drawString(text, (int) bubble.getX() + 15, (int) bubble.getY() + (int) bubble.getHeight() - 7);
            }
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setAlpha(int alpha) {
            this.alpha = alpha;
            if (alpha == 0) {
                visible = false;
            } else {
                visible = true;
            }
        }

        public int getAlpha() {
            return alpha;
        }
    }
