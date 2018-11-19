package originalDamorin.voices.longRange;

import java.util.ArrayList;
import java.util.List;

import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import originalDamorin.Agent;
import originalDamorin.model.SpriteInformation;
import originalDamorin.model.WorldInformation;
import originalDamorin.model.impl.PositionImpl;
import originalDamorin.model.impl.SpriteInformationImpl;
import originalDamorin.voices.Opinion;
import originalDamorin.voices.Voice;
import core.game.Observation;
import core.game.StateObservation;

/**
 * A Long range {@link Voice} which analyses the sprites in the Game world and
 * determines a goal to aim towards.
 * 
 * The sprites are analysed individually using a directed search algorithm and
 * if it would earn points, this is selected as a goal. Once the goal has been
 * reached a new goal is selected.
 * 
 * @author Damien Anderson (Damorin)
 *
 */
public class AnalysisExplorationVoice implements Voice {

	private StateObservation stateObs;
	private List<Observation>[][] observationGrid;

	private List<SpriteInformation> spritesToEvaluate;
	private List<Observation> spritesExplored;

	private SpriteInformation goal;
	private int directionToTake;

	private WorldInformation worldInformation;

	/**
	 * The constructor for the {@link AnalysisExplorationVoice}. Holds the current state
	 * of the game in the {@link StateObservation}.
	 * 
	 * @param stateObs
	 *            the {@link StateObservation}
	 */
	public AnalysisExplorationVoice(StateObservation stateObs) {
		this.stateObs = stateObs;
		spritesToEvaluate = new ArrayList<>();
		spritesExplored = new ArrayList<>();
	}

	@Override
	public void update(StateObservation stateObs) {
		this.stateObs = stateObs;
	}

	private void scanWorld() {
		observationGrid = null;
		observationGrid = stateObs.getObservationGrid();
		for (int yPos = 0; yPos < observationGrid[0].length; yPos++) {
			for (int xPos = 0; xPos < observationGrid.length; xPos++) {
				List<Observation> observations = observationGrid[xPos][yPos];
				if (!observations.isEmpty()) {
					for (Observation observation : observations) {
						if (!spritesExplored.contains(observation)) {
							if (observation.category == Types.TYPE_AVATAR) {
								Agent.position = new PositionImpl(xPos, yPos);
								spritesExplored.add(observation);
							}
							if (checkIfWall(observation)) {
								spritesExplored.add(observation);
							} else {
								spritesToEvaluate
										.add(new SpriteInformationImpl(
												new PositionImpl(xPos, yPos),
												observation));
							}
						}
					}
				}
			}
		}
	}

	private boolean checkIfWall(Observation observation) {
		return observation.category == 0 && observation.itype == 4;
	}

	@Override
	public Opinion askOpinion(ElapsedCpuTimer elapsedTimer,
			WorldInformation worldInformation) {
		this.worldInformation = worldInformation;
		scanWorld();
		goal = selectAGoal();
		ACTIONS direction = getDirectionTo(goal);
		for (int i = 0; i < Agent.numberOfAvailableActions; i++) {
			if (direction == Agent.availableActions[i]) {
				directionToTake = i;
			}
		}
		return new Opinion(directionToTake, 1.0);
	}

	private SpriteInformation selectAGoal() {
		if (goal == null) {
			for (SpriteInformation sprite : spritesToEvaluate) {
				if (analyse(sprite)) {
					return sprite;
				}
			}
			goal = null;
		}
		return goal;
	}

	private boolean analyse(SpriteInformation sprite) {
		return isValidGoal(sprite);
	}

	private ACTIONS getDirectionTo(SpriteInformation sprite) {
		if (Math.abs(Agent.position.getX() - sprite.getPosition().getX()) > Math
				.abs(Agent.position.getY() - sprite.getPosition().getY())) {
			return checkHorizontalMovement(sprite);
		} else {
			return checkVerticalMovement(sprite);
		}
	}

	private ACTIONS checkHorizontalMovement(SpriteInformation sprite) {
		ACTIONS action = null;
		if (Agent.position.getX() - sprite.getPosition().getX() > 0) {
			action = ACTIONS.ACTION_LEFT;
			Agent.position = new PositionImpl(Agent.position.getX() - 1,
					Agent.position.getY());
		} else {
			action = ACTIONS.ACTION_RIGHT;
			Agent.position = new PositionImpl(Agent.position.getX() + 1,
					Agent.position.getY());
		}
		return action;
	}

	private ACTIONS checkVerticalMovement(SpriteInformation sprite) {
		ACTIONS action = null;
		if (Agent.position.getY() - sprite.getPosition().getY() > 0) {
			action = ACTIONS.ACTION_UP;
			Agent.position = new PositionImpl(Agent.position.getX(),
					Agent.position.getY() - 1);
		} else {
			action = ACTIONS.ACTION_DOWN;
			Agent.position = new PositionImpl(Agent.position.getX(),
					Agent.position.getY() + 1);
		}
		return action;
	}

	private boolean isValidGoal(SpriteInformation sprite) {
		double distance = sprite.getObservation().position.dist(stateObs
				.getAvatarPosition());

		int maxScanDistance = 30;
		int scanDistance = 0;

		StateObservation nextState = stateObs.copy();

		while (distance != 0 && scanDistance < maxScanDistance) {
			nextState.advance(getDirectionTo(sprite));
			distance = sprite.getObservation().position.dist(stateObs
					.getAvatarPosition());
			scanDistance++;
		}

		double evaluateStateScore = evaluateStateScore(stateObs);
		spritesExplored.add(sprite.getObservation());
		spritesToEvaluate.remove(sprite);

		if (evaluateStateScore >= 0) {
			return true;
		} else {
			return false;
		}
	}

	private double evaluateStateScore(StateObservation nextState) {
		double score = nextState.getGameScore();
		if (nextState.getGameWinner() == Types.WINNER.PLAYER_WINS) {
			score += 10000;
		} else if (nextState.getGameWinner() == Types.WINNER.PLAYER_LOSES) {
			score -= 10000;
		}
		return score;
	}

	@Override
	public WorldInformation getUpdatedWorldInformation() {
		return this.worldInformation;
	}
}
