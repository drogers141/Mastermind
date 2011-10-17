/**
 * Dave Rogers
 * dave at drogers dot us
 * This software is for instructive purposes.  Use at your own risk - not meant to be robust at all.
 * Feel free to use anything, credit is appreciated if warranted.
 */
package mastermind.event;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import mastermind.Engine;
import mastermind.gui.GuessColorPanel;
import mastermind.gui.GuessNumberPanel;
import mastermind.gui.UserControlColorPanel;
import mastermind.gui.UserControlNumberPanel;

/**
 * Handles mouse events for the Mastermind game.  The MasterMind class
 * handles action events.
 * --for ai--added ability to disable and enable
 */
public class ClickHandler extends MouseAdapter {
    // This handler's engine
    private Engine engine;

    // disable if ai is playing
    private boolean enabled = true;
    
    /**
     * Set to false for ai, reset to true for human player.
     * Default is true.
     * 
     * @param enabled
     */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
     * Creates a ClickHandler object
     */
    public ClickHandler() {}

    /**
     * Creates a ClickHandler object
     * @param eng Engine - Engine for this click handler to report to
     */
    public ClickHandler(Engine eng) {
        engine = eng;
    }

    /**
     * Adds a reference to an engine for this handler to send
     * messages to.  This is for the chicken and egg problem
     * of forward referencing dependent objects in construction.
     * No doubt a hack, but hey...
     * @param eng Engine - Engine for this click handler to report to
     */
    public void addEngine(Engine eng) {
        engine = eng;
    }

    /**
     * Handle the mouse click.  If enabled is false, does nothing.
     * 
     * @param e MouseEvent
     */
    public void mousePressed(MouseEvent e) {
    	if(!enabled) {
    		return;
    	}
        if (e.getComponent() instanceof UserControlColorPanel) {
            UserControlColorPanel colorPanel =
                    (UserControlColorPanel) e.getComponent();
            if (colorPanel.isInCircle(e.getX(), e.getY())) {
                engine.userControlClicked(colorPanel);
            }
        } else if (e.getComponent() instanceof GuessColorPanel) {
            GuessColorPanel colorPanel =
                    (GuessColorPanel) e.getComponent();
            if (colorPanel.isInCircle(e.getX(), e.getY())) {
                engine.boardClicked(colorPanel);

            }
        } else if (e.getComponent() instanceof UserControlNumberPanel) {
            UserControlNumberPanel numberPanel =
                    (UserControlNumberPanel) e.getComponent();
            engine.userControlClicked(numberPanel);
        } else if (e.getComponent() instanceof GuessNumberPanel) {
            GuessNumberPanel numberPanel =
                    (GuessNumberPanel) e.getComponent();
            engine.boardClicked(numberPanel);
        }

    }
}
