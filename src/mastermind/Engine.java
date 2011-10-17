/**
 * Dave Rogers
 * dave at drogers dot us
 * This software is for instructive purposes.  Use at your own risk - not meant to be robust at all.
 * Feel free to use anything, credit is appreciated if warranted.
 */
package mastermind;

import mastermind.event.*;
import mastermind.gui.*;
import mastermind.ai.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.awt.geom.*;

/**
 * <code>Engine</code> is the engine for the <code>MasterMind</code>
 * game. It represents the Model in the Model View Controller design
 * pattern.  As far as this engine is concerned, guesses are
 * arrays of integers, so the documentation here will mention numbers
 * and parenthetically note that they may be referring to colors (or
 * elements) in the basic implementation of the Mastermind game.
 * Note that using decimal integers we have up to 10 digits to work
 * with, but a larger base can be chosen if desired.<br>
 * <code>Engine</code> takes a user's guess in the form of
 * <code>int guess[SECRET_NUMBER_LENGTH]</code> and compares it to
 * <code>int secretNumber[SECRET_NUMBER_LENGTH]</code> to generate
 * the typical Mastermind data.
 * @author Dave Rogers
 * 
 */
public class Engine {

    // Together the board and users control and their sub-objects
    // make up the the View for Mastermind
    // The board
    private Board board;

    // The users control
    private UserControl control;

    // This game
    private MasterMind game;
    
    // ai player--see mastermind.ai
    private Player aiPlayer = null;

    /**
     * The length of the secret number and therefore of all guesses.
     * If desired larger length possibilities can be used, though 4
     * seems to be the standard.
     */
    public int guessLength;

    // The array that holds the secret or winning series of digits
    // again--note that this does not have to mean decimal digits,
    // it could be any base
    private int[] secretNumber;

    // The number of elements (colors, numbers, etc) selected by
    // the user for this game--this equals how many different digits
    // to choose from when generating the secret number
    private int numSelectedElements;

    // The total number of elements possible to play with for
    // this engine object--size of the array of elements the View
    // and Controller have somewhere in a back room...
    private int totalElements;

    // How many guesses the user is allowed before the game is
    // considered lost--this should relate to domainSize
    private int numGuessesAllowed;

    // The array of guesses--from 0 to currentGuessIndex-1 are
    // guesses already made
    private GuessInfo[] guessArray;

    // The index of guessArray representing the last guess
    private int currentGuessIndex;

    // Random number generator for the secret number
    private Random rand = new Random();

    // Last element selected by user clicking on control
    private int currentlySelectedIndex;

    // Array of indexes to elements selected for play in this game
    int[] selectedIndexes;

    // ~~~~~~~ Boolean flags ~~~~~~~~~~

    // True if last guess is a match and user has won
    private boolean hasWon = false;

    // True if user has exceeded the number of guesses allowed
    // and therefore has lost
    private boolean hasLost = false;

    // Safety measure to make sure the game is initialized before
    // trying to play
    private boolean gameIsInitialized = false;

    /**
     * Creates an <code>Engine</code> object.
     */
    public Engine() {}

    /**
     * Creates an <code>Engine</code> without a board or
     * control unit associated with it.
     * @param guessLength int - how many elements are in a guess,
     * eg four colors or four numbers is typical
     * @param totalNumElements int - the total number of elements
     * available to select from in this instance of Mastermind--
     * ie the size of the array of Colors, Integers, etc being
     * played with
     * @param numberOfGuessesAllowed int - number of guesses allowed
     * before game is considered lost
     */
    public Engine(int guessLength,
                  int totalNumElements,
                  int numberOfGuessesAllowed) {
        totalElements = totalNumElements;
        numSelectedElements = totalElements;
        this.guessLength = guessLength;
        secretNumber = new int[this.guessLength];
        numGuessesAllowed = numberOfGuessesAllowed;
    }

    /**
     *Creates an <code>Engine</code> for a gui implementation--with
     * a board and a user control
     * @param b Board - the board for this engine
     * @param c UserControl - the user control for this engine
     * @param g MasterMind - reference to the game using this engine
     * @param guessLength int - how many elements are in a guess,
     * eg four colors or four numbers is typical
     * @param numberOfGuessesAllowed int - number of guesses allowed
     * before game is considered lost
     * @param totalNumElements int - the total number of elements
     * available to select from in this instance of Mastermind--
     * ie the size of the array of Colors, Integers, etc being
     * played with
     */
    public Engine(Board b, UserControl c, MasterMind g,
                  int guessLength,
                  int numberOfGuessesAllowed,
                  int totalNumElements) {
        board = b;
        control = c;
        game = g;
        this.guessLength = guessLength;
        secretNumber = new int[this.guessLength];
        numGuessesAllowed = numberOfGuessesAllowed;
        totalElements = totalNumElements;
        control.showSelectedElementAtTop(0);
        currentlySelectedIndex = 0;
    }

