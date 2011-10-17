/**
 * Dave Rogers
 * dave at drogers dot us
 * This software is for instructive purposes.  Use at your own risk - not meant to be robust at all.
 * Feel free to use anything, credit is appreciated if warranted.
 */
package mastermind.ai;

import mastermind.*;

import java.util.*;

/**
 * Supplies guesses for the Mastermind ai Player using an algorithm that
 * reasons based on a list of inferences gained from the results of guesses.
 * Note that in the comments and names of members/methods, that elements refers
 * to either the colors or numbers used in the game.  In any of algorithms,
 * the element is referred to by its index which is found in elementIndices.
 * 
 * In general, the information gained from guessing is kept by the inferences 
 * list.  There are 2 main actors that sweep left to right and forward through
 * the list of inferences.  These are beingConsidered, which is an index of an
 * element (eg a color)  that is being tried to see if it it is in the secret 
 * code, and beingFixed, which is an Inference whose element is known to be in
 * the secret code, but it's position is being "fixed."  Once an Inference's 
 * positions list is only one position long, that Inference's position is "tied"
 * to the element, and the Inference will be fixed.
 * 
 * Note--the algorithm this uses is modified from the one presented by
 * T. Mahadeva Rao, "An Algorithm to Play the Game of Mastermind", in
 * ACM SIGART Bulletin, Issue 82, October 1982
 * --and when I say modified, I mean modified--the original is incomplete
 * 		as far as implementation goes, and at least from what I can glean, there
 *  	are some omissions and inconsistencies in what is presented.  Though some 
 *  	of this could be due to pseudo-code based on Pascal which I don't really know.  
 *  	For instance, in the article he presents a subroutine Addlists(), to be 
 *  	used in the Update() routine (see my update()), but Addlists() does not
 *  	appear in his algorithm at all.
 *  	Since he only presents part of his algorithm in the article, there are many
 *  	other issues.  So, this is really quite different--particularly if this 
 *  	algorithm is wrong somewhere, it is my fault, not his... (Though it seems
 *  	ok in all tests as of now).
 *  
 * @author drogers
 */
public class Brain {

	private MasterMind mm;
	private Engine engine;
	
	private int guessLength;
	
	private int[] currentGuess;
	
	//--contains the indices of the selected colors, or numbers, 
	//--selected for playing this game
	private int[] elementIndices;
	
	// index of current element being considered--ie tried
	// for the first time
	// set to -1 if no new elements to try
	private int beingConsidered;
	
	// index of element currently being fixed for position--ie known to be
	// in the solution, but trying to find out where
	// set to -1 if no elements are being fixed
	private Inference beingFixed;
	
	// the list of inferences in the knowledge base
	// note that inferences maintains an ordering:
	// ordered according to each inference's element
	// the elements are ordered by their occurrence in elementIndices
	private List<Inference> inferences = new ArrayList<Inference>();
	
	public Brain() {}
	
	public Brain(MasterMind mm) {
		this.mm = mm;
		this.engine = mm.getEngine();
		init();
	}
	
	private void init() {
		guessLength = this.engine.guessLength;
		elementIndices = engine.getUserControl().getSelectedElementIndexes();
		beingConsidered = elementIndices[0];
		beingFixed = null;
	}

	// cleans up the inferences list by removing any tied positions
	// from the positions lists of the other elements
	//--adding--if beingFixed is in the last position (and therefore tied)
	//  	set it as fixed and increment it
	private void cleanupInfs() throws Exception {
		Collections.sort(inferences);
		boolean clean = false;
		List<Integer> tied = new ArrayList<Integer>();
		while(!clean) {
			clean = true;			
			for(Inference inf : inferences) {
				if(inf.tied()){
					Integer pos = inf.positions.get(0);
					if(!tied.contains(pos)) {
						tied.add(pos);
						clean = false;
					}
				}
			}
			for(Integer pos : tied) {
				for(Inference inf : inferences) {
					if(!inf.tied()) {
						inf.removePos(pos);
					}
				}
			}
		}
		while(beingFixed != null && beingFixed.tied()) {
			setBeingFixed();
			incrBeingFixed();
		}
	}

