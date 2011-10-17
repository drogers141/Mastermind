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

import mastermind.event.ClickHandler;

/**
 * Panel to display winning combination of elements.
 */
public class WinOrLoseDisplayWinningCombo extends JPanel {
    public WinOrLoseDisplayWinningCombo(int width, int height,
                                        int guessLength) {
        setBackground(backgroundColor);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(width, height));
        this.width = width;
        this.height = height;
        winningElements = new GuessPanel[guessLength];
    }

    /**
     * Clear panel of all elements.
     */
    public void clear() {
    	this.removeAll();
    }
    
    /**
     * Show the winning combination of colors or numbers
     * @param elems Object[] - the array of winning elements
     * @todo Debug me, I don't work yet -- null pointer exception
     * while attempting to construct winningElements[i]
     */
    public void showWinningCombination(Object[] elems) {
    	this.removeAll();
        Box box = Box.createHorizontalBox();
        int size = height / elems.length;
        if (elems[0] instanceof Color) {
            for (int i = 0; i < elems.length; i++) {
                winningElements[i] = new GuessColorPanel(
                        size, size,
                        new ClickHandler(), backgroundColor);
                box.add(winningElements[i]);
                winningElements[i].select(elems[i]);
            }
        } else if (elems[0] instanceof Integer) {
            for (int i = 0; i < elems.length; i++) {
                winningElements[i] = new GuessNumberPanel(
                        size, size,
                        new ClickHandler(), backgroundColor);
                box.add(winningElements[i]);
                winningElements[i].select(elems[i]);
            }
        }
        add(box);
    }

    // Panels to display winning colors or numbers if game lost
    private GuessPanel[] winningElements;
    // panel's background color
    private Color backgroundColor = new Color(160, 160, 160);

    private int height;
    private int width;

}
