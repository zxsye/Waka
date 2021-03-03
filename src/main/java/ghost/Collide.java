package ghost;

public interface Collide {
    /**
    * Returns the top boundary, as a y pixel coordinate.
    * @return the top boundary y pixel coordinate
    */
    public int getTop();

    /**
    * Returns the bottom boundary, as a y pixel coordinate.
    * @return the bottom boundary y pixel coordinate
    */
    public int getBottom();

    /**
    * Returns the left boundary, as an x pixel coordinate.
    * @return the left boundary y pixel coordinate
    */
    public int getLeft();

    /**
    * Returns the right boundary, as an x pixel coordinate.
    * @return the right boundary y pixel coordinate
    */
    public int getRight();

    /**
    * Returns the x coordinate of the object, either as pixel or grid coordinate.
    * @return the x coordinate, pixel or grid.
    */
    public int getx();

    /**
    * Returns the y coordinate of the object, either as pixel or grid coordinate.
    * @return the y coordinate, pixel or grid.
    */
    public int gety();

    /**
    * Return whether two objects have collided based on their boundaries,
    * using their pixel coordinates for comparison.
    * @param other another Collide object
    * @return true if current object collides with the argument
    */
    public default boolean isCollide(Collide other) {
        boolean x_overlaps = (this.getLeft() < other.getRight()) && (this.getRight() > other.getLeft());
        boolean y_overlaps = (this.getTop() < other.getBottom()) && (this.getBottom() > other.getTop());
        return x_overlaps && y_overlaps;
    }
}
