package originalDamorin.voices.shortRange;

import originalDamorin.model.WorldInformation;
import originalDamorin.voices.Opinion;
import originalDamorin.voices.Voice;
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.List;

/**
 * A Short range {@link Voice} which is designed with extremely accurate close
 * range sight for quick survival actions.
 * 
 * @author Damien Anderson (Damorin)
 *
 */
public class SurvivalVoice implements Voice {

	private static final int DEFAULT_ACTION = 0;

	private static final double SHOUT = 10.0;
	private static final double WHISPER = 0.0;

	private StateObservation stateObs;
	private Opinion opinion;

	private WorldInformation worldInformation;

	private double urgency;
	private int bestAction;

	/**
	 * The constructor takes in the current state of the game as a
	 * {@link StateObservation}.
	 * 
	 * @param stateObs
	 *            the current state of the game
	 */
	public SurvivalVoice(StateObservation stateObs) {
		this.stateObs = stateObs;
	}

	@Override
	public Opinion askOpinion(ElapsedCpuTimer elapsedTimer,
			WorldInformation worldInformation) {
		this.worldInformation = worldInformation;
		checkUrgency();
		return opinion;
	}

	private void checkUrgency() {
		analyseVicinity();
		opinion = new Opinion(bestAction, urgency);
	}

	private void analyseVicinity() {
		urgency = WHISPER;
		bestAction = DEFAULT_ACTION;
		double bestScore = stateObs.getGameScore();

		List<StateObservation> nextStates = this.worldInformation
				.getImmediateStates();

		for (int action = 0; action < nextStates.size(); action++) {
			if (scoreIsLowerOrGameLost(nextStates, action)) {
				urgency = SHOUT;
			} else if (scoreIsHigher(bestScore, nextStates, action)) {
				bestScore = nextStates.get(action).getGameScore();
				bestAction = action;
			} else if (scoreIsEqualAndGameNotLost(bestScore, nextStates, action)) {
				bestAction = action;
			}
		}
	}

	private boolean scoreIsLowerOrGameLost(List<StateObservation> nextStates,
			int action) {
		return nextStates.get(action).getGameScore() < stateObs.getGameScore()
				|| nextStates.get(action).getGameWinner() == Types.WINNER.PLAYER_LOSES;
	}

	private boolean scoreIsHigher(double bestScore,
			List<StateObservation> nextStates, int action) {
		return nextStates.get(action).getGameScore() > bestScore;
	}

	private boolean scoreIsEqualAndGameNotLost(double bestScore,
			List<StateObservation> nextStates, int action) {
		return nextStates.get(action).getGameScore() == bestScore
				&& nextStates.get(action).getGameWinner() != Types.WINNER.PLAYER_LOSES;
	}

	@Override
	public void update(StateObservation stateObs) {
		this.stateObs = stateObs;
	}

	@Override
	public WorldInformation getUpdatedWorldInformation() {
		return this.worldInformation;
	}
}
