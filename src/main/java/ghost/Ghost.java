package ghost;

import processing.core.PImage;
import java.util.*;
import java.lang.Math;
import java.util.Random;

public class Ghost extends Actor {

    protected List<Integer> modeLengths;
    protected GhostMode curMode;
    protected GhostMode prevMode;
    protected int curModeLength; // current mode length (seconds)
    protected int curModeLengthIndex; // which number of modes currently on
    protected int numOfModes; // number of periods
    protected long myFrames; // frames into current mode period

    protected Cell[] corners;
    protected Cell corner;
    protected Waka player;
    protected Collide target;

    // FRIGHTENED MODE
    protected PImage normalSprite;
    protected PImage frightSprite;
    protected PImage sodaSprite;

    protected boolean isSodaFrightened;
    protected int frightenedLength;
    protected int frightenedCounter;

    protected boolean death;
    protected Random genRandom;

    /**
    * Constructs a ghost with x and y grid coordinates.
    * @param x the x grid coordinate
    * @param y the y grid coordinate
    * @param speed the speed
    * @param app the app, used for drawing
    * @param modeLengths a list of integers specifying time spent on each mode,
    * scatter or chase
    */
    public Ghost(int x, int y, int speed, App app, List<Integer> modeLengths) {
        super(x, y, speed, app);
        this.death = false;

        // SPRITE ART
        this.normalSprite = app.loadImage("src/main/resources/ghost.png");
        this.frightSprite = app.loadImage("src/main/resources/frightened.png");
        this.sodaSprite = app.loadImage("src/main/resources/sodaGhost.png");
        this.sprite = this.normalSprite;

        // USUAL MODES
        this.modeLengths = modeLengths;
        this.curMode = GhostMode.SCATTER;
        this.curModeLength = modeLengths.get(0);
        this.curModeLengthIndex = 0;
        this.numOfModes = modeLengths.size();
        this.myFrames = 0;

        // FRIGHTENED MODES
        this.frightenedLength = this.app.getManager().getFrightenedLength();
        this.frightenedCounter = 0;
        this.genRandom = new Random();

        this.isSodaFrightened = false;

        // SETUP OF CORNERS
        Cell corner1 = this.app.getManager().getArena().getGrid()[0][0];
        Cell corner2 = this.app.getManager().getArena().getGrid()[0][28-1];
        Cell corner3 = this.app.getManager().getArena().getGrid()[36-1][28-1];
        Cell corner4 = this.app.getManager().getArena().getGrid()[36-1][0];

        this.corners = new Cell[] {corner1, corner2, corner3, corner4};
        this.corner = corner1; // CHASE type is TopLeft
        this.player = this.app.getManager().getPlayer();
    }

    /**
    * Returns current mode of the ghost.
    * @return current mode
    */
    public GhostMode getCurMode() {
        return this.curMode;
    }

    /**
    * Returns previous mode of the ghost, used when ghost exiting frightened mode
    * and returning to its previous mode.
    * @return previous mode
    */
    public GhostMode getPrevMode() {
        return this.prevMode;
    }

    /**
    * Returns the corner cell the ghost will scatter towards.
    * @return the ghost's target corner cell
    */
    public Cell getCorner() {
        return this.corner;
    }

    /**
    * Draws the ghost. The image depends on its current state as frightened,
    * normal, or invisible.
    * @return true if ghost can be drawn, otherwise false when dead
    */
    public boolean draw() {
        if (this.isDead()) {
            return false;
        }
        app.image(this.sprite, this.x-5, this.y-5);
        return true;
    }

    /**
    * Draws the debug line, connecting the ghost to where it is targetting.
    * @return true if can be drawn, false when frightened or dead
    */
    public boolean drawDebugLine() {
        if (this.isFrightened() || this.isDead()) {
            return false;
        }
        this.app.line(this.x, this.y, this.target.getx(), this.target.gety());
        return true;
    }

    /**
    * Updates the ghost after one frame, and how it responds to the updated
    * arena and other entities. Does not draw the ghost.
    * @return true if can be updated, otherwise false when dead
    */
    public boolean tick() {
        if (this.isDead()) {
            return false;
        }
        updateMode();
        setTarget(this.player);
        move();
        return true;
    }

    /**
    * Updates the mode of the ghost. When frightened, update the frightened
    * counter only. When unfrightened, updates the mode according to the
    * modelengths and resets to the first modelength when all modelengths have
    * been iterated through.
    */
    public void updateMode() {
        if (frightenedCounter > 0) {
            frightenedCounter--;
        } else {
            // NORMAL CHANGE MODES
            if (curMode == GhostMode.FRIGHTENED) {
                // unFrighten only once when timer finishes
                this.unFrightened();
            }
            if (myFrames >= curModeLength * 60) {
                myFrames = 0;
                if (curModeLengthIndex + 1 == numOfModes) {
                    // when final mode runs out
                    curModeLengthIndex = 0;
                    curModeLength = modeLengths.get(0);
                    curMode = GhostMode.SCATTER;
                } else {
                    curModeLength = modeLengths.get(++curModeLengthIndex);
                    curMode = curMode.opp();
                }
            }
            myFrames++; // do not count frames when frightened
        }

    }

