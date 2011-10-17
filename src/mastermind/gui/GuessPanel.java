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

/**
 * Super class for the board's color and number panels. As with the
 * UserControlPanel documentation and naming, in
 * general a color or number is referred to as a panel's element to
 * emphasize generality. <br>
 * A guess panel shows one selected element.  Thus a row on the board
 * is a row of these panels (and a result panel).
 */
public abstract class GuessPanel extends JPanel {
    // this panel's index
    protected int index;

    // indicates this panel has been selected if true -- so display
    // its element, etc
    protected boolean isSelected = false;

	/**
     * Abstract -- no objects of this class can be instantiated.
     */
    public GuessPanel() {
        super();
    }

    /**
     * Get the index of this panel's element
     * @return int - the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set this panel's index
     * @param index int - index associated with this panel's element
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Clears the panel and resets it to the unselected state.
     */
    public void clearPanel() {
        isSelected = false;
        repaint();
    }

    /**
     * Returns a reference to this panel.
     * @return GuessColorPanel - reference to this panel
     */
    public GuessPanel getPanel() {
        return this;
    }

    /**
     * Give the panel the object to display, and set it as selected
     * client should set the panel's index after selecting the panel
     * @param element Object - the panel's element -- color, number,
     * or whatever
     */
    public abstract void select(Object element);

    /**
     * returns true if this position has been selected
     * @return boolean - true if selected, false otherwise
     */
    public boolean isSelected() {
        return isSelected;
    }

}
