package originalDamorin.model;

import core.game.Observation;

/**
 * Stores information regarding a sprite in the game world.
 * 
 * @author Damien Anderson (Damorin)
 *
 */
public interface SpriteInformation {

	/**
	 * The current position of the sprite in the game world as a
	 * {@link Position}
	 * 
	 * @return the x and y coordinate stored in {@link Position}
	 */
	Position getPosition();

	/**
	 * Returns if this {@link Observation} is a beneficial goal.
	 * 
	 * @return true if beneficial, false if not.
	 */
	boolean isBeneficialGoal();

	/**
	 * Set true if this {@link Observation} is beneficial, or false if not.
	 * 
	 * @param beneficial
	 *            boolean true or false.
	 */
	void setIsBeneficialSprite(Boolean beneficial);

	/**
	 * Returns the {@link Observation} held within this
	 * {@link SpriteInformation}
	 * 
	 * @return an {@link Observation}
	 */
	Observation getObservation();
}