    /**
    * Sets the target of the ghost depending on the mode. If currently SCATTER,
    * then only set to corner. If CHASE, then set to the given target which
    * different types of ghost will pass in.
    * @param chaseTarget the target cell or actor to chase
    */
    public void setTarget(Collide chaseTarget) {
        // Set Target based on current Ghost Mode
        if (this.curMode == GhostMode.SCATTER) {
            this.target = this.corner;
        } else if (this.curMode == GhostMode.CHASE){
            this.target = chaseTarget;
        } else {
            this.target = this.corner;
        }
    }

    /**
    * Sets ghost to be frightened only if currently not frightened.
    * If soda-frightened, then reset to normal frightened. Set the frightened
    * counter to the configured time and set sprite to frightened.
    */
    public void beFrightened() {
        // Only switch when first becoming Frightened
        if (this.curMode != GhostMode.FRIGHTENED) {
            this.prevMode = this.curMode;
            this.curMode = GhostMode.FRIGHTENED;
        } else if (this.isSodaFrightened()) {
            this.unFrightened();
            this.beFrightened();
        }
        this.frightenedCounter = this.frightenedLength * 60;
        this.sprite = this.frightSprite;
    }

    /**
    * Sets ghost to be unfrightened and can kill waka again. Removes any type
    * of frightened, including sodaFrightened. Sets mode back to previous mode,
    * and sprite back to the normal sprite.
    */
    public void unFrightened() {
        this.curMode = this.prevMode;
        this.sprite = this.normalSprite;
        this.isSodaFrightened = false;
    }

    /**
    * Sets ghost to be soda-frightened by calling normal frightened then adding
    * on top of that. Switches sprite to the soda sprite.
    */
    public void beSodaFright() {
        this.beFrightened();
        this.isSodaFrightened = true;
        this.sprite = this.sodaSprite;
    }

    /**
    * Selects a direction depending on target and current state, then move
    * towards that direction.
    * @return always true as ghosts cannot pause at wall
    */
    public boolean move() {
        // move depending on nexDir, but if not, uses curDir
        chase(this.target);
        tryMove(nexDir);
        return true; // Ghost moves are guaranteed to work;

    }

    /**
    * Updates direction based on current state and given entity. If frightened
    * move in a random and valid direction, such as not backing on itself.
    * @param entity the chasing target
    */
    public void chase(Collide entity) {
        Map<Double, Direction> moves = possibleMoves(entity);
        // Dead end
        if (moves.size() == 0) {
            this.nexDir = this.nexDir.opp();
            return;
        }

        List<Double> keys = new ArrayList<Double>();
        for (Double k : moves.keySet()) {
            keys.add(k);
        }

        if (this.isFrightened()) {
            // choose random path;
            int rdm = genRandom.nextInt(keys.size());
            this.nexDir = moves.get(keys.get(rdm));
        } else {
            this.nexDir = moves.get(keys.get(0)); // Get first move (shortest)
        }

    }

    /**
    * Returns possible directions with their respective distance to a given entity
    * that the ghost can make for current direction. Returns an empty map
    * if no direction available.
    * @param the chase target
    * @return map containing directions as values, distance as key
    */
    public Map<Double, Direction> possibleMoves(Collide entity) {
        /* For non-opposite directions, retrieve the distance to
        Waka, and add the direction to Map, with distance as key
        */
        Map<Double, Direction> moves = new TreeMap<Double, Direction>();
        for (Direction d : Direction.values() ) {
            if (d != this.nexDir.opp()) { // cannot backtrack

                double dist = canMove(d, entity); // retrieves distance
                if (dist >= 0 ) { // ignores walls
                    moves.put(dist, d);
                }

            }
        }
        return moves;
    }

    /**
    * Similar to tryMove(), but does not actually move ghost. Checks if direction
    * is valid and returns its distance to a given entity.
    * @param d hypothetical direction to travel in
    * @param entity the target to calculate distance to
    */
    public double canMove(Direction d, Collide entity) {
        /**/

        if (d == Direction.STOP) {
            return -1;
        }
        double dist = -1;

        int[] coords = d.update(this.x, this.y, this.speed);
        int ox = this.x;
        int oy = this.y;

        this.x = coords[0];
        this.y = coords[1];

        if (this.checkWall()) {
            dist = -1;
        } else {
            // Waka player = this.app.getPlayer();
            dist = distanceTo(entity); // UPDATE HOW TO CHASE WAKA THROUGH PARAMETER
        }
        this.x = ox;
        this.y = oy;

        return dist;
    }

    /**
    * Returns euclidian distance towards an entity from current position.
    * @return distance to entity from current distance in pixels
    */
    public double distanceTo(Collide entity) {
        double wX = entity.getx();
        double wY = entity.gety();

        double distance = Math.sqrt(Math.pow(this.x - wX, 2)
                                    + Math.pow(this.y - wY, 2));
        return distance;
    }