	// fixes beingFixed to its next possible position and
	// deletes that position from the other inferences
	// returns true if successful
	private boolean setBeingFixed() {
		// first-time
		if(beingFixed == null) {
			return false;
		}
		int nextPos = nextPosition(beingFixed);
		beingFixed.positions.clear();
		beingFixed.positions.add(new Integer(nextPos));
		beingFixed.fixed = true;
		for(Inference inf : inferences) {
			if( !(inf == beingFixed) ) {
				inf.removePos(nextPos);
			}
		}
		return true;
	}

	// increments beingFixed by setting it to the next Inference
	// that is not fixed 
	// sets it to null if no next element available
	private void incrBeingFixed() {
		Collections.sort(inferences);
		boolean found = false;
		for(Inference inf : inferences) {
			if(found || (beingFixed == null && numFixed() == 0)) {
				if(inf.fixed) {
					continue;
				}
				beingFixed = inf;
				return;
			}
			if(inf == beingFixed) {
				found = true;
			}
			if(beingFixed == null &&
					!inf.fixed) {
				beingFixed = inf;
				return;
			}
		}
		beingFixed = null;
	}

	// increments beingConsidered by setting it to the next available element
	// sets it to -1 if all elements have been considered
	private void incrBeingConsidered() {
		if(inferences.size() == guessLength) {
			beingConsidered = -1;
			return;
		}
		for(int i=0; i<elementIndices.length; i++) {
			if( (elementIndices[i] == beingConsidered) &&
					(i < (elementIndices.length-1)) ) {
				beingConsidered = elementIndices[i+1];
				return;
			}
		}
		beingConsidered = -1;
	}
	// removes the current position for element elemI from
	// the list of positions for elemJ
	private void removeIFromJ(Inference infI, int elemJ) {
		int iPos = nextPosition(infI);		
		for(Inference inf : inferences) {
			if(inf.element == elemJ) {
				inf.removePos(iPos);
			}
		}
	}

	// removes the current position for element in infI from
	// the list of positions in infJ
	private void removeIFromJ(Inference infI, Inference infJ) {
		int iPos = nextPosition(infI);		
		infJ.removePos(iPos);
	}

	
	// fixes element elemI to infJ's current position
	// ie--ties elemI to nextPosition(infJ)
	// and deletes the position from the other position lists
	private void fixIToJ(int elemI, Inference infJ) {
		int nextPos = nextPosition(infJ);
		boolean done = false;
		for(Inference inf : inferences) {
			if(inf.element == elemI && 
					!inf.fixed &&
					!done) {
				inf.positions.clear();
				inf.positions.add(new Integer(nextPos));
				inf.fixed = true;
				done = true;
				continue;
			} else {
				inf.removePos(nextPos);
			}
		}
		Collections.sort(inferences);
	}
	
	// adds howMany Inferences with element element to inferences
	private void addInferences(int howMany, int element) {
		for(int i=0; i<howMany; i++) {
			Inference inf = new Inference(element, guessLength);
			inferences.add(inf);
		}
		Collections.sort(inferences);
		
	}
	// returns element index that param position is tied to if tide to an element
	// returns -1 if position is not tied
	private int elemTiedTo(int position) {
		for(Inference inf : inferences) {
			if(inf.tied()) {
				if(inf.currentPos() == position) {
					return inf.element;
				}
			}
		}
		return -1;
	}
	
	// returns how many positions are tied to elements
	private int numTied() {
		int count = 0;
		for(Inference inf : inferences) {
			if(inf.tied()) {
				count++;
			}
		}
		return count;
	}
	
	// returns how many positions are fixed to elements
	private int numFixed() {
		int count = 0;
		for(Inference inf : inferences) {
			if(inf.fixed) {
				count++;
			}
		}
		return count;
	}
	
