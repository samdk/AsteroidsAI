package samdk.asteroids.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import samdk.asteroids.core.*;

public class AsteroidsGUI extends JFrame {
    protected AsteroidsGame game;
    protected AsteroidsViz av;
    protected JFrame parent;

    public AsteroidsGUI(AsteroidsGame game) {
	this.game = game;
	add(new AsteroidsViz(this.game));
	setSize(game.getWidth(),game.getHeight());
	setTitle("AsteroidsGame");
	setUndecorated(true);
	setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        parent = this;
    }

    private class AsteroidsViz extends JPanel {
	private AsteroidsGame game;

	public AsteroidsViz(AsteroidsGame game) {
	    this.game = game;
	}

	public AsteroidsGame getGame() {
	    return game;
	}

	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
            Toolkit.getDefaultToolkit().sync();
	    Graphics2D g2d = (Graphics2D) g;
            
	    if ( game.isGameOver() ) {
		g2d.drawString("GAME OVER! points: "+game.getPoints(),(game.getWidth()/2-50),(game.getHeight()/2-30));
                parent.setVisible(false);
	    }
	    else {
		for (Asteroid a : game.getAsteroids()) {
		    int x = a.getX(); int y = a.getY();
		    int size = a.getSize();
		    Rectangle2D.Double first = new Rectangle2D.Double(x,y,size,size);
		    boolean drawSecond = false;
		    if (x < 0) {
			x = size - x;
			drawSecond = true;
		    }
		    else if (x+size >= game.getWidth()) {
			x = x - game.getWidth();
			drawSecond = true;
		    }
		    if (y < 0) {
			y = size - y;
			drawSecond = true;
		    }
		    else if (y+size >= game.getHeight()) {
			y = y - game.getHeight();
			drawSecond = true;
		    }
		    g2d.draw(first);
		    if (drawSecond) {
			Rectangle2D.Double second = new Rectangle2D.Double(x,y,size,size);
			g2d.draw(second);
		    }
		}
		Ship s = game.getShip();
		Polygon shipshape = getShipShape(s);
		g2d.fill(shipshape);
		if ( game.shipIsImmune() ) {
		    g2d.draw(new Ellipse2D.Double(s.getX()-18,s.getY()-18,37,37));
		}
		for (Bullet b : game.getBullets()) {
		    g2d.draw(new Rectangle2D.Double(b.getX(),b.getY(),1,1));
		}
		g2d.drawString("level: " + game.getLevel() + ", points: " + game.getPoints() + ", lives left: " + game.getLivesLeft(),10,20);
                //drawAllSensors(g2d,s);
                drawActivatedSensors(g2d,s);
            }
        }

        private void drawAllSensors(Graphics2D g2d, Ship s) {
            int[] sensors = s.getSensors();
            int i = 0;
            int size = 1;
            for (double[] pt : s.getL1Points()) {
                if (sensors[i] == 1) {g2d.setColor(Color.RED); size = 3;}
                else {g2d.setColor(Color.BLACK); size = 1;}
                g2d.fill(new Rectangle2D.Double(pt[0],pt[1],size,size));
                i++;
            }
            i = 0;
            for (double[] pt : s.getL2Points()) {
                if (sensors[i] == 2) {g2d.setColor(Color.RED); size = 3;}
                else {g2d.setColor(Color.BLACK); size = 1;}
                g2d.fill(new Rectangle2D.Double(pt[0],pt[1],size,size));
                i++;
            }
            i = 0;
            for (double[] pt : s.getL3Points()) {
                if (sensors[i] == 3) {g2d.setColor(Color.RED); size = 3;}
                else {g2d.setColor(Color.BLACK); size = 1;}
                g2d.fill(new Rectangle2D.Double(pt[0],pt[1],size,size));
                i++;
            }
            i = 0;
            for (double[] pt : s.getL4Points()) {
                if (sensors[i] == 4) {g2d.setColor(Color.RED); size = 3;}
                else {g2d.setColor(Color.BLACK); size = 1;}
                g2d.fill(new Rectangle2D.Double(pt[0],pt[1],size,size));
                i++;
            }
            i = 0;
            for (double[] pt : s.getL5Points()) {
                if (sensors[i] == 5) {g2d.setColor(Color.RED); size = 3;}
                else {g2d.setColor(Color.BLACK); size = 1;}
                g2d.fill(new Rectangle2D.Double(pt[0],pt[1],size,size));
                i++;
            }
            i = 0;
            for (double[] pt : s.getL6Points()) {
                if (sensors[i] == 6) {g2d.setColor(Color.RED); size = 3;}
                else {g2d.setColor(Color.BLACK); size = 1;}
                g2d.fill(new Rectangle2D.Double(pt[0],pt[1],size,size));
                i++;
            }
	}
        private void drawActivatedSensors(Graphics2D g2d, Ship s) {
            int[] sensors = s.getSensors();
            int i = 0;
            int size = 3;
            g2d.setColor(Color.RED);
            for (double[] pt : s.getL1Points()) {
                if (sensors[i] == 1)
                    g2d.fill(new Rectangle2D.Double(pt[0],pt[1],size,size));
                i++;
            }
            i = 0;
            for (double[] pt : s.getL2Points()) {
                if (sensors[i] == 2)
                    g2d.fill(new Rectangle2D.Double(pt[0],pt[1],size,size));
                i++;
            }
            i = 0;
            for (double[] pt : s.getL3Points()) {
                if (sensors[i] == 3)
                    g2d.fill(new Rectangle2D.Double(pt[0],pt[1],size,size));
                i++;
            }
            i = 0;
            for (double[] pt : s.getL4Points()) {
                if (sensors[i] == 4)
                    g2d.fill(new Rectangle2D.Double(pt[0],pt[1],size,size));
                i++;
            }
            i = 0;
            for (double[] pt : s.getL5Points()) {
                if (sensors[i] == 5)
                    g2d.fill(new Rectangle2D.Double(pt[0],pt[1],size,size));
                i++;
            }
            i = 0;
            for (double[] pt : s.getL6Points()) {
                if (sensors[i] == 6)
                    g2d.fill(new Rectangle2D.Double(pt[0],pt[1],size,size));
                i++;
            }

	}

	private Polygon getShipShape(Ship ship) {
	    int[] xs = new int[3];
	    int[] ys = new int[3];
	    xs[0] = ship.getNose()[0];
	    ys[0] = ship.getNose()[1];
	    xs[1] = ship.getTailR()[0];
	    ys[1] = ship.getTailR()[1];
	    xs[2] = ship.getTailL()[0];
	    ys[2] = ship.getTailL()[1];
	    return new Polygon(xs,ys,3);
	}
    }
}
