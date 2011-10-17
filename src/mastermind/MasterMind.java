/**
 * Dave Rogers
 * dave at drogers dot us
 * This software is for instructive purposes.  Use at your own risk - not meant to be robust at all.
 * Feel free to use anything, credit is appreciated if warranted.
 */
package mastermind;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;

import javax.swing.*;

// javahelp
import javax.help.*;

import mastermind.event.ClickHandler;
import mastermind.gui.Board;
import mastermind.gui.GameDialog;
import mastermind.gui.UserControl;
import mastermind.gui.WinOrLose;


/**
 * --drogers 2008-03:
 * --update for ai--boolean autopilot, set to true if game is 
 * --being played by ai--see package mastermind.ai
 * --set with Autopilot checkbox on Game menu
 * 
 * The MasterMind class holds the View components--the board,
 * and the control unit--together, and initializes them along with
 * the engine, which is the Model for the game.  It acts as part
 * of the Controller (along with the ClickHandler class), by handling
 * the action events from the Start button.  MasterMind also has a
 * menu and GameDialog for switching game types and setting game
 * parameters.  The GameDialog is its own listener to avoid further
 * complexity.  Thus you could consider it part of the Controller
 * as well. Finally, there are some public static fields for
 * global access to colors and panel dimensions, etc.
 */
public class MasterMind extends JFrame implements ActionListener, ItemListener {

    // This game's user control
    private UserControl userControl;
    // This game's board
    private Board board;
    // This game's engine
    private Engine engine;
    // Dialog for setting up new games
    private GameDialog dialog;
    // Game ending message panel
    private WinOrLose showWonOrLost;
    // This game's click handler
    private ClickHandler clickHandler;
    
    // True if game is being played by ai
    private boolean autopilot = false;

    /**
     * The preferred height of an individual board element panel.
     * Only need height for square panels.
     */
    public static int boardUnitHt;

    /** Colors the board and user controls can use for
     * displaying numbers
     */
    public static Color numberColor =
            new Color(40, 40, 40); // deep blue

    public static Color selectedNumberColor =
            Color.blue;
    //new Color(115, 10, 214); // violet

    // The array of possible integers to use for a number game
    private Integer[] numbers;

    // panel background color
    private Color backgroundColor = new Color(160, 160, 160);

    /**
     * The base colors are the unhighlighted colors available for
     * play in the colors game.  This bit of hackery was necessary
     * because I wasn't satisfied with the algorithm I had for
     * making the 3d peg head look.  So each color has a matching
     * highlight color with the same index in the array
     * highlightColors[].
     */
    public static Color[] baseColors = {
                                       new Color(230, 160, 0), // gold
                                       new Color(255, 0, 255), // magenta
                                       new Color(255, 0, 0), // red
                                       new Color(255, 191, 0), // yellow
                                       new Color(0, 0, 255), // blue
                                       new Color(0, 192, 0), // green
                                       new Color(255, 95, 27), // orange
                                       new Color(45, 150, 255), // sky blue
                                       new Color(115, 10, 214), // violet
                                       new Color(134, 81, 1) // brown
    };

    /**
     * Hard-coded highlights for 3d peg head appearance--these
     * colors are matched by index with their darker base colors in
     * baseColors[].
     */
    public static Color[] highlightColors = {
                                            new Color(255, 192, 0), // highlighted gold
                                            new Color(255, 128, 255), // highlighted magenta
                                            new Color(255, 128, 128), // highlighted red
                                            new Color(255, 255, 70), // highlighted yellow
                                            new Color(128, 128, 255), // highlighted blue
                                            new Color(64, 255, 64), // highlighted green
                                            new Color(255, 160, 85), // highlighted orange
                                            new Color(110, 220, 255), // highlighted sky blue
                                            new Color(195, 75, 255), // highlighted violet
                                            new Color(200, 145, 65) // highlighted brown
    };

    /**
     * Gets the proper highlight color for a base color.
     * @param base Color - the base color you want to match.
     * @return Color - matching highlight color.
     */
    public static Color getHighlightColor(Color base) {
        for (int i = 0; i < baseColors.length; i++) {
            if (baseColors[i].equals((Color) base)) {
                return highlightColors[i];
            }
        }
        return null;
    }


