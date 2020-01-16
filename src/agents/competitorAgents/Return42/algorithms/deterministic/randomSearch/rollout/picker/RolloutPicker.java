package agents.competitorAgents.Return42.algorithms.deterministic.randomSearch.rollout.picker;

import agents.competitorAgents.Return42.algorithms.deterministic.randomSearch.rollout.strategy.RollOutStrategy;

public interface RolloutPicker {

	public void iterationFinished(boolean didFindPlan);
	public RollOutStrategy getCurrentRolloutStrategy();
	
}
