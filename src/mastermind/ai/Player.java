/**
 * Dave Rogers
 * dave at drogers dot us
 * This software is for instructive purposes.  Use at your own risk - not meant to be robust at all.
 * Feel free to use anything, credit is appreciated if warranted.
 */
package mastermind.ai;

import mastermind.*;
import mastermind.gui.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


/**
 * The player encapsulates the interaction between the ai and the MasterMind game.
 * See Brain for the implementation of the ai.
 * 
 * @author drogers
 *
 */
public class Player implements ActionListener {
	private Brain brain = null;
	
	// game this player plays
	private MasterMind mm = null;
	
	// this game's engine
	private Engine engine = null;
	
	// amount of time to pause between guesses in ms
	private int guessDelay = 500;
	
	// guess counter--keep track of iterations
	private int gCounter = 0;
	
	private Timer timer = new Timer(guessDelay, this);
	
	public Player() {}
	
	public Player(MasterMind game) {
		this.mm = game;
		this.engine = mm.getEngine();
		this.brain = new Brain(mm);
	}
	
	/**
	 * Main game loop.
	 * 
	 * Pre:  game must be initialized properly.
	 */
	public void play() {
		timer.start();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		GuessInfo guessResult = makeGuess();
		// end of game stuff
		if(engine.hasWon() || engine.hasLost() ) {
			engine.setGameIsInitialized(false);
			engine.getUserControl().stop();

			// customize me for ai
			if (engine.hasWon()) {	                    
				mm.showGameWon();
			} else {
				mm.showGameLost();
				mm.showWinningCombination();
			}
			timer.stop();
			mm.getClickHandler().setEnabled(true);
			
			return;
		}
		brain.update(guessResult);
		gCounter++;
	}
	
	public GuessInfo makeGuess() {
		Board board = engine.getBoard();
		GuessInfo result = null;
		
		// array of the indices of the elements in the guess
		int[] guess = brain.nextGuess();
		// show on the board
		GuessPanel[] panels =
            board.getPanelsInRow(engine.getCurrentGuessIndex());

		for(int i=0; i<guess.length; i++) {
			int index = guess[i];
			panels[i].select(board.getElement(index));
			panels[i].setIndex(index);
		}
		result = engine.compare(guess);
		board.showGuessResult(engine.getCurrentGuessIndex() - 1, result);
		
		return result;
	}
	
	public void observeAndThink(GuessInfo result) {
		brain.update(result);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MasterMind mm = new MasterMind();
		Player p = null;
		Board board = null;
		GuessInfo result = null;
		
        try {
            mm.init("Colors", 10, 10, 4, false);
            p = new Player(mm);
            result = p.makeGuess();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}

}
