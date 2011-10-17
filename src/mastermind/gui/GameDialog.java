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

/**
 * Dialog to handle selecting either a Colors game or a Numbers game,
 * and to set the game's parameters.
 */
public class GameDialog extends JDialog implements ActionListener {
    // The game using this dialog
    private MasterMind parent;
    // The type of game -- "Colors" or "Numbers", e.g.
    private String typeOfGame;
    // Total number of elements to make available for the game
    private int totalElements;
    // Number of guesses allowed for this game
    private int intNumGuesses;
    // length of guess--set default to 4 here because I can't seem
    // to query the radio buttons for there isSelected() state unless
    // in an action
    private int guessLength = 4;
    
    // True if game is being played by ai
    private boolean autopilot;
    
    // The following members are for the dialog's display
    private String[] numElems = {"6", "8", "10"};
    private String[] numOfGuesses = {"8", "10", "12", "14"};
    private JComboBox numElements = new JComboBox(numElems);
    private JComboBox numGuesses = new JComboBox(numOfGuesses);
    JLabel numOfElements;
    JLabel numberOfGuesses = new JLabel("Number of Guesses");
    private ButtonGroup group = new ButtonGroup();
    private JRadioButton len4 = new
                                JRadioButton("Guess Length 4 (Standard)", true);
    private JRadioButton len5 = new JRadioButton("Guess Length 5");
    private JButton ok = new JButton("OK");

	/**
     * Creates a GameDialog.
     * @param parent JFrame - the dialog's parent frame
     * @param typeOfGame String - the type of game selected
     * by the menu which launches this dialog
     * @param autopilot boolean - true if ai is playing, false if user
     */
    public GameDialog(MasterMind parent, String typeOfGame, boolean autopilot) {
        super(parent, "Set Game Options", true);
        this.parent = parent;
        this.typeOfGame = typeOfGame;
        this.autopilot = autopilot;
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        if (typeOfGame.equals("Colors")) {
            numOfElements = new JLabel("Number of Colors");
        } else if (typeOfGame.equals("Numbers")) {
            numOfElements = new JLabel("Number of Digits");
        } 
        // comboboxes
        numElements.setSelectedIndex(1);
        numElements.addActionListener(this);
        numGuesses.setSelectedIndex(2);
        numGuesses.addActionListener(this);
        Box boxElem = Box.createHorizontalBox();
        boxElem.add(numOfElements);
        boxElem.add(Box.createHorizontalStrut(25));
        boxElem.add(numElements);
        Box boxGuess = Box.createHorizontalBox();
        boxGuess.add(numberOfGuesses);
        boxGuess.add(Box.createHorizontalGlue());
        boxGuess.add(numGuesses);
        Box all = Box.createVerticalBox();
        all.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black),
                BorderFactory.createEmptyBorder()));
        all.add(boxElem);
        all.add(boxGuess);
        // radios
        len4.addActionListener(this);
        len4.setActionCommand("length=4");
        //len5.addActionListener(this);
        //len5.setActionCommand("length=5");
        group.add(len4);
        //group.add(len5);
        Box radioBox = Box.createVerticalBox();
        radioBox.add(len4);
        //radioBox.add(len5);
        Box anotherBox = Box.createHorizontalBox();
        anotherBox.add(radioBox);
        anotherBox.add(Box.createHorizontalGlue());
        all.add(anotherBox);
        all.add(Box.createVerticalStrut(10));
        ok.addActionListener(this);
        ok.setActionCommand("ok");
        all.add(ok);
        getContentPane().add(all);
        pack();

    }

    /**
     * This dialog handles its own actions.
     * @param e ActionEvent
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JComboBox) {
            JComboBox sourceBox = (JComboBox) e.getSource();
            if (sourceBox == numElements) {
                String s = (String) sourceBox.getSelectedItem();
                totalElements = Integer.parseInt(s);
                //System.out.println("ComboBox numElements: " + item);
            } else if (sourceBox == numGuesses) {
                String s = (String) sourceBox.getSelectedItem();
                intNumGuesses = Integer.parseInt(s);
                //System.out.println("ComboBox numGuesses: " + item);
            }
        } else if (e.getActionCommand().equals("length=4")) {
            guessLength = 4;
            //System.out.println("Set length to 4");
        } else if (e.getActionCommand().equals("length=5")) {
            guessLength = 5;
            //System.out.println("Set length to 5");
        } else if (e.getActionCommand().equals("ok")) {
            // make sure all values are set if not changed during
            // this action
            String s = (String) numElements.getSelectedItem();
            totalElements = Integer.parseInt(s);
            s = (String) numGuesses.getSelectedItem();
            intNumGuesses = Integer.parseInt(s);
            // action to pass parameters to MasterMind
            try {
                dispose();
                parent.reInit();
                parent.init(typeOfGame, totalElements, intNumGuesses,
                            guessLength, this.autopilot);
            } catch (Exception ex) {}
        }
    }

}
