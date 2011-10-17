/**
 * Dave Rogers
 * dave at drogers dot us
 * This software is for instructive purposes.  Use at your own risk - not meant to be robust at all.
 * Feel free to use anything, credit is appreciated if warranted.
 */
package mastermind.gui;

import javax.swing.JPanel;
import java.awt.LayoutManager;

/**
 * Super class for the user control's color and number panels. In
 * general a color or number is referred to as a panel's element to
 * emphasize generalizability.
 */
public abstract class UserControlPanel extends JPanel {

    // this panel's index
    protected int index;

    // true if this panel is selected
    protected boolean isSelected = false;


	public UserControlPanel() {
        super();
    }

    /**
     * Get the index to this panel's element--ie the color or number,
     * etc, that it displays.
     * @return int -the index of this panel's element
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set the index to this panel's element--ie the color or number,
     * etc, that it displays.
     * @param index int - index associated with this panel's element
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Select this panel's element
     */
    public void select() {
        isSelected = true;
        repaint();
    }
    /**
     * Deselect this panel's element
     */
    public void deselect() {
        isSelected = false;
        repaint();
    }
    /**
     * Returns whether or not this panel's element has been selected.
     * @return boolean
     */
    public boolean isSelected() {
        return isSelected;
    }
}
