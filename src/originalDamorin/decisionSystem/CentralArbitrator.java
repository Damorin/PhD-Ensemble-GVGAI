package originalDamorin.decisionSystem;

import tools.ElapsedCpuTimer;
import originalDamorin.voices.Voice;
import core.game.StateObservation;

/**
 * A system which can be used to decide an action for the agent. This may use a
 * variety of different implementations.
 * 
 * @author Damien Anderson (Damorin)
 *
 */
public interface CentralArbitrator {

	/**
	 * Initialises the decision system, activating the {@link Voice}s 
	 * that have been selected for this system.
	 * 
	 * @param stateObs
	 *            The given {@link StateObservation} for decision making.
	 */
	void update(StateObservation stateObs);

	/**
	 * Selects the best action for the current situation from the
	 * {@link StateObservation}.
	 * 
	 * @param elapsedTimer
	 *            The amount of time that has elapsed
	 * @return The integer value of the action chosen
	 */
	int selectAction(ElapsedCpuTimer elapsedTimer);

}
