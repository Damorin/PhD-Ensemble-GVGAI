package originalDamorin.voices;

import tools.ElapsedCpuTimer;
import originalDamorin.model.WorldInformation;
import core.game.StateObservation;

/**
 * An interface for defining the various Voice implementations.
 * 
 * @author Damien Anderson (Damorin)
 *
 */
public interface Voice {

	/**
	 * Decides the best action from the point of view of the voice as well as
	 * how urgent it thinks it is.
	 * 
	 * @return An {@link Opinion}
	 */
	Opinion askOpinion(ElapsedCpuTimer elapsedTimer,
                       WorldInformation worldInformation);

	/**
	 * Update the current {@link StateObservation}.
	 * 
	 * @param stateObs
	 *            the updated {@link StateObservation}
	 */
	void update(StateObservation stateObs);

	/**
	 * Returns the {@link WorldInformation} object with any updates applied to
	 * its information.
	 * 
	 * @return the updated {@link WorldInformation}
	 */
	WorldInformation getUpdatedWorldInformation();
}
