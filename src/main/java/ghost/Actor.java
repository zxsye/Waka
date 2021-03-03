package ghost;

import processing.core.PImage;

public abstract class Actor implements Collide {
    protected int x, y;
    protected int startX, startY;
    protected int speed;
    protected App app;
    protected Direction nexDir;
    protected Direction curDir;

    // Animation
    protected PImage sprite;

    public Actor(int x, int y, int speed, App app) {
        this.app = app;
        this.x = x * 16;
        this.y = y * 16;
        this.startX = this.x;
        this.startY = this.y;
        this.speed = speed;

        this.nexDir = Direction.LEFT;
        this.curDir = Direction.LEFT;

    }
    public Actor(int pixelX, int pixelY) {
        this.x = pixelX;
        this.y = pixelY;
    }
    public boolean draw() {
        app.image(this.sprite, this.x-4, this.y-4);
        return true;
    }

    public int getTop() {
        return this.y;
    }
    public int getBottom() {
        return this.y + 16;
    }
    public int getLeft() {
        return this.x;
    }
    public int getRight() {
        return this.x + 16;
    }
    public int getx() {
        return this.x;
    }
    public int gety() {
        return this.y;
    }
    public abstract boolean tick();
    public abstract boolean move();

    public boolean tryMove(Direction d) {
        if (d == Direction.STOP) {
            return false;
        }

        int[] coords = d.update(this.x, this.y, this.speed);
        int ox = this.x;
        int oy = this.y;

        this.x = coords[0];
        this.y = coords[1];

        if (interact() == -1) {
            this.app.getManager().death();
            return false;
        } else if (this.checkWall()) {
            this.x = ox;
            this.y = oy;
            return false;
        }
        return true;

    }
    public boolean checkWall() {
        // Checks whether current Actor collides with ANY CELL
        Cell[][] grid = this.app.getManager().getArena().getGrid();

        for (Cell[] row : grid) {
            for (Cell c : row) {
                if (c.isWall() && this.isCollide(c)) {
                    return true;
                }
            }
        }
        return false;
    }
    public void changeDirection(int keyCode) {
        // Used for user-directed change in direction
        if (keyCode == App.UP) {
            nexDir = Direction.UP;
        } else if (keyCode == App.DOWN) {
            nexDir = Direction.DOWN;
        } else if (keyCode == App.LEFT) {
            nexDir = Direction.LEFT;
        } else if (keyCode == App.RIGHT) {
            nexDir = Direction.RIGHT;
        }
    }
    public abstract int interact();
    public void resetActor() {
        this.x = this.startX;
        this.y = this.startY;
        changeDirection(App.LEFT);
    }

}
