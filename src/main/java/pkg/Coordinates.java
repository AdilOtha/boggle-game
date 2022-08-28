package pkg;

/**
 * Defines the data and methods that represent X and Y coordinates of a letter in the grid
 * and the direction moved in to reach that letter.
 */
public class Coordinates {
    // The X coordinate of a letter in the puzzle grid.
    int x;
    // The Y coordinate of a letter in the puzzle grid.
    int y;
    /**
     * The direction moved for shifting to that letter from an adjacent letter. Could be any of the following values:
     * ‘U’  Up
     * ‘D’  Down
     * ‘L’  Left
     * ‘R’  Right
     * ‘N’  North
     * ‘S’  South
     * ‘E’  East
     * ‘W’  West
     * ‘X’  Source
     * The directions North, South, East, West are considered as rotated 45 degrees in anti-clockwise direction.
     */
    char direction;

    public Coordinates(int x, int y, char direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public char getDirection() {
        return direction;
    }
}
