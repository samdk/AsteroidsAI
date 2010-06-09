package samdk.asteroids.core;

import java.util.LinkedList;

public class Ship {
    public static final double MAX_SPEED = 200.0 / Constants.FPS; //400
    private final double MAX_FORWARD_SPEED = 200.0 / Constants.FPS; // 5
    private final double SPEED_FORWARD_STEP = 16.0 / Constants.FPS; // .02
    private final double SPEED_BACKWARD_STEP = 16.0 / Constants.FPS + .000101; //.020101
    private final double MAX_BACKWARD_SPEED = 200.0 / Constants.FPS; // 5

    private final double HEADING_STEP = Math.PI / (Constants.FPS / 1.9); // Math.PI / 90


    private double x,y;
    private int width,height;
    
    private double xVel,yVel;
    private double heading;

    private int[] nose,tailL,tailR;

    private int[] sensors;

    public Ship(int width,int height) {
	this.width = width;
	this.height = height;
	x = width / 2;
	y = height / 2;
	xVel = 0;
	yVel = 0;
	heading = 0;
	setShipPoints();
        sensors = new int[16];
    }

    private void setShipPoints() {
	nose = new int[2];
	tailL = new int[2];
	tailR = new int[2];
	setNose();
	setTailL();
	setTailR();
    }
    private void setNose() {
	nose[0] = (int) (x - 10 * Math.sin(heading));
	nose[1] = (int) (y - 10 * Math.cos(heading));
    }
    public int[] getNose() {
	return nose;
    }
    private void setTailL() {
	tailL[0] = (int) (x + 10 * Math.sin(heading) - 5 * Math.cos(heading));
	tailL[1] = (int) (y + 10 * Math.cos(heading) + 5 * Math.sin(heading));
    }
    public int[] getTailL() {
	return tailL;
    }
    private void setTailR() {
	tailR[0] = (int) (x + 10 * Math.sin(heading) + 5 * Math.cos(heading));
	tailR[1] = (int) (y + 10 * Math.cos(heading) - 5 * Math.sin(heading));
    }
    public int[] getTailR() {
	return tailR;
    }

    public Bullet shootBullet() {
	return new Bullet(width,height,xVel,yVel,heading,getNose());
    }

    public void up() {
	double newXVel = xVel - SPEED_FORWARD_STEP * Math.sin(heading);
	double newYVel = yVel - SPEED_FORWARD_STEP * Math.cos(heading);
	double speed = Math.sqrt(Math.pow(newXVel,2)+Math.pow(newYVel,2));
	if (speed < MAX_FORWARD_SPEED) {
	    xVel = newXVel;
	    yVel = newYVel;
	}
    }
    public void down() {
	double newXVel = xVel + SPEED_FORWARD_STEP * Math.sin(heading);
	double newYVel = yVel + SPEED_FORWARD_STEP * Math.cos(heading);
	double speed = Math.sqrt(Math.pow(newXVel,2)+Math.pow(newYVel,2));
	if (speed < MAX_BACKWARD_SPEED) {
	    xVel = newXVel;
	    yVel = newYVel;
	}

    }
    public void left() {
	heading = heading + HEADING_STEP;
	if (heading > 2*Math.PI) heading = heading-2*Math.PI;
    }
    public void right() {
	heading = heading - HEADING_STEP;
	if (heading < 0) heading = 2*Math.PI+heading;
    }

    public void move(int keys) {
	double[] pt = getMovement(keys);
        x = pt[0];
	y = pt[1];
	setShipPoints();
    }

    private double[] getMovement(int keys) {
	if ((keys & 1)==1) up();
	if ((keys & 2)==2) down();
	if ((keys & 4)==4) left();
	if ((keys & 8)==8) right();
	//setVelocity();
	double[] pt = wrap(x+xVel,y+yVel);
        return pt;
    }

    private double[] wrap(double x,double y) {
	double newX = x;
	double newY = y;
	if ( x < 0 ) newX = width + x;
	else if ( x >= width ) newX = x - width;
	
	if ( y < 0 ) newY = height + y;
	else if ( y >= height ) newY = y - height;
	
	return new double[] {newX,newY};
    }

    public int getX() { return (int) x; }
    public int getY() { return (int) y; }
    public double getXVel() { return xVel; }
    public double getYVel() { return yVel; }
    public double getHeading() { return heading; }

    public double getActionX(int keys) {
        return getMovement(keys)[0];
    }
    public double getActionY(int keys) {
        return getMovement(keys)[1];
    }

    public void setSensors(int[] vals) {
        sensors = vals;
    }
    public int[] getSensors() {
        return sensors;
    }

    public double[][] getL1Points() {
        // pts[0] is straight ahead, pts[i+1] proceeds clockwise
        double[][] pts = new double[16][2];
        for (int i = 0; i < 16; i++) {
            pts[i] = wrap(x-50*Math.sin(heading + (i*Math.PI/8)),
                          y-50*Math.cos(heading + (i*Math.PI/8)));
        }
        return pts;
    }
    public double[][] getL2Points() {
        // pts[0] is straight ahead, pts[i+1] proceeds clockwise
        double[][] pts = new double[16][2];
        for (int i = 0; i < 16; i++) {
            pts[i] = wrap(x-70*Math.sin(heading + (i*Math.PI/8)),
                          y-70*Math.cos(heading + (i*Math.PI/8)));
        }
        return pts;
    }
    public double[][] getL3Points() {
        // pts[0] is straight ahead, pts[i+1] proceeds clockwise
        double[][] pts = new double[16][2];
        for (int i = 0; i < 16; i++) {
            pts[i] = wrap(x-90*Math.sin(heading + (i*Math.PI/8)),
                          y-90*Math.cos(heading + (i*Math.PI/8)));
        }
        return pts;
    }
    public double[][] getL4Points() {
        // pts[0] is straight ahead, pts[i+1] proceeds clockwise
        double[][] pts = new double[16][2];
        for (int i = 0; i < 16; i++) {
            pts[i] = wrap(x-120*Math.sin(heading + (i*Math.PI/8)),
                          y-120*Math.cos(heading + (i*Math.PI/8)));
        }
        return pts;
    }
    public double[][] getL5Points() {
        // pts[0] is straight ahead, pts[i+1] proceeds clockwise
        double[][] pts = new double[16][2];
        for (int i = 0; i < 16; i++) {
            pts[i] = wrap(x-160*Math.sin(heading + (i*Math.PI/8)),
                          y-160*Math.cos(heading + (i*Math.PI/8)));
        }
        return pts;
    }
    public double[][] getL6Points() {
        // pts[0] is straight ahead, pts[i+1] proceeds clockwise
        double[][] pts = new double[16][2];
        for (int i = 0; i < 16; i++) {
            pts[i] = wrap(x-210*Math.sin(heading + (i*Math.PI/8)),
                          y-210*Math.cos(heading + (i*Math.PI/8)));
        }
        return pts;
    }
}
