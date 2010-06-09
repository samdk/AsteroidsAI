package samdk.asteroids.core;

import java.util.Random;
import java.util.LinkedList;

public class Asteroid {
    private final double SPEED_MODIFIER = 100.0 / Constants.FPS; // .5;
    private static final int DEFAULT_GENERATION = 1;

    private double x,y,x_vel,y_vel;
    private int size,width,height;
    private int pointValue;

    private Random gen;

    private int generation;

    public Asteroid(int width,int height,Random gen) {
        this(width,height,gen,DEFAULT_GENERATION);
    }
    public Asteroid(int width,int height,Random gen,int generation) {
        this.generation = generation;
        this.gen = gen;
	this.width = width;
	this.height = height;
        pointValue = 1;
	size = 50 + gen.nextInt(5);
	int pos = gen.nextInt(4);
	if ( pos == 1 ) { // top
	    x = gen.nextInt(width/8);
	    y = gen.nextInt(height);
	}
	else if ( pos == 1 ) { //bottom
	    x = width - gen.nextInt(width/8);
	    y = gen.nextInt(height);
	}
	else if ( pos == 2) { //left
	    x = gen.nextInt(width);
	    y = gen.nextInt(height/8);
	}
	else { //right
	    x = gen.nextInt(width);
	    y = height - gen.nextInt(height/8);
	}
	x_vel = gen.nextDouble() * SPEED_MODIFIER * (1 + generation / 5);
	y_vel = gen.nextDouble() * SPEED_MODIFIER * (1 + generation / 5);
	if ( gen.nextBoolean() ) x_vel = -x_vel;
	if ( gen.nextBoolean() ) y_vel = -y_vel;
	if ( gen.nextBoolean() ) x += width / 8;
	if ( gen.nextBoolean() ) y += height / 8;
    }
    public Asteroid(Asteroid parent) {
        generation = parent.generation + 1;
        size = (int) (parent.size * .55);
        gen = parent.gen;
        pointValue = parent.pointValue + 1;
	x_vel = gen.nextDouble() * SPEED_MODIFIER * (1 + generation / 5);
	y_vel = gen.nextDouble() * SPEED_MODIFIER * (1 + generation / 5);
	if ( gen.nextBoolean() ) x_vel = -x_vel;
	if ( gen.nextBoolean() ) y_vel = -y_vel;
	if ( gen.nextBoolean() ) x += width / 8;
	if ( gen.nextBoolean() ) y += height / 8;
        x = parent.getX();
        y = parent.getY();
        width = parent.width;
        height = parent.height;
    }

    public void move() {
	double[] pt = wrap(x+x_vel,y+y_vel);
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

    public boolean checkCollision(int x,int y) {
	return x >= this.x && x <= this.x + size && y >= this.y && y <= this.y + size;
    }
    public boolean checkCollision(double x, double y) {
        return checkCollision((int)x,(int)y);
    }

    public LinkedList<Asteroid> spawnChildren() {
        LinkedList<Asteroid> children = new LinkedList<Asteroid>();
        if (generation < 3) {
            children.add(new Asteroid(this));
            children.add(new Asteroid(this));
        }
        return children;
    }

    public int getSize() { return size; }
    public int getX() { return (int) x; }
    public int getY() { return (int) y; }
    public int getPointValue() { return pointValue; }
}
