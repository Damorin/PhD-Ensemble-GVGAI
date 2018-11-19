package originalDamorin.model;

import java.util.List;

import ontology.Types.ACTIONS;
import originalDamorin.Agent;
import core.game.Observation;
import core.game.StateObservation;

/**
 * Used to store and analyse data about the game world.
 * 
 * @author Damien Anderson (Damorin)
 *
 */
public interface WorldInformation {

	/**
	 * Resets the {@link WorldInformation} object.
	 */
	void reset();

	/**
	 * Returns the {@link StateObservation}s surrounding the {@link Agent}'s
	 * current position.
	 * 
	 * @return list of {@link StateObservation}
	 */
	List<StateObservation> getImmediateStates();

	/**
	 * Updates the current {@link StateObservation} of the game.
	 * 
	 * @param stateObs
	 *            the new {@link StateObservation}
	 */
	void update(StateObservation stateObs);

	/**
	 * Returns true if a goal has been set, false otherwise.
	 * 
	 * @return boolean
	 */
	boolean hasGoalBeenSet();

	/**
	 * Used to set whether or not a goal is beneficial to the agent.
	 * 
	 * @param validity
	 *            true for beneficial, false otherwise
	 */
	void setGoalValidity(boolean validity);

	/**
	 * Sets a new {@link Observation} as a goal for the {@link Agent}.
	 * 
	 * @param observation
	 *            the Observation to set as a goal.
	 */
	void setGoal(Observation observation);

	/**
	 * Returns the {@link Observation} which has been set as a goal.
	 * 
	 * @return an {@link Observation}
	 */
	Observation getGoal();

	/**
	 * Sets a path from the {@link Agent}'s position to the goal's position.
	 * 
	 * @param pathToGoal
	 *            the path
	 */
	void setPathToGoal(List<ACTIONS> pathToGoal);

	/**
	 * Returns the path from the {@link Agent} to the goal.
	 * 
	 * @return the path
	 */
	List<ACTIONS> getPathToGoal();
}
