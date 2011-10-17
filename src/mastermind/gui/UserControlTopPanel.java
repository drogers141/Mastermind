/**
 * Dave Rogers
 * dave at drogers dot us
 * This software is for instructive purposes.  Use at your own risk - not meant to be robust at all.
 * Feel free to use anything, credit is appreciated if warranted.
 */
package mastermind.gui;

import java.awt.*;
import javax.swing.*;

import mastermind.MasterMind;

import java.awt.geom.*;

/**
 * Top panel displays selected colored peg head or number...
 * Note that this constructor is hardwired to start with a color--
 * due to the rush thing and the highlighting of colors
 * debacle described elsewhere.
 */
public class UserControlTopPanel extends JPanel {
    public UserControlTopPanel(Color color,
                               Color background,
                               int prefWidth, int prefHeight) {
        setPreferredSize(new Dimension(prefWidth, prefHeight));
        makeBorder();
        setBackground(background);
        this.color = color;
        highlightColor = MasterMind.getHighlightColor(color);
        usingHighlightColor = true;
    }
    // Make this panel's border
    private void makeBorder() {
        setBorder(BorderFactory.createBevelBorder(1, this.getBackground(),
                                                  Color.darkGray));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                             RenderingHints.VALUE_RENDER_QUALITY);
        Insets insets = getInsets();
        double width = (double) (getWidth() - insets.left - insets.right);
        double ht = (double) (getHeight() - insets.left - insets.right);
        if (displayType.equals("Color")) {
            circle = new Ellipse2D.Double(width * 5 / 16.0, ht / 8.0,
                                          width * 3 / 8.0, ht * 3 / 4.0);
            shadow(g2d);
            g2d.setPaint(color);
            g2d.fill(circle);
            make3d(g2d);
            g2d.setColor(Color.black);
            g2d.draw(circle);
        } else if (displayType.equals("Number")) {
            int w = (int) width;
            int h = (int) ht;
            g.setColor(MasterMind.selectedNumberColor);
            g.setFont(new Font("Arial", Font.BOLD, h));
            FontMetrics metrics = g.getFontMetrics();
            String strNumber = String.valueOf(number);
            int strW = metrics.stringWidth(strNumber);
            int strH = metrics.getHeight();
            g.drawString(strNumber, insets.left + w / 2 - strW / 2,
                         insets.top + h / 2 + strH / 3);
        }
    }

    // Makes the circle appear a little more 3d
    // Pre: circle is initialized to size and a "flat circle"
    // has already been drawn and filled
    private void make3d(Graphics2D g2d) {
        if (!displayType.equals("Color")) {
            return;
        }
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
        if (!displayType.equals("Color")) {
            return;
        }
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
     * Display newly selected element.
     * @param element Object
     */
    public void displayNewElement(Object element) {
        if (element instanceof Color) {
            color = (Color) element;
            displayType = "Color";
            // until I can use a color algorithm for the highlight
            // use this hack to get the hard-coded highlight
            highlightColor = MasterMind.getHighlightColor(color);
            usingHighlightColor = true;
            repaint();
        } else if (element instanceof Integer) {
            number = (Integer) element;
            displayType = "Number";
            repaint();
        }
    }

    /**
     * Overloaded to deal with highlight colors.
     * @param element Color
     * @param highlight Color
     */
    public void displayNewElement(Color element, Color highlight) {
        color = element;
        highlightColor = highlight;
        usingHighlightColor = true;
        repaint();
    }

    // String version of an enumerated data type--tells
    // paintComponent() which routine to use--can add to this
    // for extending the class
    protected String displayType; // "Color" or "Number"

    // color to display
    private Color color;
    // number to display
    private Integer number;
    // circle surrounding this panel's color (the peg top)
    private Ellipse2D circle;
    // A hardcoded highlight color--because I don't have time
    // to really learn colors better yet ...
    private Color highlightColor;
    // A flag to set to signal we are using a highlight color
    // rather than the regular algorithm
    private boolean usingHighlightColor = false;


}
