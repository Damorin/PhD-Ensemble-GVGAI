package originalDamorin.decisionSystem;

import tools.ElapsedCpuTimer;
import originalDamorin.model.WorldInformation;
import originalDamorin.model.impl.WorldInformationImpl;
import originalDamorin.voices.Opinion;
import originalDamorin.voices.Voice;
import originalDamorin.voices.longRange.SimulatedExplorationVoice;
import originalDamorin.voices.mediumRange.OpenLoopMCTSVoice;
import originalDamorin.voices.shortRange.SurvivalVoice;
import core.game.StateObservation;

/**
 * A {@link CentralArbitrator} which uses pre-specified {@link Voice}s to make its
 * decisions.
 *
 * In this {@link CentralArbitrator} the {@link Opinion}s are not always trusted,
 * and some higher priority takes place when deciding on actions.
 * 
 * @author Damien Anderson (Damorin)
 *
 */
public class InformedDecisionSystem implements CentralArbitrator {

	private static final int TIME_REMAINING = 20;
	private static final double SHOUT = 10.0;
	private Voice longRangeVoice;
	private Voice midRangeVoice;
	private Voice shortRangeVoice;

	private WorldInformation worldInformation;

	/**
	 * Constructor for the {@link InformedDecisionSystem} which
	 * initialises the {@link Voice}s for decision making.
	 * 
	 * @param stateObs
	 *            The {@link StateObservation} to initialise {@link Voice}s with
	 * @param elapsedTimer
	 *            The {@link ElapsedCpuTimer}
	 */
	public InformedDecisionSystem(final StateObservation stateObs,
			final ElapsedCpuTimer elapsedTimer) {

		if (stateObs == null) {
			throw new IllegalArgumentException();
		}

		worldInformation = new WorldInformationImpl(stateObs, elapsedTimer);
		initialiseVoices(stateObs);
	}

	private void initialiseVoices(final StateObservation stateObs) {
		midRangeVoice = new OpenLoopMCTSVoice(stateObs);
		shortRangeVoice = new SurvivalVoice(stateObs);
		longRangeVoice = new SimulatedExplorationVoice(stateObs);
	}

	@Override
	public final void update(final StateObservation stateObs) {
		worldInformation.update(stateObs);

		longRangeVoice.update(stateObs);
		midRangeVoice.update(stateObs);
		shortRangeVoice.update(stateObs);
	}

	@Override
	public final int selectAction(final ElapsedCpuTimer elapsedTimer) {
		if (agentHasStartedLate(elapsedTimer)) {
			return midRangeVoice.askOpinion(elapsedTimer, worldInformation)
					.getAction();
		}

		Opinion shortOpinion = shortRangeVoice.askOpinion(elapsedTimer,
				worldInformation);
		if (isShortVoiceShouting(shortOpinion)) {
			worldInformation.reset();
			return shortOpinion.getAction();
		}

		longRangeVoice.askOpinion(elapsedTimer, worldInformation);
		Opinion midOpinion = midRangeVoice.askOpinion(elapsedTimer,
				worldInformation);
		worldInformation.reset();
		return midOpinion.getAction();
	}

	private boolean isShortVoiceShouting(Opinion shortOpinion) {
		return shortOpinion.getUrgency() == SHOUT;
	}

	private boolean agentHasStartedLate(final ElapsedCpuTimer elapsedTimer) {
		return elapsedTimer.elapsedMillis() > TIME_REMAINING;
	}

}
