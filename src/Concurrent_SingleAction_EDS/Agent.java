package Concurrent_SingleAction_EDS;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

/**
 * A basic Ensemble Decision System agent.
 * <p>
 * Needs a lot of work!
 * <p>
 * Created by kisenshi on 11/05/17.
 */
public class Agent extends AbstractPlayer {

    private CentralArbitrator ensemble = new CentralArbitrator();
//    private AsyncArbitrator ensemble = new AsyncArbitrator();

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedCpuTimer) {
        ensemble.setAvailableActions(stateObs.getAvailableActions(true));
//        ensemble.addVoice((new Concurrent_SingleAction_EDS.voices.olets.Agent(stateObs, elapsedCpuTimer, "COGPaper.heuristics.MaximizeScoreHeuristic")));
        ensemble.addVoice((new Concurrent_SingleAction_EDS.voices.olets.Agent(stateObs, elapsedCpuTimer, "COGPaper.heuristics.MaximizeExplorationHeuristic")));
//        ensemble.addVoice((new Concurrent_SingleAction_EDS.voices.rs.Agent(stateObs, elapsedCpuTimer, "COGPaper.heuristics.KnowledgeDiscoveryHeuristic")));
//        ensemble.addVoice((new Concurrent_SingleAction_EDS.voices.olets.Agent(stateObs, elapsedCpuTimer, "COGPaper.heuristics.KnowledgeEstimationHeuristic")));
//        ensemble.addVoice((new Concurrent_SingleAction_EDS.voices.Yolobot.Agent(stateObs, elapsedCpuTimer)));
        ensemble.initialiseThreadPool();
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        return ensemble.act(stateObs, elapsedTimer);
    }
}
