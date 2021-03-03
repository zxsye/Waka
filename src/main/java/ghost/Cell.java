package ghost;

import processing.core.PImage;

public abstract class Cell implements Collide {
    protected int x;
    protected int y;
    protected PImage art;

    protected boolean isWall;
    protected boolean isFruit;
    protected boolean isSuperFruit;
    protected boolean isSoda;

    /**
    * Constructs cell containing its grid coordinates and image.
    * @param x x grid coordinate
    * @param y y grid coordinate
    * @param art PImage file of cell display art
    */
    public Cell(int x, int y, PImage art) {
        this.x = x;
        this.y = y;
        this.art = art;
    }

    /**
    * Returns the grid x (column) coordinate, not pixel coordinate, of the cell.
    * @return x coordinate
    */
    public int getGridx() { return this.x; }

    /**
    * Returns the grid y (row) coordinate, not pixel coordinate, of the cell.
    * @return y coordinate
    */
    public int getGridy() { return this.y; }

    /**
    * Returns the pixel x (column) coordinate, not grid coordinate, of the cell.
    * @return x coordinate
    */
    public int getx() { return this.x * 16; }

    /**
    * Returns the pixel y (row) coordinate, not grid coordinate, of the cell.
    * @return y coordinate
    */
    public int gety() { return this.y * 16; }

    /**
    * Sets the grid x (column) coordinate, not pixel coordinate, of the cell.
    * @param x the x coordinate
    */
    public void setx(int x) { this.x = x; }

    /**
    * Sets the grid y (row) coordinate, not pixel coordinate, of the cell.
    * @param y the y coordinate
    */
    public void sety(int y) { this.y = y; }

    /**
    * Returns whether cell is a wall.
    * @return true if cell is wall
    */
    public boolean isWall() {
        return this.isWall;
    }

    /**
    * Returns whether cell is a fruit.
    * @return true if cell is fruit or superfruit
    */
    public boolean isFruit() {
        return this.isFruit;
    }

    /**
    * Returns whether cell is a superfruit.
    * @return true if cell is superfruit
    */
    public boolean isSuperFruit() {
        return this.isSuperFruit;
    }

    /**
    * Returns the cell image, as a PImage file.
    * @return the cell image
    */
    public PImage getArt() {
        return this.art;
    }

    /**
    * Returns the cell's top boundary, as a y pixel coordinate.
    * @return the top boundary y pixel coordinate
    */
    public int getTop() {
        return this.y * 16;
    }

    /**
    * Returns the cell's bottom boundary, as a y pixel coordinate.
    * @return the bottom boundary y pixel coordinate
    */
    public int getBottom() {
        return this.y * 16 + 16;
    }

    /**
    * Returns the cell's left boundary, as a x pixel coordinate.
    * @return the left boundary x pixel coordinate
    */
    public int getLeft() {
        return this.x * 16;
    }

    /**
    * Returns the cell's right boundary, as a x pixel coordinate.
    * @return the right boundary x pixel coordinate
    */
    public int getRight() {
        return this.x * 16 + 16;
    }

    /**
    * Return if cell is a soda.
    * @return true if cell is soda
    */
    public boolean isSoda() {
        return this.isSoda;
    }

    /**
    * Return success of trying to eat the cell.
    * @return cell's content was sucessfully eaten.
    */
    public abstract boolean eat();

}

class Wall extends Cell {
    /**
    *
    *
    */
    public Wall(int x, int y, PImage art) {
        super(x, y, art);
        this.isWall = true;
        this.isFruit = false;
        this.isSuperFruit = false;
        this.isSoda = false;
    }

    /**
    *
    *
    */
    public boolean eat() {
        return false;
    }
}

class Path extends Cell {

    /**
    * Constructor for a path file, where Actor objects can walk across.
    * Path cells can hold a fruit, superfruit, or soda.
    * @param x x grid coordinate
    * @param y y grid coordinate
    * @param art display image of Cell
    * @param isFruit whether the cell contains fruit
    */
    public Path(int x, int y, PImage art, boolean isFruit) {
        super(x, y, art);
        this.isWall = false;
        this.isFruit = isFruit;
        this.isSuperFruit = false;
    }

    /**
    * Attempts to eat what the cell contains.
    * @return true if the cell was successfully eaten
    */
    public boolean eat() {
        if (this.isFruit) {
            this.isFruit = false;
            this.art = null;
            return true;
        }
        return false;
    }

    /**
    * Returns the cells's top boundary, as a y pixel coordinate.
    * The boundaries are smaller than a normal wall cell, because the player
    * must stand closer to the centre of the cell to eat it.
    * @return the top boundary y pixel coordinate
    */
    public int getTop() {
        return this.y * 16 + 8;
    }

    /**
    * Returns the cells's bottom boundary, as a y pixel coordinate.
    * The boundaries are smaller than a normal wall cell, because the player
    * must stand closer to the centre of the cell to eat it.
    * @return the bottom boundary y pixel coordinate
    */
    public int getBottom() {
        return this.y * 16 + 8;
    }

    /**
    * Returns the cells's left boundary, as a x pixel coordinate.
    * The boundaries are smaller than a normal wall cell, because the player
    * must stand closer to the centre of the cell to eat it.
    * @return the left boundary x pixel coordinate
    */
    public int getLeft() {
        return this.x * 16 + 8;
    }

    /**
    * Returns the cells's right boundary, as a x pixel coordinate.
    * The boundaries are smaller than a normal wall cell, because the player
    * must stand closer to the centre of the cell to eat it.
    * @return the right boundary x pixel coordinate
    */
    public int getRight() {
        return this.x * 16 + 8;
    }
}

class SuperFruit extends Path {

    /**
    * Constructs a superfruit cell with grid coordinates and display file. As a
    * path file, it can be walked on. Superfruit cells are also fruit cells.
    * @param x x grid coordinate
    * @param y y grid coordinate
    * @param art display image of Cell
    */
    public SuperFruit(int x, int y, PImage art) {
        super(x, y, art, true);
        this.isSuperFruit = true;
    }

    /**
    * Attempts to eat what the cell contains.
    * @return true if the superfruit was successfully eaten
    */
    public boolean eat() {
        if (this.isFruit && this.isSuperFruit) {
            this.isFruit = false;
            this.isSuperFruit = false;
            this.art = null;
            return true;
        }
        return false;
    }
}

class Soda extends Path {

    /**
    * Constructs a Soda cell with grid coordinates and display file. As a
    * path file, it can be walked on. Soda cells are not fruit cells.
    * @param x x grid coordinate
    * @param y y grid coordinate
    * @param art display image of Cell
    */
    public Soda(int x, int y, PImage art) {
        super(x, y, art, false);
        this.isSoda = true;
    }

    /**
    * Attempts to eat what the cell contains.
    * @return true if the soda was successfully eaten
    */
    public boolean eat() {
        if (this.isSoda) {
            this.isSoda = false;
            this.art = null;
            return true;
        }
        return false;
    }
}
