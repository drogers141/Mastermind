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

import java.awt.geom.*;

/**
 * This panel shows a selected color on the board, once the user clicks
 * in it's circle (peg head).
 */
public class GuessColorPanel extends GuessPanel {
    // This panel's display color
    private Color color = Color.black;
    // The circle enclosing the displayed color
    private Ellipse2D circle;
    // A hardcoded highlight color--because I don't have time
    // to really learn colors better yet ...
    private Color highlightColor;
    // A flag to set to signal we are using a highlight color
    // rather than the regular algorithm
    private boolean usingHighlightColor = false;

	public GuessColorPanel() {}

    /**
     * Regular constructor uses algorithm to do 3d color highlight
     * @param prefWidth int
     * @param prefHeight int
     * @param ml MouseListener
     * @param background Color
     */
    public GuessColorPanel(int prefWidth, int prefHeight,
                           MouseListener ml, Color background) {
        setPreferredSize(new Dimension(prefWidth, prefHeight));
        setBackground(background);
        addMouseListener(ml);
        usingHighlightColor = true;
    }

    /**
     * paint the component either with a big circle the selected color,
     * or with a small black circle if unselected
     * @param g Graphics
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                             RenderingHints.VALUE_RENDER_QUALITY);
        if (isSelected) {
            setCircleSizeAndPosition();
            shadow(g2d);
            g2d.setPaint(color);
            g2d.fill(circle);
            make3d(g2d);
            g2d.setColor(Color.black);
            g2d.draw(circle);
        } else {
            g2d.setColor(Color.black);
            Insets insets = getInsets();
            double w = (double) (getWidth() - insets.left - insets.right);
            double h = (double) (getHeight() - insets.left - insets.right);
            Ellipse2D small = new Ellipse2D.Double((w / 2.0 - w / 12.0),
                    (h / 2.0 - h / 12.0), (w / 6.0),
                    (h / 6.0));
            g2d.fill(small);
            g2d.draw(small);
        }
    }

    // Makes the circle appear a little more 3d
    // Pre: circle is initialized to size and a "flat circle"
    // has already been drawn and filled
    private void make3d(Graphics2D g2d) {
        double w = circle.getWidth();
        double h = circle.getHeight();
        double cx = circle.getCenterX();
        double cy = circle.getCenterY();

        Ellipse2D small = new Ellipse2D.Double(cx - w * .3, cy - w * .3,
                                               w * .21, h * .21);
        Ellipse2D big = new Ellipse2D.Double(cx - w * .45, cy - w * .45,
                                             w * .8, h * .8);
        if (usingHighlightColor) {
            g2d.setPaint(highlightColor);
        } else {
            g2d.setPaint(new Color(1, 1, 1, .55F));
        }
        g2d.fill(big);
        g2d.setPaint(Color.white);
        g2d.fill(small);
    }

    // Draws a shadow to the lower right of the circle/sphere/peg
    // Pre: circle is initialized, but not filled yet
    private void shadow(Graphics2D g2d) {
        double w = circle.getWidth();
        double h = circle.getHeight();
        double cx = circle.getCenterX();
        double cy = circle.getCenterY();
        Ellipse2D sh = new Ellipse2D.Double(cx - w * .33, cy - w * .33,
                                            w * .95, h * .95);
        g2d.setPaint(this.getBackground().darker().darker());
        g2d.fill(sh);
    }

    /**
     * Select this panel to show the given color circle.
     * @param element Object - the color to show as selected in this
     * position.
     */
    public void select(Object element) {
        if (element instanceof Color) {
            this.color = (Color) element;
            highlightColor = MasterMind.getHighlightColor(color);
            isSelected = true;
            repaint();
        }
    }

    /**
     * Checks to see if point (x, y) is in this panel's circle.
     * @param x int - x coordinate of point.
     * @param y int - y coordinate of point.
     * @return boolean - <code>true</code> if (x, y) is in the circle,
     * <code>false</code> otherwise.
     */
    public boolean isInCircle(int x, int y) {
        setCircleSizeAndPosition();
        return circle.contains((double) x, (double) y);
    }

    // sets the size of circle whether drawn or not
    private void setCircleSizeAndPosition() {
        Insets insets = getInsets();
        double width = (double) (getWidth() - insets.left - insets.right);
        double ht = (double) (getHeight() - insets.left - insets.right);
        circle = new Ellipse2D.Double(width / 8, ht / 8,
                                      width * 3 / 4.0, ht * 3 / 4.0);
    }
}
