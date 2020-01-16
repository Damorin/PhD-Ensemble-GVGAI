package agents.edsAgents.Concurrent_All_Actions_EDS;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

public class Agent extends AbstractPlayer {

    private Arbitrator decisionSystem = new GeneralArbitrator();

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedCpuTimer) {
        decisionSystem.addVoice(new agents.edsAgents.Concurrent_All_Actions_EDS.voices.olets.usingHeuristics.Agent(stateObs, elapsedCpuTimer, "COGPaper.heuristics.MaximizeScoreHeuristic"));
        decisionSystem.addVoice(new agents.edsAgents.Concurrent_All_Actions_EDS.voices.olets.usingHeuristics.Agent(stateObs, elapsedCpuTimer, "COGPaper.heuristics.MaximizeExplorationHeuristic"));
//        decisionSystem.addVoice(new agents.edsAgents.Concurrent_All_Actions_EDS.voices.pessimisticMCTS.Agent(stateObs, elapsedCpuTimer, "COGPaper.heuristics.MaximizeScoreHeuristic"));
//        decisionSystem.addVoice(new agents.edsAgents.Concurrent_All_Actions_EDS.voices.mcts.Agent(stateObs, elapsedCpuTimer, "COGPaper.heuristics.MaximizeScoreHeuristic"));
//        decisionSystem.initializeThreadPool();
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        return decisionSystem.makeDecision(stateObs, elapsedTimer);
    }

}