	/**
     * Creates a MasterMind object.
     */
    public MasterMind() {
        clickHandler = new ClickHandler();
        setTitle("Mastermind");
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        boardUnitHt = dim.height / 20;
        setLocation(dim.width / 4, 0);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * This main is the entry point for the GUI Mastermind game.
     * @param args String[]
     */
    public static void main(String[] args) {
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    			createAndRunGui();
    		}
    	});
    }
 
    private static void createAndRunGui() {
    	MasterMind mm = new MasterMind();
        try {
            mm.init("Colors", 10, 10, 4, false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Handles the Start button being clicked, and game menu radio buttons.
     * 
     * @param e ActionEvent
     */
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("start")) {
            engine.startButtonClicked();
        } else if (cmd.equals("colors")) {
            dialog = new GameDialog(this, "Colors", this.autopilot);
            dialog.setVisible(true);
        } else if (cmd.equals("numbers")) {
            dialog = new GameDialog(this, "Numbers", this.autopilot);
            dialog.setVisible(true);
        } 
//        else if (cmd.equals("auto")) {
//        	//JRadioButtonMenuItem btn = (JRadioButtonMenuItem)e.getSource();
//        	//this.autopilot = this.autopilot? false: true;
//        	this.autopilot = this.autoBtn.isSelected();
//        	
//        	//autoBtn.setSelected(this.autopilot);
//
//        	if(engine.gameIsInitialized()) {
//        		if(this.autopilot) {
//        		JOptionPane.showMessageDialog(this,
//        				"Autopilot will be turned on for the next game.");
//        		} else {
//        			JOptionPane.showMessageDialog(this,
//    				"Autopilot will be turned off for the next game.");
//        		}
//        	}
//        }
    }
    
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		if(source == chkAuto) {
			if(e.getStateChange() == ItemEvent.DESELECTED) {
				autopilot = false;
			} else {
				autopilot = true;
			}
		}
	}

    /**
     * Call before calling init() after game has started.
     */
    public void reInit() {
        setVisible(false);
        getContentPane().removeAll();
        userControl = null;
        board = null;
        engine = null;
        showWonOrLost = null;
        autopilot = false;
    }

    /**
     * Initializes a new game.
     * @param typeOfGame String - the type of game selected
     * @param totalElements int - the total number of elements
     * available for this game
     * @param numGuesses int - number of guesses allowed for this game
     * @param guessLength int - length of the guess
     * @param autopilot boolean - true if ai is playing, false if user
     * @throws Exception - for subclassing
     */
    public void init(String typeOfGame, int totalElements,
                     int numGuesses, int guessLength, boolean autopilot) throws Exception {
    	this.autopilot = autopilot;
        makeMenu();
        if (typeOfGame.equalsIgnoreCase("Numbers")) {
            numbers = new Integer[totalElements];
            for (int i = 0; i < numbers.length; i++) {
                numbers[i] = new Integer(i);
            }
            userControl = new UserControl(clickHandler, this,
                                          numbers);
            board = new Board(guessLength, numGuesses,
                              clickHandler, numbers, backgroundColor);
            engine = new Engine(board, userControl, this, guessLength,
                                numGuesses, numbers.length);
        } else if (typeOfGame.equalsIgnoreCase("Colors")) {
            Color[] workingColors = new Color[totalElements];
            for (int i = 0; i < workingColors.length; i++) {
                workingColors[i] = baseColors[i];
            }
            userControl = new UserControl(clickHandler, this,
                                          workingColors);
            board = new Board(guessLength, numGuesses, clickHandler,
                              workingColors, backgroundColor);
            engine = new Engine(board, userControl, this, guessLength,
                                numGuesses, workingColors.length);

        }

        clickHandler.addEngine(engine);
        Container cp = getContentPane();
        cp.setBackground(new Color(165, 206, 182)); // (165, 206, 182)
        cp.setLayout(new FlowLayout()); // (219, 243, 230)
        cp.add(board);
        Box vBox = Box.createVerticalBox();
        showWonOrLost = new WinOrLose(boardUnitHt * 5 / 2,
                                      boardUnitHt * 2, guessLength);
        showWonOrLost.setVisible(false);
        vBox.add(showWonOrLost);
        vBox.add(Box.createVerticalStrut(showWonOrLost.getHeight() / 2));
        vBox.add(userControl);
        cp.add(vBox);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        pack();
        Dimension gameDim = getContentPane().getSize();
        int leftX = dim.width / 2 - gameDim.width / 2;
        setLocation(leftX, 0);
        setVisible(true);
    }

    /**
     * Display message indicating user won
     */
    public void showGameWon() {
        showWonOrLost.showGameWon();
        showWonOrLost.setVisible(true);
    }

    /**
     * Display message indicating user lost
     */
    public void showGameLost() {
        showWonOrLost.showGameLost();
        showWonOrLost.setVisible(true);
    }

    /**
     * Initializes a new game by hiding the game ending message panel.
     */
    public void newGame() {
    	//** orig
    	showWonOrLost.clear();
        showWonOrLost.setVisible(false);
//    	int guessLength = showWonOrLost.getGuessLength();
//    	showWonOrLost = new WinOrLose(boardUnitHt * 5 / 2,
//                boardUnitHt * 2, guessLength);
//    	showWonOrLost.setVisible(false);
        getContentPane().invalidate();
    }

    /**
     * Shows the winning combination of Colors or Numbers
     * in the WinOrLose panel
     */
    public void showWinningCombination() {
	int[] secret = engine.getSecretNumber();
        Object[] elems = new Object[secret.length];
        for(int i = 0; i < elems.length; i++) {
            elems[i] = board.getElement(secret[i]);
        }
        showWonOrLost.showWinningCombination(elems);
    }

    // added for ai
    JCheckBoxMenuItem chkAuto = null;
    
    // Make this app's menu
    private void makeMenu() {
        JMenuBar bar = new JMenuBar();        

        JMenu game = new JMenu("Game");
        game.setMnemonic(KeyEvent.VK_G);
        ButtonGroup group = new ButtonGroup();
        JRadioButtonMenuItem col = new JRadioButtonMenuItem("Colors",
                true);
        JRadioButtonMenuItem num = new JRadioButtonMenuItem("Numbers");
        group.add(col);
        group.add(num);
        col.addActionListener(this);
        num.addActionListener(this);
        col.setMnemonic(KeyEvent.VK_C);
        num.setMnemonic(KeyEvent.VK_N);
        col.setActionCommand("colors");
        num.setActionCommand("numbers");
        game.add(col);
        game.add(num);        
        
        // added for ai--drogers 2008-03-11
        chkAuto = new JCheckBoxMenuItem("Autopilot (AI)");
        chkAuto.addItemListener(this);
        game.add(chkAuto);
        
        //**debug
        //chkAuto.setSelected(true);
        
        bar.add(game);

        // add handler to open help contents to help menu
        JMenu help = new JMenu("Help");
        help.setMnemonic(KeyEvent.VK_H);
        JMenuItem contents = new JMenuItem("Contents", KeyEvent.VK_C);
//        contents.addActionListener(this);
//        contents.setActionCommand("help_contents");
        help.add(contents);
        bar.add(help);
        
     // help is implemented as a javahelp helpset
        HelpSet hs;
    	HelpBroker hb;
    	ClassLoader cl = this.getClass().getClassLoader();
    	try {
    		URL hsUrl = HelpSet.findHelpSet(cl, "MasterMind.hs");
    		hs = new HelpSet(null, hsUrl);
    	} catch (Exception ee) {
    		System.out.println("Exception getting helpset:" + 
    				ee.getMessage());
    		return;
		}
    	hb = hs.createHelpBroker();
    	contents.addActionListener(new CSH.DisplayHelpFromSource(hb));
        
        setJMenuBar(bar);
    }


	public Engine getEngine() {
		return engine;
	}

	public ClickHandler getClickHandler() {
		return clickHandler;
	}


	/**
	 * Returns true if game is being run by computer (see ai package).
	 * 
	 * @return
	 */
	public boolean autoPilotIsOn() {
		return autopilot;
	}


}
