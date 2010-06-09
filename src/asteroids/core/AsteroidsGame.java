package samdk.asteroids.core;

import java.util.Random;
import java.util.LinkedList;
import java.util.Arrays;

public class AsteroidsGame {
/*
 * class variables
 */
    // game defaults (if options are not provided in constructor)
    static final int SCREEN_WIDTH  = 800;
    static final int SCREEN_HEIGHT = 480;
    static final int ASTEROID_COUNT = 14;
    static final int LIFE_COUNT = 0;
    
    // min number of frames between shots fired
    private final int SHOT_SPACING = Constants.FPS / 8;
    


/*
 * instance variables
 */
    // basic game stuff
    private int width,height;
    private int asteroidCount;
    private int lifeCount;

    // game objects
    private LinkedList<Asteroid> asteroids;
    private LinkedList<Bullet> bullets;
    private Ship ship;

    // inner game state
    private int frameCount; // number of frames played in current game
    private int keys; // bitmask for the keys being pressed
    private int lastShot; // number of frames away last shot was (min 0)
    private boolean shoot; // shoot when ready
    private int immune; // number of frames ship is immune for (min 0)
    private int wait; // number of frames to wait before a new level (min 0)

    // externally accessible state
    private boolean gameOver;
    private int level;
    private int points;
    private int lives;

    // other
    private Random gen;



/*
 * constructors
 */
    public AsteroidsGame() {
	this(SCREEN_WIDTH,SCREEN_HEIGHT);
    }
    public AsteroidsGame(int width,int height) {
        this(width,height,ASTEROID_COUNT,LIFE_COUNT);
    }
    public AsteroidsGame(int width,int height,int asteroidCount,int lifeCount) {
	this.width = width;
	this.height = height;
	gen = new Random();
        this.asteroidCount = asteroidCount;
        this.lifeCount = lifeCount;
        startNewGame();
    }



/*
 * public methods
 */

    // game accessors

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }


    // object accessors

    public LinkedList<Asteroid> getAsteroids() {
	return asteroids;
    }
    public Ship getShip() {
	return ship;
    }
    public LinkedList<Bullet> getBullets() {
	return bullets;
    }


    // state accessors

    public boolean isGameOver() {
	return gameOver;
    }
    public int getKeys() {
        return keys;
    }
    public boolean shipIsImmune() {
	return immune > 0;
    }
    public int getPoints() {
	return points;
    }
    public int getLevel() {
	return level;
    }
    public int getLivesLeft() {
	return lives;
    }
    public int getFrameCount() {
        return frameCount;
    }


    // input

    public void doShoot(boolean on) {
        shoot = on;
    }

    public void doUp(boolean on) {
        if (on) keys |=  1; // set bit
        else    keys &=~1; // clear bit
    }

    public void doDown(boolean on) {
        if (on) keys |= 2;
        else    keys &=~2;
    }

    public void doLeft(boolean on) {
        if (on) keys |= 4;
        else    keys &=~4;
    }

    public void doRight(boolean on) {
        if (on) keys |= 8;
        else    keys &=~8;
    }

    // moves game forward 1 frame
    public void update() {
	if ( wait > 1 )
            wait--;
        else if ( wait == 1 )
            startNewLevel();
        else if ( asteroids.size() == 0 )
            nextLevelInit();
        else
            checkCollisions();
        updatePositions();
        handleNewShot();
        setSensors();
        frameCount++;
    }

    // ends game
    public void setGameOver() {
        gameOver = true;
    }
    public void newGame() {
        startNewGame();
    }
    public void newGame(long seed) {
        gen = new Random(seed);
        newGame();
    }


