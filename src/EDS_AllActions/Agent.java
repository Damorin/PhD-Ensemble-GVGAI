package EDS_AllActions;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

public class Agent extends AbstractPlayer {

    private Arbitrator decisionSystem = new GeneralArbitrator();

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedCpuTimer) {
//        decisionSystem.addVoice(new EDS_AllActions.voices.Yolobot.Agent(stateObs, elapsedCpuTimer));
        decisionSystem.addVoice(new EDS_AllActions.voices.olets.usingHeuristics.Agent(stateObs, elapsedCpuTimer, "COGPaper.heuristics.MaximizeScoreHeuristic"));
        decisionSystem.addVoice(new EDS_AllActions.voices.olets.usingHeuristics.Agent(stateObs, elapsedCpuTimer, "COGPaper.heuristics.MaximizeExplorationHeuristic"));
//        decisionSystem.addVoice(new EDS_AllActions.voices.pessimisticMCTS.Agent(stateObs, elapsedCpuTimer, "COGPaper.heuristics.MaximizeScoreHeuristic"));
//        decisionSystem.addVoice(new EDS_AllActions.voices.mcts.Agent(stateObs, elapsedCpuTimer, "COGPaper.heuristics.MaximizeScoreHeuristic"));
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        return decisionSystem.makeDecision(stateObs, elapsedTimer);
    }

}
