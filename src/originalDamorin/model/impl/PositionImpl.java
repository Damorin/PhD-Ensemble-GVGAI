package originalDamorin.model.impl;

import originalDamorin.model.Position;

/**
 * Provides an X and Y Coordinate for a sprite in the game world.
 *
 * @author Damien Anderson (Damorin)
 */
public class PositionImpl implements Position {

    private int x;
    private int y;

    /**
     * The constructor for creating a new set of coordinates in the Observation
     * Grid.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public PositionImpl(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

}
