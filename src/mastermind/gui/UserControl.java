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
//import javax.swing.BorderFactory.*;
import java.awt.geom.*;

/**
 * The <code>UserControl</code> class is a component of the Mastermind's
 * View that holds the start/stop button, and all the colors/numbers that the user
 * can select to play with before the game is started, and to select for
 * each guess once the game is underway.
 */
public class UserControl extends JPanel {

    private JButton start = new JButton("Start");
    private UserControlTopPanel topPanel;
    private UserControlPanel[] panels;
    //private String[] strColors = {"red", "yellow", "green", "blue",
    //                             "orange", "magenta", "cyan", "gray"};
    // the total number of colors available for this game
    //private int totalNumColors;

    // unitWidth is the width of one of the colorPanels[]
    private static int unitWidth = MasterMind.boardUnitHt * 5/4;
    // unitHeight is the height of one of the colorPanels[]
    private static int unitHeight = MasterMind.boardUnitHt * 5/4;

    private Color backgroundColor = new Color(160, 160, 160);

    // holds the elements to select from -- colors, numbers, etc
    private Object[] elements;


	public UserControl() {}
    /**
     * The users control has an array of JPanels which each display
     * one element -- for this game either a colored peg head or
     * a number , each has to add the mouse listener to itself
     * so the Controller can listen to it--thus registering the
     * user's selected color.
     * @param ml MouseListener - for the element panels
     * @param al ActionListener - for the Start button
     * @param elements Object[] - the elements (e.g. Colors, or
     * Integers) to use in this Mastermind instance
     */
    public UserControl(MouseListener ml,
                        ActionListener al,
                        Object[] elements) {
	this.elements = elements;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(backgroundColor);
        makeBorder();
        panels = new UserControlPanel[elements.length];
        JPanel p = new JPanel(new GridLayout(0, 2));
        for (int i = 0; i < panels.length; i++) {
            if (elements[i] instanceof Color) {
                panels[i] = new UserControlColorPanel(ml, (Color)elements[i],
                        backgroundColor, unitWidth, unitHeight);
                panels[i].setIndex(i);
            } else if (elements[i] instanceof Integer) {
                panels[i] = new UserControlNumberPanel(ml,
                        (Integer)elements[i], backgroundColor,
                        unitWidth, unitHeight);
                panels[i].setIndex(i);
            }
            p.add(panels[i]);
        }
        topPanel = new UserControlTopPanel(MasterMind.baseColors[0],
                                           backgroundColor,
                                            2 * unitWidth, unitHeight);
        add(topPanel);
        add(p);
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        start.setBackground(backgroundColor);
        start.setActionCommand("start");
        start.addActionListener(al);
        add(start);
    }

    /*
        private void makeBorder() {
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createRaisedBevelBorder(),
                            BorderFactory.createEmptyBorder(5, 10, 5, 10)),
                    BorderFactory.createRaisedBevelBorder()));
        }
     */
    private void makeBorder() {
        setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createCompoundBorder(
                                        BorderFactory.createBevelBorder(
                                                0, backgroundColor,
                                                Color.darkGray),
                                        BorderFactory.createBevelBorder(
                                                0, backgroundColor,
                                                Color.darkGray)),
                                BorderFactory.createEmptyBorder(5, 10, 5, 10)),
                        BorderFactory.createBevelBorder(
                                0, backgroundColor, Color.darkGray)));
    }

    /**
     * Show the currently selected element at the top of this control.
     * @param index int - index of the element to display
     */
    public void showSelectedElementAtTop(int index) {
        topPanel.displayNewElement(elements[index]);
    }

    /**
     * Returns an array of the indexes of the elements
     * user selected to play this game with.
     * @return int[] - selected indexes
     */
    public int[] getSelectedElementIndexes() {
        int count = 0;
        for (int i = 0; i < panels.length; i++) {
            if (panels[i].isSelected()) {
                count++;
            }
        }
        int[] selected = new int[count];
        int colIndex = 0;
        for (int i = 0; i < panels.length; i++) {
            if (panels[i].isSelected()) {
                selected[colIndex++] = panels[i].getIndex();
            }
        }
        return selected;
    }

    /**
     * Changes start button to "End"
     */
    public void start() {
        start.setText("End");
        start.repaint();
    }

    /**
     * Changes start button back to "Start"
     */
    public void stop() {
        start.setText("Start");
        start.repaint();
    }

    // For testing
    public static void main(String[] args) {
        JFrame f = new JFrame("Test User control");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        UserControl control = new UserControl();
                //new ClickHandler(), new ButtonHandler(), 8);
        f.getContentPane().add(control);
        f.pack();
        f.setVisible(true);
    }

}
