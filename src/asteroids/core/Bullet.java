package samdk.asteroids.core;

public class Bullet {
    public static final int MAX_DISTANCE = 300;

    private final double BASE_SPEED = 600.0 / Constants.FPS; // 3

    private int width,height;

    private double x,y;
    private double xVel,yVel;
    private double speed;
    private double xDist,yDist;

    public Bullet(int width,int height,double xVel, double yVel,double shipHeading,int[] shipNose) {
	this.width = width;
	this.height = height;
	
	double baseX = BASE_SPEED;
	double baseY = BASE_SPEED;

	this.xVel = xVel - (baseX * Math.sin(shipHeading));
	this.yVel = yVel - (baseY * Math.cos(shipHeading));

	this.x = shipNose[0];
	this.y = shipNose[1];
	xDist = 0;
	yDist = 0;
    }

    public int getDistance() {
	return (int) Math.sqrt(Math.pow(xDist,2)+Math.pow(yDist,2));
    }

    public void move() {
	double[] pt = wrap(x+xVel,y+yVel);
	xDist += xVel;
	yDist += yVel;
	x = pt[0];
	y = pt[1];
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
}
