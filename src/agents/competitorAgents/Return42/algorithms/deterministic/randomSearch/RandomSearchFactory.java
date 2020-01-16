package agents.competitorAgents.Return42.algorithms.deterministic.randomSearch;

import agents.competitorAgents.Return42.algorithms.deterministic.randomSearch.depthControl.DepthControl;
import agents.competitorAgents.Return42.algorithms.deterministic.randomSearch.depthControl.FixedHorizon;
import agents.competitorAgents.Return42.algorithms.deterministic.randomSearch.depthControl.RollingHorizon;
import agents.competitorAgents.Return42.algorithms.deterministic.randomSearch.planning.PlanGenerator;
import agents.competitorAgents.Return42.algorithms.deterministic.randomSearch.planning.PlanKeeper;
import agents.competitorAgents.Return42.algorithms.deterministic.randomSearch.planning.update.NpcAwareUpdatePolicy;
import agents.competitorAgents.Return42.algorithms.deterministic.randomSearch.rollout.heuristic.ScoreHeuristic;
import agents.competitorAgents.Return42.algorithms.deterministic.randomSearch.rollout.picker.AdaptiveRolloutPicker;
import agents.competitorAgents.Return42.knowledgebase.KnowledgeBase;
import core.game.StateObservation;

/**
 * Created by Oliver on 06.05.2015.
 */
public class RandomSearchFactory {

	public static RandomSearch buildForLevel(KnowledgeBase knowledge, StateObservation stateObs, int iterationLimit, boolean randomMovesHaveToBeNilMoves) {
        DepthControl depthControl = pickDepthControlForLevel( stateObs );

        return new RandomSearch(
                new PlanKeeper( new NpcAwareUpdatePolicy() ),
                new PlanGenerator( new ScoreHeuristic(), depthControl, new AdaptiveRolloutPicker(knowledge) ),
                iterationLimit,
                randomMovesHaveToBeNilMoves
        );
	}
	
    private static DepthControl pickDepthControlForLevel(StateObservation so) {
        if (so.getMovablePositions() == null)
            return new RollingHorizon();
        else
            return new FixedHorizon();
    }

}