    /**
     * Handles anything to do when a user clicks somewhere on the board.
     * @param gp GuessPanel - the panel that was clicked
     */
    public void boardClicked(GuessPanel gp) {
        if (!gameIsInitialized) {
            return;
        }
        //*************************************************
         //******** DEBUGGING *****************************
          //**
           //game.showGameLost();
           //game.showWinningCombination();
           //game.showGameWon();

           int row = board.getRowIndex(gp);
        if (isCurrentGuessIndex(row)) {
            gp.select(board.getElement(currentlySelectedIndex));
            gp.setIndex(currentlySelectedIndex);
        } // get the indexes of selected elements in guess
        if (isCurrentGuessIndex(row) && guessIsComplete()) {
            GuessPanel[] panels =
                    board.getPanelsInRow(currentGuessIndex);
            int[] guessInt = new int[panels.length];
            for (int i = 0; i < panels.length; i++) {
                guessInt[i] = panels[i].getIndex();
            } // make the guess and tell the board to show the result
            GuessInfo result = compare(guessInt);
            board.showGuessResult(currentGuessIndex - 1, result);
            if (hasWon || hasLost) {
                // notify user control game is over
                control.stop();
                // reset game to uninitialized state
                gameIsInitialized = false;
                if (hasWon) {
                    // *********** Game Won Logic ***************
                    game.showGameWon();
                } else {
                    // *********** Game Lost Logic **************
                    game.showGameLost();
                    game.showWinningCombination();
                }
            }
        }
    }

    /**
     * Handles anything to do when a user clicks somewhere on the user
     * control.
     * @param panel UserControlPanel
     */
    public void userControlClicked(UserControlPanel panel) {
        int index = panel.getIndex();
        //Color color = uccp.getColor();
        if (!gameIsInitialized) {
            if (panel.isSelected()) {
                panel.deselect();
            } else {
                panel.select();
            }
        } else if (isPossibleSelection(index)) {
            control.showSelectedElementAtTop(index);
            currentlySelectedIndex = index;
        }
    }

