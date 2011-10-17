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
 * Displays the results of a single guess.  Black circles indicate
 * a positional match, and white ones indicate a nonpositional
 * match.
 */
public class GuessResultPanel extends JPanel {
    public GuessResultPanel() {}

    public GuessResultPanel(int prefWidth, int prefHeight,
                            Color background) {
        setPreferredSize(new Dimension(prefWidth, prefHeight));
        makeBorder();
        setBackground(background);
    }

    // make this panels border
    private void makeBorder() {
        setBorder(BorderFactory.createBevelBorder(1, this.getBackground(),
                                                  Color.darkGray));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                             RenderingHints.VALUE_RENDER_QUALITY);
        Color notBlack = new Color(60, 60, 60);
        Insets insets = getInsets();
        double w = (double) (getWidth() - insets.left - insets.right);
        double h = (double) (getHeight() - insets.left - insets.right);

        // divide length into 2 rows to draw "peg holes" in
        int bottom = guessLength / 2;
        int top = guessLength - bottom;
        int t = 0; // keep track of how many holes used in top
        int b = 0; // and bottom rows
        //first black circles for positionally correct
        int i; // count holes filled whether black or white
        Ellipse2D small;
        if (!isShowingGuessResult) {
            numCorrectPosition = numCorrectNotPosition = 0;
        }

        double radiusFactor = 3.5; // relates circle's radius to panel width
        for (i = numCorrectPosition; i > 0; i--) {
            if (t >= top) {
                break;
            }
            black = new Ellipse2D.Double(w * ((t + 1) /
                                              (double) (top + 1)) -
                                         w / (2 * radiusFactor),
                                         h / 3.0 - h / (2 * radiusFactor),
                                         w / radiusFactor, h / radiusFactor);
            shadow(g2d, black);
            g2d.setPaint(notBlack);
            g2d.fill(black);
            make3d(g2d, black);
            t++;
        }
        for (; i > 0; i--) {
            if (b >= bottom) {
                break;
            }
            black = new Ellipse2D.Double(w * ((b + 1) /
                                              (double) (top + 1)) -
                                         w / (2 * radiusFactor),
                                         h * 2 / 3.0 -
                                         h / (2 * radiusFactor),
                                         w / radiusFactor, h / radiusFactor);
            shadow(g2d, black);
            g2d.setPaint(notBlack);
            g2d.fill(black);
            make3d(g2d, black);

            b++;
        }
        //now fill/cover holes with white circles for correct
        // but not for position