	// returns the next possible position for element
	// returns -1 if not applicable
	private int nextPosition(int element) {
		for(Inference inf: inferences) {
			if(!inf.tied()) {
				if(inf.element == element) {
					return inf.currentPos();
				}
			}
		}
		return -1;
	}
	
	private int nextPosition(Inference inf) {
		if(inf != null) {
			return inf.currentPos();
		} else {
			return -1;
		}
	}
	
	// returns the second Inference not yet fixed 
	// one inference after beingFixed
	// returns null if not applicable
	//*** late late addition--subtle --seems that
	//	if there are multiple infs with the same element, 
	// 	but other elements that are unfixed, these should 
	// 	become second unfixed -- this is for the case of 0 whites
	// 	indicating that beingFixed can be fixed in its current pos
	private Inference secondUnfixed() {
		Collections.sort(inferences);
		boolean foundBf = false;
		Inference possible = null;
		//int count = 2;
		for(Inference inf : inferences) {
			if(possible != null) {
				if (inf.element != possible.element) {
					return inf;
				}
			} else if(foundBf && 
					!inf.fixed) {
				if(inf.element != beingFixed.element) {
					return inf;
				} else {
					possible = inf;
				}
			}
			if(inf == beingFixed) {
				foundBf = true;
			}
		}
		return possible;
	}

	
	/**
	 * Creates and returns the next guess.
	 * 
	 * @return
	 */
	public int[] nextGuess() {
		currentGuess = new int[guessLength];
		
		for(int i=0; i<currentGuess.length; i++) {
			
			if(elemTiedTo(i) != -1) {
				currentGuess[i] = elemTiedTo(i);
				
			} else if(i == nextPosition(beingFixed)) {
				currentGuess[i] = beingFixed.element;
				
			} else if(inferences.size() == guessLength) {
				currentGuess[i] = secondUnfixed().element;

			} else {
				currentGuess[i] = beingConsidered;
			}
		}
		return currentGuess;
	}
	/**
	 * Updates the knowledge base with the results of the last
	 * guess.
	 * 
	 * @param result
	 */
	public void update(GuessInfo result) {
		
		int black = result.getPositionMatches();
		int white = result.getNonPositionalMatches();		
		int gain;
		boolean firstTime = false;
		
		if(inferences.isEmpty()) {
			//beingFixed = beingConsidered;
			firstTime = true;
		}
		
		if(beingFixed == null) {
			gain = black + white - numTied();
		} else {
			gain = black + white - numTied()-1;
		}
		
		if(beingConsidered != -1) {
			addInferences(gain, beingConsidered);
		}
		
		if(firstTime) {
			if(!inferences.isEmpty()) {
				firstTime = false;
			} else {
				incrBeingConsidered();
				return;
			}
		}
		
		switch(white) {
		case 0: 	// beingFixed is in the correct position
			setBeingFixed();
			incrBeingFixed();
			break;
		case 1:		// beingFixed is not in the correct position, being considered
					// is not right in that position either
			if(beingFixed != null) {
				removeIFromJ(beingFixed, beingConsidered);
				removeIFromJ(beingFixed, beingFixed);
			}
			break;
		case 2:
			//--another late addition--if beingConsidered is no longer active
			// fix with secondUnfixed--seems logical so far
			if(beingConsidered != -1) {
				fixIToJ(beingConsidered, beingFixed);
			} else {
				fixIToJ(secondUnfixed().element, beingFixed);
			}
			break;
		default:
			System.err.println("******* Brain.update():  fell through switch");
			
		}
		try {
			cleanupInfs();
		} catch (Exception e) {
			e.printStackTrace();
		}
		incrBeingConsidered();
	}
	