    /**
    * Deprecated, only to use for Waka for ending the game.
    * @return integer 0 always.
    */
    public int interact() {
        return 0;
    }

    /**
    * Sets the ghost to die.
    */
    public void die() {
        this.death = true;
    }

    /**
    * Returns whether ghost is dead.
    * @return true if ghost is dead
    */
    public boolean isDead() {
        return this.death;
    }

    /**
    * Returns whether ghost is frightened.
    * @return true if ghost is frightened
    */
    public boolean isFrightened() {
        if (frightenedCounter > 0 ) {
            return true;
        }
        return false;
    }

    /**
    * Returns whether ghost is soda-frightened.
    * @return true if ghost is soda-frightened
    */
    public boolean isSodaFrightened() {
        return this.isSodaFrightened;
    }

    /**
    * Rests ghost to starting position, sprite, modes, position in modeLengths,
    * and re-animates the ghost to become alive again... boo!
    */
    public void resetGhost() {
        this.resetActor();
        // RESET GHOST AFTER WAKA DEATH
        this.sprite = normalSprite;
        this.frightenedCounter = 0;
        this.isSodaFrightened = false;
        this.death = false;

        this.myFrames = 0;
        this.curMode = GhostMode.SCATTER;
        this.curModeLength = modeLengths.get(0);
        this.curModeLengthIndex = 0;
    }
}

class Ambusher extends Ghost {

    /**
    * Constructs ambusher type ghost with coordinate, speed, app (for drawing)
    * and modelengths. Sets up its unique sprite and corner target.
    * @param x the x grid coordinate
    * @param y the y grid coordinate
    * @param speed the speed
    * @param app the app, used for drawing
    * @param modeLengths a list of integers specifying time spent on each mode,
    */
    public Ambusher(int x, int y, int speed, App app, List<Integer> modeLengths) {
        super(x, y, speed, app, modeLengths);
        this.normalSprite = app.loadImage("src/main/resources/ambusher.png");
        this.sprite = normalSprite;
        this.corner = corners[1];
    }

    /**
    * Updates sprite state according to the ambusher rules. Will move towards
    * tile 4 grid coordinates ahead of Waka's current position and direction.
    * @return true if alive and update was successful.
    */
    public boolean tick() {
        if (this.isDead()) {
            return false;
        }
        updateMode();

        int[] coords = this.player.getCoordsAhead(4);
        int x = coords[0];
        int y = coords[1];
        Collide ambushTarget = new Waka(x, y);
        setTarget(ambushTarget);

        move();
        return true;

    }
}

class Ignorant extends Ghost {

    /**
    * Constructs ignorant type ghost with coordinate, speed, app (for drawing)
    * and modelengths. Sets up its unique sprite and corner target.
    * @param x the x grid coordinate
    * @param y the y grid coordinate
    * @param speed the speed
    * @param app the app, used for drawing
    * @param modeLengths a list of integers specifying time spent on each mode,
    */
    public Ignorant(int x, int y, int speed, App app, List<Integer> modeLengths) {
        super(x, y, speed, app, modeLengths);
        this.normalSprite = app.loadImage("src/main/resources/ignorant.png");
        this.sprite = normalSprite;
        this.corner = corners[3];
    }

    /**
    * Updates sprite state according to the ignorant rules. Will move towards
    * Waka's position if curr
    * @return true if alive and update was successful.
    */
    public boolean tick() {
        if (this.isDead()) {
            return false;
        }
        updateMode();

        if (distanceTo(this.player) >= 8*16) {
            setTarget(this.player);
        } else {
            setTarget(this.corner);
        }

        move();
        return true;
    }

}

class Whim extends Ghost {

    /**
    * Constructs ignorant type ghost with coordinate, speed, app (for drawing)
    * @param x the x grid coordinate
    * @param y the y grid coordinate
    * @param speed the speed
    * @param app the app, used for drawing
    * @param modeLengths a list of integers specifying time spent on each mode,
    */
    public Whim(int x, int y, int speed, App app, List<Integer> modeLengths) {
        super(x, y, speed, app, modeLengths);
        this.normalSprite = app.loadImage("src/main/resources/whim.png");
        this.sprite = normalSprite;
        this.corner = corners[2];
    }

    /**
    * Updates the current state of the Ghost
    */
    public boolean tick() {
        if (this.isDead()) {
            return false;
        }
        updateMode();

        int[] coords = this.player.getCoordsAhead(2);
        Ghost chaser = this.app.getManager().getGhosts().get(0);
        if (!chaser.isDead()) {
            // Only use Chaser position when alive
            int chaserX = chaser.getx();
            int chaserY = chaser.gety();

            int tileX = coords[0];
            int tileY = coords[1];
            int vecX = tileX - chaserX;
            int vecY = tileY - chaserY;

            Collide ambushTarget = new Waka(chaserX + 2*vecX, chaserY + 2*vecY);
            setTarget(ambushTarget);
        } else {
            // Whim becomes Chaser when Chaser is dead
            setTarget(this.app.getManager().getPlayer());
        }

        move();
        return true;
    }

}
