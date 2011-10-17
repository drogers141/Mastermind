/**
 * Dave Rogers
 * dave at drogers dot us
 * This software is for instructive purposes.  Use at your own risk - not meant to be robust at all.
 * Feel free to use anything, credit is appreciated if warranted.
 */
package mastermind;

import java.io.*;


/**
 * Tests the Mastermind engine using a command line interface.
 */
public class MasterMindTester {
    private Engine engine;

    public Engine getEngine() {
		return engine;
	}

	public MasterMindTester() {
		this(4, 10, 16);
    }
	/**
	 * Set numElements to the number of elements allowed--the elements set will
	 * contain digits from 0 to numElements-1
	 * 
	 * @param guessLength
	 * @param numElements
	 * @param maxNumGuesses--int--number of guesses which determine a loss
	 */
	public MasterMindTester(int guessLength, 
								int numElements,
								int maxNumGuesses) {
		this.guessLength = guessLength;
		this.domainSize = numElements;
		this.numberOfGuesses = maxNumGuesses;
		guess = new int[guessLength];
		for (int i = 0; i < guessLength; i++) {
			guess[i] = -1;
		}
        engine = new Engine(guessLength, domainSize, numberOfGuesses);
        engine.init();
    }
    private int domainSize; // Use these to initialize the engine
    private int numberOfGuesses;
    private int guessLength;
    private int[] guess;

    public String arrayToString(int[] guess) {
        StringBuffer s = new StringBuffer("[");
        for (int i = 0; i < guess.length; i++) {
            s.append(guess[i] + ", ");
        }
        if(s.length() >= 2) {
        	s.delete(s.length() - 2, s.length());
        }
        s.append("]");
        return s.toString();
    }

    //int guessAsInt;
    public void setGuessArray(int intGuess) {
        for (int i = guessLength - 1; i >= 0; i--) {
            int powOf10 = (int) Math.round(Math.pow(10, i));
            guess[guessLength - 1 - i] = intGuess / powOf10;
            intGuess %= powOf10;
        }
    }

    public static void main(String[] args) throws IOException {
        MasterMindTester mt = new MasterMindTester();
        BufferedReader in = new BufferedReader(new InputStreamReader
                                               (System.in));

        //mt.engine.init(mt.domainSize, mt.numberOfGuesses);
        String sn = mt.arrayToString(mt.engine.getSecretNumber());
        System.out.println("Secret number is " + sn);
        for (; ; ) {
            System.out.println("Enter a four digit guess:");
            String strInt = in.readLine();
            int intGuess = Integer.parseInt(strInt);
            mt.setGuessArray(intGuess);
            System.out.println("guess = " + mt.arrayToString(mt.guess));
            GuessInfo gr = mt.engine.compare(mt.guess);
            System.out.println(gr.toString());
            if (mt.engine.hasWon()) {
                System.out.println("You the man!");
                break;
            } else if (mt.engine.hasLost()) {
                System.out.println("Ouch, sorry dude ...");
                break;
            }
        }
    }


}
