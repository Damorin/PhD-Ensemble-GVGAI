package originalDamorin.model.impl;

import originalDamorin.model.Position;
import originalDamorin.model.SpriteInformation;
import core.game.Observation;

/**
 * Holds information about an individual "sprite" within the game world.
 * 
 * Sprites are entities which represents objects and characters in the game.
 * 
 * @author Damien Anderson (Damorin)
 *
 */
public class SpriteInformationImpl implements SpriteInformation {

	private Position position;
	private Boolean beneficialGoal;
	private Observation sprite;

	/**
	 * The constructor to use when only the {@link Observation}ion is given.
	 * Sets the goal validity to false.
	 * 
	 * @param observation
	 *            the sprite to store information about
	 */
	public SpriteInformationImpl(final Observation observation) {
		this.sprite = observation;
		this.beneficialGoal = false;
	}

	/**
	 * The constructor for when a {@link Position} is given along with an
	 * {@link Observation}. Sets the goal validity to false.
	 * 
	 * @param position
	 *            the {@link Position}
	 * @param observation
	 *            the {@link Observation}
	 */
	public SpriteInformationImpl(final Position position,
			final Observation observation) {
		this.position = position;
		this.sprite = observation;
		this.beneficialGoal = false;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public void setIsBeneficialSprite(Boolean valid) {
		this.beneficialGoal = valid;
	}

	@Override
	public boolean isBeneficialGoal() {
		return this.beneficialGoal;
	}

	@Override
	public Observation getObservation() {
		return this.sprite;
	}
}
