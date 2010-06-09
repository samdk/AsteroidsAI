package samdk.asteroids.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.LinkedList;

import samdk.asteroids.core.*;
import samdk.asteroids.gui.*;

public abstract class AbstractPlayableGUI extends AsteroidsGUI {
    public static final int DEFAULT_TIMESTEP = 1000 / Constants.FPS; // 5
    
    public AbstractPlayableGUI() {
	this(new AsteroidsGame());
    }

    public AbstractPlayableGUI(AsteroidsGame game) {
	super(game);
	setVisible(true);
    }

    // called each frame
    public void step() {
        repaint();
        doAction(game);
        game.update();
    }

    // override to define an AI action each frame
    public abstract void doAction(AsteroidsGame game);

    // runs the game
    public void run() {
	run(DEFAULT_TIMESTEP);
    }
    public void run(int timestep) {
        Action updateAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                step();
            }
        };
        new Timer(timestep,updateAction).start();
    }
}