	/**
	 * Prints the inference list to std out.
	 */
	public void printInferences() {
		String newline = System.getProperty("line.separator");		
		StringBuffer sbuf = new StringBuffer("(");
		for(Inference inf: inferences) {
			sbuf.append(inf + newline + " ");
		}
		if(inferences.size() > 0) {
			sbuf.delete( sbuf.lastIndexOf(newline), sbuf.length() );
		}
		sbuf.append(")");
		System.out.println(sbuf.toString());
	}
	
	/**
	 * Test the ai interactively by choosing the secret code and having
	 * the computer guess it.  Black and white are positional and non-positional
	 * matches as is standard in MasterMind.
	 */
	public static void testBrain() {
		Brain b = new Brain();
		b.elementIndices = new int[] {1, 2, 3, 4, 5, 6, 7, 8};
		//b.guessLength = 5;
		b.beingConsidered = b.elementIndices[0];
		b.beingFixed = null;
		
		System.out.println("Test MasterMind AI:");
		System.out.println("Use digits 1-8 for secret code");
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter guess length (4 to 5 usually):");
		b.guessLength = sc.nextInt();
		
		System.out.println("Choose secret code and enter black and white at prompt" +
				" based on my guess.");
		
		int guessNum = 1;
		
		while(true) {
			int[] g = b.nextGuess();
			System.out.println("Guess " + (guessNum++) + ":");
			for(int i : g) {
				System.out.print(i + " ");
			}
			System.out.println();
			
			System.out.println("Enter black and white:");
			
			
			int black = sc.nextInt();
			int white = sc.nextInt();
			
			if(black == b.guessLength) {
				System.out.print("Secret code is: ");
				for(int i : g) {
					System.out.print(i + " ");
				}
				System.out.println();
				System.out.println("Not exactly Terminator, but I got it in "
						+ (guessNum-1) + " guesses :-)");
				break;
			}
			GuessInfo info = new GuessInfo(g, black, white);
			
			b.update(info);
			
			System.out.println();
			System.out.println("Inferences:");
			b.printInferences();
			System.out.println("beingFixed = " + b.beingFixed +
					"  beingConsidered = " + b.beingConsidered);
			System.out.println();
		}
		sc.close();
	}

