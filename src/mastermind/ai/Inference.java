/**
 * Dave Rogers
 * dave at drogers dot us
 * This software is for instructive purposes.  Use at your own risk - not meant to be robust at all.
 * Feel free to use anything, credit is appreciated if warranted.
 */
package mastermind.ai;

import java.util.*;

/** 
 * Contains the element index and a list of positions that are possible
 * positions for the element
 * if positions has length 1, then that position is fixed to that element
 * note--package access--this class is for Brain to use internally
 * 	   --it can directly access the element and positions list 
 * *** note--added boolean fixed to help revamp ability to determine 
 *		which are fixed officially as opposed to looking like it
 */
class Inference implements Comparable {
	// index of element the inference refers to
	int element;
	
	// poss positions
	List<Integer> positions = new ArrayList<Integer>();
	
	// set to true if this inference's element has been fixed
	// to a position--use tied() to indicate being on the way to this
	boolean fixed = false;
	
	Inference(int elem, List<Integer> positions) {
		this.element = elem;
		this.positions.addAll(positions);
	}
	
	Inference(int elem, int[] positions) {
		this.element = elem;
		for(int i : positions) {
			this.positions.add(new Integer(i));
		}
	}
	/**
	 * Creates an Inference with positions (0, 1, .., (guessLength-1))
	 * 
	 * @param elem
	 * @param guessLength
	 */
	Inference(int elem, int guessLength) {
		this.element = elem;
		for(int i=0; i<guessLength; i++) {
			this.positions.add(new Integer(i));
		}
	}

	// remove pos from the list of positions
	void removePos(int pos) {
		positions.remove(new Integer(pos));
	}
	
	// returns the "current" position for the element
	// ie--first in positions list
	int currentPos() {
		return positions.get(0).intValue();
	}
	
	// true if element is tied to one position
	boolean tied() {
		return positions.size() == 1;
	}
	
	public String toString() {
		StringBuffer sbuf = new StringBuffer("(" + element + " (");
		for(Integer pos : positions) {
			sbuf.append(pos + " ");
		}
		sbuf.deleteCharAt(sbuf.length()-1);
		sbuf.append("))");
		return sbuf.toString();
	}

	@Override
	public int compareTo(Object o) {
		Inference inf = (Inference)o;
		return (this.element - inf.element);
	}
}