        for (i = numCorrectNotPosition; i > 0; i--) {
            if (t >= top) {
                break;
            }
            white = new Ellipse2D.Double(w * ((t + 1) /
                                              (double) (top + 1)) -
                                         w / (2 * radiusFactor),
                                         h / 3.0 - h / (2 * radiusFactor),
                                         w / radiusFactor, h / radiusFactor);
            shadow(g2d, white);
            g2d.setPaint(Color.white);
            g2d.fill(white);
            make3d(g2d, white);
            t++;
        }
        for (; i > 0; i--) {
            if (b >= bottom) {
                break;
            }
            white = new Ellipse2D.Double(w * ((b + 1) /
                                              (double) (top + 1)) -
                                         w / (2 * radiusFactor),
                                         h * 2 / 3.0 -
                                         h / (2 * radiusFactor),
                                         w / radiusFactor, h / radiusFactor);
            shadow(g2d, white);
            g2d.setPaint(Color.white);
            g2d.fill(white);
            make3d(g2d, white);
            b++;
        }
        // The rest of the method draws the small circles for the "holes"
        g2d.setColor(Color.black);
        int total = top + bottom; // guessLength ...
        int filled = numCorrectPosition + numCorrectNotPosition;
        int remain = total - filled;
        if (remain == 0) {
            return;
        }
        for (; remain > bottom; remain--) { // top peg holes
            if (remain <= bottom) {
                break;
            }
            int pos = top + 1 - (remain - bottom);
            small = new Ellipse2D.Double(w * pos /
                                         (double) (top + 1) - w / 20.0,
                                         h / 3.0 - h / 12.0, w / 6.0,
                                         h / 6.0);
            g2d.fill(small);
        }
        for (; remain > 0; remain--) {
            int pos = bottom + 1 - remain;
            small = new Ellipse2D.Double(w * pos /
                                         (double) (top + 1) - w / 20.0,
                                         h * 2 / 3.0 - h / 12.0, w / 6.0,
                                         h / 6.0);
            g2d.fill(small);
        }
    }

    // Makes the circle appear a little more 3d
    // Pre: circle is initialized to size and a "flat circle"
    // has already been drawn and filled
    private void make3d(Graphics2D g2d, Ellipse2D circle) {
        double w = circle.getWidth();
        double h = circle.getHeight();
        double x = circle.getMinX();
        double y = circle.getMinY();
        if (circle == black) {
            g2d.setPaint(Color.white);
            g2d.setStroke(new BasicStroke(1.2f));
            g2d.draw(new Arc2D.Double(x + 1, y + 1, w, h,
                                      88, 94, Arc2D.OPEN));
        } else if (circle == white) {
            g2d.setPaint(new Color(192, 192, 192));
            //g2d.setPaint(new Color(255, 0, 0));
            Arc2D arc = new Arc2D.Double(x - w * .1, y - h * .1, w * .9, h * .9,
                                         270, 90, Arc2D.CHORD);
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.draw(new Arc2D.Double(x - w * .1, y - h * .1, w * .9, h * .9,
                                      285, 60, Arc2D.OPEN));
        }
    }

    // Draws a shadow to the lower right of the circle/sphere/peg
    // Pre: circle is initialized, but not filled yet
    private void shadow(Graphics2D g2d, Ellipse2D circle) {
        double w = circle.getWidth();
        double h = circle.getHeight();
        double cx = circle.getCenterX();
        double cy = circle.getCenterY();
        Ellipse2D sh = new Ellipse2D.Double(cx - w * .35, cy - w * .35,
                                            w, h);
        g2d.setPaint(Color.black);
        g2d.fill(sh);
    }

    /**
     * Set the results of a guess to be displayed by this panel.
     * @param correctPosition int - matches in the correct position
     * @param correctNotPosition int - matches not in the correct position
     */
    public void setResult(int correctPosition, int correctNotPosition) {
        isShowingGuessResult = true;
        if (correctPosition + correctNotPosition > guessLength) {
            System.out.println("positional + non-positional matches > " +
                               "length of guess");
            return;
        }
        numCorrectPosition = correctPosition;
        numCorrectNotPosition = correctNotPosition;
        repaint();
    }

    /**
     * Clears the panel and resets it to the original state of
     * not showing a result.
     */
    public void clearPanel() {
        isShowingGuessResult = false;
        repaint();
    }
    // positional match count
    private int numCorrectPosition = 0;
    // nonpositional match count
    private int numCorrectNotPosition = 0;
    // set to true once this panel has had a result set
    private boolean isShowingGuessResult = false;
    // The number of positions in a guess
    private int guessLength = 4;
    // circles for drawing the white and black result peg heads
    private Ellipse2D black, white;
    /**
     * For testing only
     */
    public static void main(String[] args) {
        JFrame f = new JFrame("Test");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GuessResultPanel[] panels = new GuessResultPanel[4];
        Box box = Box.createVerticalBox();
        for (int i = 0; i < panels.length; i++) {
            panels[i] = new
                        GuessResultPanel(MasterMind.boardUnitHt,
                                         MasterMind.boardUnitHt,
                                         Color.gray);
            box.add(panels[i]);
        }
        f.getContentPane().add(box);
        f.setLocation(100, 100);
        f.pack();
        for (int i = 0; i < panels.length; i++) {
            panels[i].setResult(i, 4 - i);
        }

        f.setVisible(true);
    }


}
