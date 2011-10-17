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

import mastermind.MasterMind;
import mastermind.event.ClickHandler;

/**
 * Guess panel that displays a number for selecting.
 */
public class GuessNumberPanel extends GuessPanel {
    // This panel's number
    private Integer number;
    // unitWidth is the width of one of the guessPanels[]
    private static int unitWidth = 50;
    // unitHeight is the height of one of the guessPanels[]
    private static int unitHeight = 50;


	public GuessNumberPanel() {}
    /**
     *
     * @param prefWidth int - preferred width of panel
     * @param prefHeight int- preferred height of panel
     * @param ml MouseListener - listens to this panels clicks
     * @param background Color - panel's backgound color
     */
    public GuessNumberPanel(int prefWidth, int prefHeight,
                            MouseListener ml, Color background) {
        setPreferredSize(new Dimension(prefWidth, prefHeight));
        setBorder(BorderFactory.createLineBorder(
                Color.black));
        setBackground(background);
        addMouseListener(ml);
    }

    /**
     * Paint the component either with its number,
     * or with a small black circle if unselected.
     * @param g Graphics
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Insets insets = getInsets();
        int w = getWidth() - insets.left - insets.right;
        int h = getHeight() - insets.top - insets.bottom;

        if (isSelected) {
            g.setColor(MasterMind.selectedNumberColor);
            g.setFont(new Font("Arial", Font.BOLD, h));
            g.drawString(String.valueOf(number),
                         w / 5, 9 * h / 10);
        } else {
            g.setColor(Color.black);
            g.fillOval(w / 2 - w / 12, h / 2 - h / 12, w / 6, h / 6);
        }
    }

    /**
     * Select this panel to show the given number.
     * @param element Object - the number to show
     */
    public void select(Object element) {
        if (element instanceof Integer) {
            this.number = (Integer) element;
            isSelected = true;
            repaint();
        }
    }

    /**
     * This main is for testing only
     * @param args String[]
     */
    public static void main(String[] args) {
        JFrame f = new JFrame();
        Box box = Box.createHorizontalBox();
        GuessNumberPanel[] panels =
                new GuessNumberPanel[5];
        for (int i = 0; i < 5; i++) {
            panels[i] = new GuessNumberPanel(unitWidth, unitHeight,
                                             new ClickHandler(),
                                             Color.lightGray);
            box.add(panels[i]);
        }
        f.getContentPane().add(box);
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);

    }

}
