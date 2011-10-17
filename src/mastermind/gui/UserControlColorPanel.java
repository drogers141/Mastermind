/**
 * Dave Rogers
 * dave at drogers dot us
 * This software is for instructive purposes.  Use at your own risk - not meant to be robust at all.
 * Feel free to use anything, credit is appreciated if warranted.
 */
package mastermind.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;

import javax.swing.*;

import mastermind.MasterMind;
import mastermind.event.ClickHandler;

/**
 * The UsersControlColorPanel extends UserControlPanel to
 * provide a panel that can display colors to be selected.
 */
public class UserControlColorPanel extends UserControlPanel {
    public UserControlColorPanel() {}

    /**
     * Creates a panel for the user control which displays a color.
     * @param ml MouseListener - listens to this panels clicks
     * @param color Object - This panel's color
     * @param background Color - background color
     * @param prefWidth int - preferred panel width
     * @param prefHeight int - preferred panel height
     */
    public UserControlColorPanel(MouseListener ml, Object color,
                                 Color background, int prefWidth,
                                 int prefHeight) {
        setPreferredSize(new Dimension(prefWidth, prefHeight));
        makeBorder();
        setBackground(background);
        // put exception code in here--if bad cast
        this.color = (Color) color;
        addMouseListener(ml);
        highlightColor = MasterMind.getHighlightColor(this.color);
        usingHighlightColor = true;
    }

    // Creates this panel's border
    private void makeBorder() {
        setBorder(BorderFactory.createBevelBorder(1, this.getBackground(),
                                                  Color.darkGray));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        //g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        //                     RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                             RenderingHints.VALUE_RENDER_QUALITY);

        Insets insets = getInsets();
        double width = (double) (getWidth() - insets.left - insets.right);
        double ht = (double) (getHeight() - insets.left - insets.right);
        circle = new Ellipse2D.Double(width / 8.0, ht / 8.0,
                                      width * 3 / 4.0, ht * 3 / 4.0);
        shadow(g2d);
        g2d.setPaint(color);
        g2d.fill(circle);
        g2d.setColor(Color.black);
        g2d.draw(circle);
        make3d(g2d);
        if (isSelected) {
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.setPaint(Color.white);
            g2d.draw(new Ellipse2D.Double(circle.getMinX() - 2,
                                          circle.getMinY() - 2,
                                          circle.getWidth() + 4,
                                          circle.getHeight() + 4));
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
        //g2d.setPaint(this.getBackground().darker());
        g2d.setPaint(this.getBackground().darker().darker());
        g2d.fill(sh);
    }

    /**
     * Checks to see if point (x, y) is in this panel's circle.
     * @param x int - x coordinate of point.
     * @param y int - y coordinate of point.
     * @return boolean - <code>true</code> if (x, y) is in the circle,
     * <code>false</code> otherwise.
     */
    public boolean isInCircle(int x, int y) {
        return circle.contains((double) x, (double) y);
    }

    /**
     * Returns this panel's circle's color.
     * @return Color - the color of this panel's circle.
     */
    public Color getColor() {
        return color;
    }

    // This panel's color
    private Color color;
    // A hardcoded highlight color--because I don't have time
    // to really learn colors better yet ...
    private Color highlightColor;
    // A flag to set to signal we are using a highlight color
    // rather than the regular algorithm
    private boolean usingHighlightColor = false;
    // The circle enclosing the displayed color
    private Ellipse2D circle;

    /**
     * This main is for testing only
     * @param args String[]
     */
    public static void main(String[] args) throws IOException {
        ClickHandler ch = new ClickHandler();
        int length2 = MasterMind.baseColors.length;
        UserControlColorPanel[] morePanels2 =
                new UserControlColorPanel[length2];
        Box anotherBox2 = Box.createHorizontalBox();
        for (int i = 0; i < length2; i++) {
            morePanels2[i] = new UserControlColorPanel(ch,
                    MasterMind.baseColors[i],
                    Color.lightGray, 50, 50);
            anotherBox2.add(morePanels2[i]);
            if (i % 3 == 0) {
                morePanels2[i].select();
            }
        }
        JFrame f3 = new JFrame();
        f3.getContentPane().add(anotherBox2);
        f3.pack();
        f3.setLocation(100, 400);
        f3.setVisible(true);

    }
}
