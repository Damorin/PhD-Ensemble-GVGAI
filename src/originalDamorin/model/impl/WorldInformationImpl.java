package originalDamorin.model.impl;

import java.util.ArrayList;
import java.util.List;

import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import originalDamorin.Agent;
import originalDamorin.model.WorldInformation;
import core.game.Observation;
import core.game.StateObservation;

/**
 * Represents the information that has been gathered about the game that is
 * currently being played.
 * 
 * This includes any analysis done by the voices, in order to cut down
 * processing time.
 * 
 * @author Damien Anderson (Damorin)
 *
 */
public class WorldInformationImpl implements WorldInformation {

	private List<StateObservation> immediateStates;
	private List<ACTIONS> pathToGoal;
	private Observation goal;
	private StateObservation beginState;
	private boolean goalValidity;

	/**
	 * The constructor for creating a new {@link WorldInformation} object.
	 * 
	 * @param stateObs
	 *            The {@link StateObservation} of the current state
	 * @param elapsedTimer
	 *            The track of how much time has passed to make a decision
	 */
	public WorldInformationImpl(StateObservation stateObs,
			ElapsedCpuTimer elapsedTimer) {
		this.immediateStates = new ArrayList<>();
		this.pathToGoal = new ArrayList<>();
		this.beginState = stateObs;
	}

	@Override
	public void reset() {
		this.immediateStates.clear();
	}

	@Override
	public List<StateObservation> getImmediateStates() {
		return immediateStates;
	}

	@Override
	public void update(StateObservation stateObs) {
		this.beginState = stateObs;
		analyseCloseStates();
	}

	private void analyseCloseStates() {
		for (int action = 0; action < Agent.numberOfAvailableActions; action++) {
			StateObservation nextState = beginState.copy();
			nextState.advance(Agent.availableActions[action]);
			immediateStates.add(nextState);
		}
	}

	@Override
	public boolean hasGoalBeenSet() {
		return this.goalValidity;
	}

	@Override
	public void setGoal(Observation observation) {
		this.goal = observation;
		this.goalValidity = true;
	}

	@Override
	public Observation getGoal() {
		return this.goal;
	}

	@Override
	public void setGoalValidity(boolean validity) {
		this.goalValidity = validity;
	}

	@Override
	public List<ACTIONS> getPathToGoal() {
		return this.pathToGoal;
	}

	@Override
	public void setPathToGoal(List<ACTIONS> pathToGoal) {
		this.pathToGoal = pathToGoal;
	}
}
