/**
 * Dave Rogers
 * dave at drogers dot us
 * This software is for instructive purposes.  Use at your own risk - not meant to be robust at all.
 * Feel free to use anything, credit is appreciated if warranted.
 */
package mastermind;


/**
 * A <code>GuessInfo</code> object holds the data for the results
 * of a single guess.
 */
public class GuessInfo {
	private Engine engine;
    // The user's guess
    private int[] guess;

    // The number of positional matches
    private int positionMatches;

    // The number of non-positional digit matches
    private int nonPositionalMatches;

    /**
     * Creates a <code>GuessInfo</code> object with all
     * values initialized to -1.
     */
    public GuessInfo(int guessLength) {
        guess = new int[guessLength];
        for(int i=0; i<guess.length; i++)
            guess[i] = -1;
        positionMatches = -1;
        nonPositionalMatches = -1;
    }

    /**
     * Creates an initialized <code>GuessInfo</code> object.
     * @param guess int[] user's guess as an array of integers
     * @param position int number of positional matches
     * @param digit int number of non-positional digit matches
     */
    public GuessInfo(int[] guess, int position, int digit) {
        this.guess = guess;
        positionMatches = position;
        nonPositionalMatches = digit;
    }

    /**
     * Sets a <code>GuessInfo</code> object's data.
     * @param guess int[] user's guess as an array of integers
     * @param position int number of positional matches
     * @param notPosition int number of non-positional digit matches
     */
    public void set(int[] guess, int position, int notPosition) {
        this.guess = guess;
        positionMatches = position;
        nonPositionalMatches = notPosition;
    }

    /**
     * Returns the GuessInfo object.
     * @return GuessInfo object returned
     */
    public GuessInfo get() {
        GuessInfo gr = new GuessInfo(guess, positionMatches,
                                         nonPositionalMatches);
        return gr;
    }

    /**
     * Returns user's guess.
     * @return int[] user's guess
     */
    public int[] getGuess() {
        return guess;
    }

    /**
     * Returns position matches.
     * @return int how many positions were matched
     */
    public int getPositionMatches() {
        return positionMatches;
    }

    /**
     * Returns digit matches.
     * @return int how many digits matched, but not positions
     */
    public int getNonPositionalMatches() {
        return nonPositionalMatches;
    }

    /**
     * Returns String version.
     * @return String GuessInfo as String
     */
    public String toString() {
        StringBuffer s = new StringBuffer("[");
        for (int i = 0; i < guess.length; i++) {
            s.append(guess[i] + ", ");
        }
        //s.delete(s.length()-2,s.length());
        if(s.length() >= 2) {
        	s.delete(s.length() - 2, s.length());
        }
        s.append("]  p=" + positionMatches + "  d=" +
                nonPositionalMatches);
        return s.toString();
    }
}
