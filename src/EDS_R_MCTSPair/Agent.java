package EDS_R_MCTSPair;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import EDS_R_MCTSPair.arbiters.Arbiter;
import EDS_R_MCTSPair.arbiters.EnsembleDecisionSystem;
import ontology.Types;
import tools.ElapsedCpuTimer;

/**
 * The main class necessary to run an agent on the GVGAI framework.
 *
 * @author Damien Anderson
 **/
public class Agent extends AbstractPlayer {

    private Arbiter arbiter;

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        arbiter = new EnsembleDecisionSystem(stateObs, elapsedTimer);
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        return arbiter.makeDecision(stateObs, elapsedTimer);
    }
}