/*
 * private methods
 */
    
    // game
    
    private void setSensors() {
        int[] sensors = new int[16];
        int i = 0;
        for ( double[] pt : ship.getL1Points() ) {
            for ( Asteroid asteroid : asteroids ) {
                if ( asteroid.checkCollision(pt[0],pt[1]) ) {
                    sensors[i] = 1;
                }
            }
            i++;
        }
        i = 0;
        for ( double[] pt : ship.getL2Points() ) {
            if ( sensors[i] == 0 ) {
                for ( Asteroid asteroid : asteroids ) {
                    if ( asteroid.checkCollision(pt[0],pt[1]) ) {
                        sensors[i] = 2;
                    }
                }
            }
            i++;
        }
        i = 0;
        for ( double[] pt : ship.getL3Points() ) {
            if ( sensors[i] == 0 ) {
                for ( Asteroid asteroid : asteroids ) {
                    if ( asteroid.checkCollision(pt[0],pt[1]) ) {
                        sensors[i] = 3;
                    }
                }
            }
            i++;
        }
        i = 0;
        for ( double[] pt : ship.getL4Points() ) {
            if ( sensors[i] == 0 ) {
                for ( Asteroid asteroid : asteroids ) {
                    if ( asteroid.checkCollision(pt[0],pt[1]) ) {
                        sensors[i] = 4;
                    }
                }
            }
            i++;
        }
        i = 0;
        for ( double[] pt : ship.getL5Points() ) {
            if ( sensors[i] == 0 ) {
                for ( Asteroid asteroid : asteroids ) {
                    if ( asteroid.checkCollision(pt[0],pt[1]) ) {
                        sensors[i] = 5;
                    }
                }
            }
            i++;
        }
        i = 0;
        for ( double[] pt : ship.getL6Points() ) {
            if ( sensors[i] == 0 ) {
                for ( Asteroid asteroid : asteroids ) {
                    if ( asteroid.checkCollision(pt[0],pt[1]) ) {
                        sensors[i] = 6;
                    }
                }
            }
            i++;
        }
        ship.setSensors(sensors);
    }

    private void checkCollisions() {
        checkBulletCollisions();
        checkShipCollisions();
    }

    private void checkBulletCollisions() {
	if ( bullets.size() > 0 ) {
	    LinkedList<Asteroid> remA = new LinkedList<Asteroid>();
	    for ( Asteroid asteroid : asteroids ) {
		LinkedList<Bullet> remB = new LinkedList<Bullet>();
		boolean collided = false;
		for ( Bullet b : bullets ) {
		    if (!asteroid.checkCollision(b.getX(),b.getY())) {
			remB.add(b);
		    }
		    else {
			collided = true;
                        if (!gameOver) points += asteroid.getPointValue();
		    }
		}
		if (!collided) remA.add(asteroid);
                else remA.addAll(asteroid.spawnChildren());
		bullets = remB;
	    }
	    asteroids = remA;
	}
    }

    private void checkShipCollisions() {
	if ( asteroids.size() > 0 ) {
	    for ( Asteroid asteroid : asteroids ) {
		if ((asteroid.checkCollision(ship.getNose()[0],ship.getNose()[1]) ||
		     asteroid.checkCollision(ship.getTailL()[0],ship.getTailL()[1]) ||
		     asteroid.checkCollision(ship.getTailR()[0],ship.getTailR()[1])) &&
		     immune == 0 ) {
		    doShipCollision();
		}
	    }
	}
    }

    private void updatePositions() {
        updateAsteroids();
        updateBullets();
        updateShip();
    }

    private void handleNewShot() { 
        if (shoot && lastShot == 0) {
	    bullets.add(ship.shootBullet());
	    lastShot = SHOT_SPACING;
	}
	if (lastShot > 0) lastShot--;
	if (immune > 0) immune--;
    }

    // adds 1 to level and initializes the next level counter
    private void nextLevelInit() {
	level++;
	wait = Constants.FPS;
    }

    private void startNewLevel() {
	spawnShip();
	genAsteroids();
	resetBullets();
        wait = 0;
    }

    private void startNewGame() {
        keys = 0;
        lastShot = 0;
        shoot = false;
        immune = 0;

        gameOver = false;
        frameCount = 0;
        level = 0;
        points = 0;
        lives = lifeCount;

        startNewLevel();
    }


    // bullets
    
    private void resetBullets() {
	bullets = new LinkedList<Bullet>();
    }

    private void updateBullets() {
	LinkedList<Bullet> remaining = new LinkedList<Bullet>();
	for (Bullet b : bullets) {
	    if (b.getDistance() < Bullet.MAX_DISTANCE) {
		b.move();
		remaining.add(b);
	    }
	}
	bullets = remaining;
    }


    // asteroids

    private void genAsteroids() {
        int count = asteroidCount + ((int) (level * .2 * asteroidCount));
        asteroids = new LinkedList<Asteroid>();
        for ( int i = 0; i < count; i++ ) {
	    asteroids.add(new Asteroid(width,height,gen));
	}
    }

    private void updateAsteroids() {
	for ( Asteroid asteroid : asteroids ) {
	    asteroid.move();
	}
    }


    // ship

    private void updateShip() {
        ship.move(keys);
    }

    private void spawnShip() {
        ship = new Ship(width,height);
        grantImmunity();
    }
 
    private void grantImmunity() {
	immune = (int) (Constants.FPS * 2);
    }
    private void grantImmunity(int immuneTime) {
	immune = immuneTime;
    }
    
    // decide what to do if the ship collides
    private void doShipCollision() {
	if ( lives > 0 ) {
	    spawnShip();
	    lives--;
	}
	else {
	    gameOver = true;
	}
    }
}