	public static void testBrainNonInteractive() {
		int numTrials = 100;
		int guessLength = 5;
		int numOfElements = 10;
		int maxGuessesAllowed = 16;
		
		
		int maxGuesses = 0;	
		
		System.out.println("TestBrainNonInteractive:");
		System.out.println("Number of trials: " + numTrials);
		System.out.println("Guess length: " + guessLength);
		System.out.println("Number of elements (digits in this case): " + numOfElements);
		System.out.println("Max Guesses Before Losing: " + maxGuessesAllowed);
		System.out.println();
		
		for(int i=0; i<numTrials; i++) {
			MasterMindTester mt = new MasterMindTester(guessLength, 
														numOfElements, 
														maxGuessesAllowed);
			Brain b = new Brain();
			String sn = mt.arrayToString(mt.getEngine().getSecretNumber());
			System.out.println("Secret number is " + sn);

			b.elementIndices = new int[numOfElements];
			for(int j=0; j<b.elementIndices.length; j++) {
				b.elementIndices[j] = j;
			}
			//b.guessLength = 5;
			b.guessLength = guessLength;
			b.beingConsidered = b.elementIndices[0];
			b.beingFixed = null;
			int[] currGuess = new int[guessLength];
			int black = 0, white = 0;
			
			for(int guessNum=1; black < guessLength; guessNum++) {
				currGuess = b.nextGuess();				
				//System.out.println("guess = " + mt.arrayToString(currGuess));
				
				GuessInfo gr = mt.getEngine().compare(currGuess);
				//System.out.println("guess result = " + gr.toString());
				
				black = gr.getPositionMatches();
				white = gr.getNonPositionalMatches();
				
				b.update(gr);
				
				if (mt.getEngine().hasWon()) {
					maxGuesses = Math.max(maxGuesses, guessNum);
					System.out.println("Secret Code = " + mt.arrayToString(currGuess)
							+ "   " + guessNum + " guesses");
					System.out.println();
					System.out.println();
					break;
				} else if (mt.getEngine().hasLost()) {
					maxGuesses = Math.max(maxGuesses, guessNum);
					System.out.println("***************************************************" +
							"*******************************************");
					System.out.println("Engine lost with " + guessNum + " guesses");
					System.out.println("Last guess: " + mt.arrayToString(currGuess));
					System.out.println();
					System.out.println();
					break;
				}
			}
		}
		System.out.println();
		System.out.println("Maximum number of guesses: " + maxGuesses);
        
	}
	/**
	 * Main now allows choice of command line tests.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Test MasterMind ai.Brain:");
		System.out.println("Choose the type of test:");
		System.out.println("1  Run non-interactive test");
		System.out.println("2  Play interactively with the ai guessing your code " +
				"(shows the ai's inferences)");
		System.out.println("Enter 1 or 2:");
		int choice = sc.nextInt();
		
		if(choice == 1) {
			testBrainNonInteractive();
			System.exit(0);
		} else {		
			testBrain();
			System.exit(0);
		}
		sc.close();
		
		Brain b = new Brain();
		
		b.elementIndices = new int[] {1, 2, 3, 4, 5};
		int[] int1 = {1};
		int[] int2 = {3, 4, 5};
		int[] int3 = {2, 3, 4, 5};
		int[] int4 = {2, 3, 4, 5};
		Inference inf1 = new Inference(2, int1);
		Inference inf2 = new Inference(2, int2);
		Inference inf3 = new Inference(3, int3);
		Inference inf4 = new Inference(4, int4);
		b.inferences.add(inf1);
		b.inferences.add(inf2);
		b.inferences.add(inf3);
		b.inferences.add(inf4);
		
		b.printInferences();
		
		System.out.println("b.elemTiedTo(1): " + b.elemTiedTo(1));
		System.out.println("b.elemTiedTo(2): " + b.elemTiedTo(2));
		
		System.out.println("b.nextPosition(2): " + b.nextPosition(2));
		System.out.println("b.nextPosition(3): " + b.nextPosition(3));
		
		b.beingFixed = inf2;
		System.out.println("b.beingFixed = " + b.beingFixed);
		System.out.println("b.secondUnfixed() = " + b.secondUnfixed() );
		System.out.println("after b.incrBeingFixed():");
		b.incrBeingFixed();
		System.out.println("b.beingFixed = " + b.beingFixed);
		b.printInferences();
		
		System.out.println("after b.setBeingFixed():");
		b.setBeingFixed();
		System.out.println("b.beingFixed = " + b.beingFixed);
		b.printInferences();
		
		
		System.out.println("\n****************************\n");
		b.inferences.clear();
		int1 = new int[] {1};
		int2 = new int[] {3, 4, 5};
		int3 = new int[] {3};
		int4 = new int[] {2, 3, 4, 5};
		inf1 = new Inference(2, int1);
		inf2 = new Inference(2, int2);
		inf3 = new Inference(3, int3);
		inf4 = new Inference(4, int4);
		b.inferences.add(inf1);
		b.inferences.add(inf2);
		b.inferences.add(inf3);
		b.inferences.add(inf4);
		
		b.printInferences();

		try {
			b.cleanupInfs();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("after b.cleanupInfs():");
		b.printInferences();
		
		b.beingConsidered = 4;
		System.out.println("b.beingConsidered = " + b.beingConsidered);
		System.out.println("after b.incrBeingConsidered:");
		b.incrBeingConsidered();
		System.out.println("b.beingConsidered = " + b.beingConsidered);
		System.out.println("after b.incrBeingConsidered:");
		b.incrBeingConsidered();
		System.out.println("b.beingConsidered = " + b.beingConsidered);
		System.out.println("after b.incrBeingConsidered:");
		b.incrBeingConsidered();
		System.out.println("b.beingConsidered = " + b.beingConsidered);
		
	}
}
