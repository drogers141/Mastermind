/**
 * Dave Rogers
 * dave at drogers dot us
 * This software is for instructive purposes.  Use at your own risk - not meant to be robust at all.
 * Feel free to use anything, credit is appreciated if warranted.
 */
package mastermind.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Just a panel to show that the game is won or lost.
 * Could make this cool, though, later on with .gifs or whatever.
 */
public class WinOrLose extends JPanel {
    public WinOrLose() {}

    /**
     * Constructs a panel
     * @param prefWidth int - preferred width of panel
     * @param prefHeight int - preferred height of panel
     */
    public WinOrLose(int prefWidth, int prefHeight,
                     int guessLength) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(prefWidth, prefHeight));
        //ourWidth = prefWidth;//**** for winning combo stuff
         makeBorder();
        setBackground(backgroundColor);
        Insets insets = getInsets();
        int w = getWidth() - insets.left - insets.right;
        int h = getHeight() - insets.left - insets.right;

        message = new WinOrLoseMessage(w, h / 2);
        comboDisplay = new WinOrLoseDisplayWinningCombo(w, h / 2,
                					guessLength);
        add(message);
        add(comboDisplay);
    }

    /**
     * Display a message indicating the user has won
     */
    public void showGameWon() {
        message.showGameWon();
    }

    /**
     * Display a message indicating the user has lost
     */
    public void showGameLost() {
        message.showGameLost();
    }

    /**
     * Show the winning combination of colors or numbers
     * 
     * @param elems Object[] - the array of winning elements
     */
    public void showWinningCombination(Object[] elems) {
        comboDisplay.showWinningCombination(elems);
    }
    
    /**
     * Clears the winning combination panel. Call on new game.
     */
    public void clear() {
    	comboDisplay.clear();
    }

    // make this panel's border
    private void makeBorder() {
        setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createBevelBorder(
                                        0, backgroundColor,
                                        Color.darkGray),
                                BorderFactory.createBevelBorder(
                                        0, backgroundColor,
                                        Color.darkGray)),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    // panel's background color
    private Color backgroundColor = new Color(160, 160, 160);
    // true if the user won, false otherwise
    private boolean gameWon = false;

    // for drawing the winning combination
    private int ourWidth;
    
	// The winning combination displaying object
    private WinOrLoseDisplayWinningCombo comboDisplay;

    // The message displaying object
    private WinOrLoseMessage message;

    /**
     * Main is for testing only
     * @param args String[]
     */
    public static void main(String[] args) {
        JFrame f = new JFrame();
        WinOrLose w = new WinOrLose(200, 100, 4);
        f.getContentPane().add(w);
//        w.showGameWon();
        w.showGameLost();
        f.pack();
        f.setLocation(100, 400);
        f.setVisible(true);
    }

}
