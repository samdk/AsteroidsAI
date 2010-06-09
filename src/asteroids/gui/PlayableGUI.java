package samdk.asteroids.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.LinkedList;

import samdk.asteroids.core.*;

public class PlayableGUI extends AsteroidsGUI implements KeyListener {
    public static final int DEFAULT_TIMESTEP = 1000 / Constants.FPS; // 5
    
    public PlayableGUI() {
	this(new AsteroidsGame());
    }

    public PlayableGUI(AsteroidsGame game) {
	super(game);
	addKeyListener(this);
	setVisible(true);
    }

    public void keyPressed(KeyEvent e) {
	switch(e.getKeyCode()) {
	    case KeyEvent.VK_LEFT:
		game.doLeft(true);
		break;
	    case KeyEvent.VK_RIGHT:
		game.doRight(true);
		break;
	    case KeyEvent.VK_UP:
		game.doUp(true);
		break;
	    case KeyEvent.VK_DOWN:
		game.doDown(true);
		break;
	}
	switch(e.getKeyChar()) {
	    case ' ':
		game.doShoot(true);
                break;
	}
    }

    public void keyReleased(KeyEvent e){
	switch(e.getKeyCode()) {
	    case KeyEvent.VK_LEFT:
		game.doLeft(false);
		break;
	    case KeyEvent.VK_RIGHT:
		game.doRight(false);
		break;
	    case KeyEvent.VK_UP:
		game.doUp(false);
		break;
	    case KeyEvent.VK_DOWN:
		game.doDown(false);
		break;
	}
        switch(e.getKeyChar()) {
            case ' ':
                game.doShoot(false);
                break;
        }
    }
    public void keyTyped(KeyEvent e){}

    public void run() {
	run(DEFAULT_TIMESTEP);
    }

    public void run(int timestep) {
        Action updateAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                repaint();
                game.update();
                if (game.isGameOver() || game.getFrameCount() > 900) {
                    System.out.println("points: " + game.getPoints());
                    System.exit(-1);
                }
            }
        };
        new Timer(timestep,updateAction).start();
    }
}


