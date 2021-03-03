package ghost;

import processing.core.PImage;
import java.util.*;

public class Waka extends Actor{

    private int lives;
    private int nFruit;

    // Animation
    private boolean switchSprite = false;
    private PImage osprite;
    private PImage spriteClosed;
    private PImage spriteRight;
    private Map<Direction, PImage> sprites = new HashMap<Direction, PImage>();
    private long frameCount;

    // Ghost TRACKING
    private Direction prevDir;


    public Waka(int x, int y, int speed, int lives, App app) {
        super(x, y, speed, app);
        // Parse in sprite images
        this.sprites.put(Direction.UP, app.loadImage("src/main/resources/playerUp.png"));
        this.sprites.put(Direction.DOWN, app.loadImage("src/main/resources/playerDown.png"));
        this.sprites.put(Direction.LEFT, app.loadImage("src/main/resources/playerLeft.png"));
        this.sprites.put(Direction.RIGHT, app.loadImage("src/main/resources/playerRight.png"));
        this.spriteClosed = app.loadImage("src/main/resources/playerClosed.png");

        // Setup attributes

        this.lives = lives;
        this.nFruit = 0;

        this.sprite = sprites.get(nexDir);
        this.osprite = this.sprite;
        this.spriteRight = sprites.get(Direction.RIGHT);

        this.frameCount = 1;
        this.prevDir = nexDir;
    }
    public Waka(int pixelX, int pixelY) {
        // USED ONLY FOR GHOST TRACKING
        super(pixelX, pixelY);
    }
    public boolean tick() {
        if (this.nFruit == this.app.getManager().getArena().getTotalFruits()) {
            this.app.getManager().win();
        }
        // move();
        updateMouth();
        if (move()) {
            return true;
        } else {
            return false;
        }

    }
    public int getFruits() {
        return nFruit;
    }
    public void drawLives() {
        for (int i = 0; i < this.lives; i++) {
            app.image(this.spriteRight, i*26 + 8, 34*16);
        }
    }
    public void updateMouth() {
        if (frameCount++ % 8 == 0 && curDir != Direction.STOP) {
            if (switchSprite == true) {
                sprite = osprite;
                switchSprite = false;
            } else {
                sprite = spriteClosed;
                switchSprite = true;
            }
        }
    }

    // Tick helpers
    public boolean move() {
        // move depending on nexDir, but if not, uses curDir
        // System.out.println(nexDir);
        if (tryMove(nexDir) == true) {
            if (curDir != nexDir) {
                curDir = nexDir;
                prevDir = curDir; // save for ghost tracking

                osprite = sprites.get(nexDir);
                sprite = osprite;
                return true;
            }
        } else {
            if (tryMove(curDir) == false) {
                curDir = Direction.STOP;
                return false;
            }
            return true;
        }
        return false;
    }
    public int interact() {
        // Return -1 if fatal, otherwise 1 for continue,
        // @TODO eating ghost mode and return different one
        int f = checkFruit();
        if (f > 0) {
            if (f == 1 || f == 2) {
                nFruit++; // only count fruits
            }
            if (f == 2 || f == 3) { // SUPERFRUIT TIME!!!
                List<Ghost> ghosts = this.app.getManager().getGhosts();
                for (Ghost g : ghosts) {

                    if ( f == 2 ) {
                        g.beFrightened();
                    } else {
                        g.beSodaFright();
                    }

                }
            }
        };
        int ghostBool = checkGhost();
        if (ghostBool == 1) {
            if (--this.lives > 0) {
                resetActor();
                resetGhosts();
            } else {
                return -1; // DEATH
            }
        }
        return 1;
    }

    public int checkFruit() {
        /* Checks the all Path cells whether collision with fruit has occurred
            - when SuperFruit, return 2
            - when fruit, return 1
            - normal path, return 0
            - when Soda, return 3
        */
        Cell[][] grid = this.app.getManager().getArena().getGrid();

        for (Cell[] row : grid) {
            for (Cell c : row) {
                if (this.isCollide(c)) {
                    if (c.isSuperFruit()) {
                        c.eat();
                        return 2;
                    } else if (c.isFruit()) {
                        c.eat();
                        return 1;
                    } else if (c.isSoda()) {
                        c.eat();
                        return 3;
                    }
                }

            }
        }
        return 0;
    }
    public int checkGhost() {
        /* Check collision with all ghosts:
            - return 1 if collide with alive ghost
            - return 2 if killed ghost
            - return 0 if no collision
        */
        List<Ghost> ghosts = this.app.getManager().getGhosts();
        for (Ghost g : ghosts) {
            if (this.isCollide(g) && g.isDead() == false) {

                if (g.isFrightened()) { // kill the ghost
                    if (g.isSodaFrightened()) {
                        return 1;
                    } else {
                        g.die();
                        return 2;
                    }
                } else {
                    return 1;
                }
            }
        }
        return 0;
    }
    public void resetGhosts() {
        List<Ghost> ghosts = this.app.getManager().getGhosts();
        for (Ghost g : ghosts) {
            g.resetGhost();
        }
    }

    public int[] getCoordsAhead(int x) {
        // Returns coordinates for Ambush type Ghost
        // Finds cell 4 grid spaces ahead using 'nexDir'
        // System.out.println(this.prevDir);
        int[] coords = this.prevDir.update(this.x, this.y, x*16);
        return coords;
    }
    public int getLives() {
        return this.lives;
    }
    public PImage getCurSprite() {
        return this.sprite;
    }
    public Map<Direction, PImage> getAllSprites() {
        return this.sprites;
    }
    public PImage getClosedSprite() {
        return this.spriteClosed;
    }
}
