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
 * Users control panel that displays a number for selecting.
 */
public class UserControlNumberPanel extends UserControlPanel {
    /**
     * Creates an uninitialized number panel.
     */
    public UserControlNumberPanel() {}

    /**
     * Creates a panel for the user control which displays a number.
     * @param ml MouseListener - listens to this panels clicks
     * @param number Object - the number to display
     * @param background Color - background color
     * @param prefWidth int - preferred panel width
     * @param prefHeight int - preferred panel height
     */
    public UserControlNumberPanel(MouseListener ml, Object number,
                                  Color background, int prefWidth,
                                  int prefHeight) {
        // put exception code in here--if bad cast
        this.number = (Integer) number;
        setPreferredSize(new Dimension(prefWidth, prefHeight));
        setBorder(BorderFactory.createLineBorder(
                Color.black));
        setBackground(background);
        addMouseListener(ml);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Insets insets = getInsets();
        int w = getWidth() - insets.left - insets.right;
        int h = getHeight() - insets.left - insets.right;
        g.setColor(MasterMind.numberColor);
        if (isSelected) {
            g.setColor(MasterMind.selectedNumberColor);
        }
        g.setFont(new Font("Arial", Font.BOLD, h));
        FontMetrics metrics = g.getFontMetrics();
        String strNumber = String.valueOf(number);
        int strW = metrics.stringWidth(strNumber);
        int strH = metrics.getHeight();
        //g.drawString(String.valueOf(number),
        //             w/5, 9 * h/10);
        g.drawString(strNumber, insets.left + w / 2 - strW / 2,
                     insets.top + h / 2 + strH / 3);
    }

    /**
     * Returns this panel's circle's color.
     * @return Color - the color of this panel's circle.
     */
    public int getNumber() {
        return number.intValue();
    }

    /**
     * This main is for testing only
     * @param args String[]
     */
    public static void main(String[] args) {
        JFrame f = new JFrame();
        Box box = Box.createHorizontalBox();
        UserControlNumberPanel[] panels =
                new UserControlNumberPanel[5];
        for (int i = 0; i < 5; i++) {
            panels[i] = new UserControlNumberPanel(new ClickHandler(),
                    new Integer(i), Color.lightGray,
                    unitWidth, unitHeight);
            box.add(panels[i]);
        }
        panels[2].select();
        panels[4].select();
        f.getContentPane().add(box);
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);

        UserControlNumberPanel userscontrolnumberpanel = new
                UserControlNumberPanel();
    }

    // this panel's number to display
    private Integer number;

    // unitWidth is the width of one of the guessPanels[]
    private static int unitWidth = 50;
    // unitHeight is the height of one of the guessPanels[]
    private static int unitHeight = 50;

}
