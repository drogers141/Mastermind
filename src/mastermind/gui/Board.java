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

import mastermind.GuessInfo;
import mastermind.MasterMind;
import mastermind.event.ClickHandler;

/**
 * The <code>Board</code> class is a component of the Mastermind's
 * View that forms the board where the user puts the elements chosen that
 * represent his guess--eg clicks spots to select a location for a
 * particular colored peg.  <br>
 * Note that for now this board will only deal with color panels to make
 * things easier.  To change this in the future, GuessColorPanel would
 * have to have a super class with a method indicating its object has
 * been selected--a generalized version of GuessColorPanel's isInCircle()
 */
public class Board extends JPanel {

    // The panels that make up the board:
    private GuessPanel[][] guessPanels;// the guesses
    private GuessResultPanel[] resultPanels;// results of the guesses

    // unitWidth is the width of one of the guessPanels[]
    private static int unitWidth = MasterMind.boardUnitHt;
    // unitHeight is the height of one of the guessPanels[]
    private static int unitHeight = MasterMind.boardUnitHt;
    // background color for the board
    private Color backgroundColor = new Color(160, 160, 160);

    // holds the elements to select from -- colors, numbers, etc
    private Object[] elements;

	public Board() {}

    /**
     * Creates a board
     * @param numOfPositionsInGuess int - length of the guess
     * @param numOfGuesses int - number of guesses allowed for this game
     * @param ml MouseListener - listener for this board
     * @param elements Object[] - the elements used for playing this
     * game--in this implementation elements are Colors or Integers
     * @param background Color - background color for boards panels
     */
    public Board(int numOfPositionsInGuess, int numOfGuesses,
                 MouseListener ml, Object[] elements, Color background) {
        this.elements = elements;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        makeBorder();
        guessPanels = new
                      GuessPanel[numOfGuesses][numOfPositionsInGuess];
        resultPanels = new GuessResultPanel[numOfGuesses];
        Box boxes[] = new Box[numOfGuesses];
        for (int i = numOfGuesses - 1; i >= 0; i--) {
            boxes[i] = Box.createHorizontalBox();
            Box guesses = Box.createHorizontalBox();
            guesses.setBorder(BorderFactory.createLoweredBevelBorder());
            //guesses.setBorder(BorderFactory.createBevelBorder(
            //                                    0, backgroundColor,
            //                                    Color.darkGray));

            Box result = Box.createHorizontalBox();
            result.setBorder(BorderFactory.createLoweredBevelBorder());
            //result.setBorder(BorderFactory.createBevelBorder(
            //                                    0, backgroundColor,
            //                                    Color.darkGray));
            if (elements[0] instanceof Color) {
                for (int j = 0; j < guessPanels[i].length; j++) {
                    guessPanels[i][j] = new GuessColorPanel(unitWidth,
                            unitHeight,
                            ml, background);
                    guesses.add(guessPanels[i][j]);
                }
            } else if (elements[0] instanceof Integer) {
                for (int j = 0; j < guessPanels[i].length; j++) {
                    guessPanels[i][j] = new GuessNumberPanel(unitWidth,
                            unitHeight,
                            ml, background);
                    guesses.add(guessPanels[i][j]);
                }
            }
            resultPanels[i] = new GuessResultPanel(unitWidth, unitHeight,
                    background);
            result.add(resultPanels[i]);
            boxes[i].add(guesses);
            boxes[i].add(result);
            boxes[i].setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createBevelBorder(1,
                    backgroundColor, Color.darkGray),
                            BorderFactory.createEmptyBorder()));
            add(boxes[i]);
        }
    }

    /**
     * Reinitialize board for another game.
     */
    public void init() {
        for (int i = 0; i < guessPanels.length; i++) {
            for (int j = 0; j < guessPanels[0].length; j++) {
                guessPanels[i][j].clearPanel();
            }
            resultPanels[i].clearPanel();
        }
    }

    /**
     * Returns the row that a GuessColorPanel is in.
     * @param panel GuessColorPanel - reference to a panel
     * @return int - the row index of the panel if found,
     * -1 if the panel is not found
     */
    public int getRowIndex(GuessPanel panel) {
        for (int row = 0; row < guessPanels.length; row++) {
            for (int col = 0; col < guessPanels[0].length; col++) {
                if (panel == guessPanels[row][col]) {
                    return row;
                }
            }
        }
        return -1;
    }

    /**
     * Get an element by index.
     * @param index int - the index of the element
     * @return Object - the element
     */
    public Object getElement(int index) {
        return elements[index];
    }

    /**
     * Returns references to a row of panels
     * @param index int - index of row - get this with getRowIndex()
     * @return GuessColorPanel[] - array of refs to the row's panels
     */
    public GuessPanel[] getPanelsInRow(int index) {
        return guessPanels[index];
    }

    /**
     * Displays the result of a guess.
     * @param rowIndex int
     * @param result GuessInfo
     */
    public void showGuessResult(int rowIndex, GuessInfo result) {
        resultPanels[rowIndex].setResult(result.getPositionMatches(),
                                         result.getNonPositionalMatches());
    }

    /**
     * This main is for testing only
     * @param args String[]
     */
    public static void main(String[] args) {
        JFrame f = new JFrame("Test");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ClickHandler ch = new ClickHandler();
        //Board board = new Board(4, 8, ch,
        //                        Color.lightGray);
        //ch.addBoard(board);
        //f.getContentPane().add(board);
        f.pack();
        f.setVisible(true);
    }

    // Make the board's border
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

}
