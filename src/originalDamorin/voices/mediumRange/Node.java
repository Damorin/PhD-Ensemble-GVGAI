package originalDamorin.voices.mediumRange;

import tools.ElapsedCpuTimer;
import originalDamorin.model.WorldInformation;
import core.game.StateObservation;

/**
 * An interface for mid-range searchs. Used by {@link MCTSVoice} and
 * {@link OpenLoopMCTSVoice}.
 * 
 * @author Damien Anderson (Damorin)
 *
 */
public interface Node {

	/**
	 * Run a search on the current {@link StateObservation} and return an
	 * action.
	 * 
	 * @param elapsedTimer
	 *            the timer to make an action
	 * @param worldinformation
	 *            the {@link WorldInformation} for storing information
	 * @return an action
	 */
	int performSearch(ElapsedCpuTimer elapsedTimer,
                      WorldInformation worldinformation);

	/**
	 * Update the {@link StateObservation}.
	 * 
	 * @param stateObs
	 *            the new {@link StateObservation}
	 */
	void setState(StateObservation stateObs);

}
