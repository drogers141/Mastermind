/**
 * Dave Rogers
 * dave at drogers dot us
 * This software is for instructive purposes.  Use at your own risk - not meant to be robust at all.
 * Feel free to use anything, credit is appreciated if warranted.
 */
package mastermind.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*;

/**
 * Panel to display game won or lost message.
 */
public class WinOrLoseMessage extends JPanel {
    public WinOrLoseMessage() {
    }

    public WinOrLoseMessage(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setBackground(backgroundColor);
        //this.width = width;
        //this.height = height;
    }

    /**
     * Display a message indicating the user has won
     */
    public void showGameWon() {
        gameWon = true;
        repaint();
    }

    /**
     * Display a message indicating the user has lost
     */
    public void showGameLost() {
        gameWon = false;
        repaint();
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(115, 10, 214)); // violet
        Insets insets = getInsets();
        int width = getWidth() - insets.left - insets.right;
        int ht = getHeight() - insets.top - insets.bottom;
        g.setFont(new Font("Arial", Font.BOLD, fontHeight));
        FontMetrics metrics = g.getFontMetrics();
        String message;
        if (gameWon) {
            message = "Nice Job!";
        } else {
            message = "Sorry...";
        }
        int strW = metrics.stringWidth(message);
        int strH = metrics.getHeight();
        g.drawString(message, insets.left + width / 2 - strW / 2,
                     ht / 2 + strH / 2);
    }
    
    // if we want to set font elsewhere
    public static int fontHeight = 24;

// panel's background color
    private Color backgroundColor = new Color(160, 160, 160);
// true if the user won, false otherwise
    private boolean gameWon = false;

    //private int height;
    //private int width;
}