    /**
     * Handles anything to do when a user clicks the start button (which
     * is also the stop button.)
     * --for ai--if autopilot is on, clickHandler will be disabled until
     * --ai calls this a second time (ie to stop)
     * --and ai player is created here
     */
    public void startButtonClicked() {
        if (!gameIsInitialized) {
            int[] indexes = control.getSelectedElementIndexes();
            if (indexes == null || indexes.length < 3) {
                return;
            }
            init(indexes);
            if (!isPossibleSelection(currentlySelectedIndex)) {
                currentlySelectedIndex = selectedIndexes[0];
                control.showSelectedElementAtTop(currentlySelectedIndex);
            }
            control.start();
            game.newGame();
            
            //** ai autopilot stuff
            if(game.autoPilotIsOn()) {
            	game.getClickHandler().setEnabled(false);
            	
//            	try {
//					SwingUtilities.invokeAndWait(new Runnable() {
//						public void run() {
//					    	aiPlayer = new Player(game);
//					    	aiPlayer.play();
//						}
//					});
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (InvocationTargetException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
            	
            	aiPlayer = new Player(game);
            	aiPlayer.play();
            }
        } else {
            gameIsInitialized = false;
            control.stop();
            board.init();
            
          //*** re-enable click handling if on autopilot
            if(game.autoPilotIsOn()) {
            	game.getClickHandler().setEnabled(true);
            }
        }
    }

    /**
     * Initializes the Mastermind game's engine for the next game.
     * This function must be called before the engine will allow
     * a (new) game to be played.
     * @param indexes int[] - the indexes of the elements selected
     * for play in this game
     */
    public void init(int[] indexes) {
        selectedIndexes = indexes;
        numSelectedElements = indexes.length;
        guessArray = new GuessInfo[numGuessesAllowed];
        currentGuessIndex = 0;
        for (int i = 0; i < secretNumber.length; i++) {
            secretNumber[i] =
                    indexes[rand.nextInt(numSelectedElements)];
        }
        hasWon = hasLost = false;
        gameIsInitialized = true;
        board.init();
    }

    /**
     * Initializes the Mastermind game's engine for the next game.
     * This function must be called before the engine will allow
     * a (new) game to be played.
     * Default version of init()--suitable for a command line
     * interface--board and control unit are not initialized.
     * ie there is no gui view...
     * Pre: totalElements, guessLength, and numGuessesAllowed
     * must be initialized--use the no gui constructor.
     */
    public void init() {
        guessArray = new GuessInfo[numGuessesAllowed];
        currentGuessIndex = 0;
        for (int i = 0; i < secretNumber.length; i++) {
            secretNumber[i] = rand.nextInt(totalElements);
        }
        hasWon = hasLost = false;
        gameIsInitialized = true;
    }

    /**
     * Checks if an index is that of the current, or active, guess
     * @param index int - index of row representing guess in 2d array
     * @return boolean - true if index is same as the current guess's
     */
    public boolean isCurrentGuessIndex(int index) {
        return (index == currentGuessIndex);
    }

    // Returns true if the element with the given index is one of
    // the active elements the user has selected for this game
    private boolean isPossibleSelection(int index) {
        int[] indexes = control.getSelectedElementIndexes();
        for (int i = 0; i < indexes.length; i++) {
            if (index == indexes[i]) {
                return true;
            }
        }
        return false;
    }

    // Returns true if all the panels in the row of the current
    // guess have been selected
    private boolean guessIsComplete() {
        GuessPanel[] panels =
                board.getPanelsInRow(currentGuessIndex);
        for (int i = 0; i < panels.length; i++) {
            if (!panels[i].isSelected()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Indicates the game has been won.
     * @return boolean <code>true</code> if user has won,
     * <code>false</code> otherwise
     */
    public boolean hasWon() {
        return hasWon;
    }

    /**
     * Indicates the game has been lost.
     * @return boolean <code>true</code> if user has lost,
     * <code>false</code> otherwise
     */
    public boolean hasLost() {
        return hasLost;
    }

    /**
     * Indicates whether or not the game has been initialized by
     * calling init()
     * @return boolean <code>true</code> if the game has been initialized,
     * initialized, <code>false</code> otherwise
     */
    public boolean gameIsInitialized() {
        return gameIsInitialized;
    }

    /**
     * Returns the history of guesses and results.
     * @return GuessInfo[] history of guesses and their results
     * as GuessInfo objects
     */
    public GuessInfo[] getGuessArray() {
        return guessArray;
    }

    /**
     * Returns the current guess index number with the first guess
     * being 0.
     * @return int the number guesses made so far - 1.
     */
    public int getCurrentGuessIndex() {
        return currentGuessIndex;
    }

    /**
     * For debugging purposes--get the secret number.
     * @return int[] the secret number as an array
     */
    public int[] getSecretNumber() {
        return secretNumber;
    }

    /**
     * The workhorse function of the engine, compares the array
     * guess with the secret number and returns the results as a
     * GuessInfo object.  Sets appropriate boolean flags if the
     * comparison causes the user to win or lose. <br>
     * Precondition: Game has been initialized, by calling init()
     * Side effects: sets boolean members hasWon and hasLost
     * appropriately, and increments currentGuessIndex.
     * Does not reset member gameIsInitialized.
     * @param guess int[] the user's guess as an int array of size
     * guessLength
     * @return GuessInfo the result of the comparison with the
     * secret number
     */
    public GuessInfo compare(int guess[]) {
        if (hasWon || hasLost || !gameIsInitialized) {
            System.out.println("In compare(), but have won, lost, " +
                               "or game is not initialized.\nDon't forget " +
                               "to put throw statements here.");
            System.exit(1);
        }
        int posMatch = 0, nonPosMatch = 0; // count matches
        int[] matchedS = new int[totalElements];
        int[] matchedG = new int[totalElements];
        for (int i = 0; i < matchedS.length; i++) {
            matchedS[i] = matchedG[i] = 0;
        }
        for (int i = 0; i < guess.length; i++) { // count occurrences
            matchedS[secretNumber[i]]++; // of each matching digit
            matchedG[guess[i]]++;
            if (guess[i] == secretNumber[i]) { // positional matches
                posMatch++;
            }
        }
        for (int i = 0; i < totalElements; i++) {
            nonPosMatch += Math.min(matchedS[i], matchedG[i]);
        }
        nonPosMatch -= posMatch;
        GuessInfo gr = new GuessInfo(guess, posMatch, nonPosMatch);
        guessArray[currentGuessIndex++] = gr;
        if (posMatch == guess.length) {// check for win first--in
            hasWon = true;	// case win on last guess allowed
        } else if(currentGuessIndex == numGuessesAllowed) {
            hasLost = true;
        }
        return gr;
    }
    
    /**
     * Returns ref to the User Control of this game.
     * 
     * @return
     */
    public UserControl getUserControl() {
    	return control;
    }
    
    /**
     * Returns ref to the board for this game.
     * 
     * @return
     */
    public Board getBoard() {
    	return board;
    }

	public void setGameIsInitialized(boolean gameIsInitialized) {
		this.gameIsInitialized = gameIsInitialized;
	}
    
    
}
