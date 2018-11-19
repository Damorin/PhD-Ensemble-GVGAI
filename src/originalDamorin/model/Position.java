package originalDamorin.model;

import core.game.Observation;
import core.game.StateObservation;

/**
 * Represents a set of coordinates in the Observation Grid provided by the
 * {@link StateObservation}. A map of the game world.
 * 
 * @author Damien Anderson (Damorin)
 *
 */
public interface Position {

	/**
	 * Returns the current x position of this {@link Observation}
	 * 
	 * @return int x
	 */
	int getX();

	/**
	 * Returns the current y position of this {@link Observation}
	 * 
	 * @return int y
	 */
	int getY();

}
