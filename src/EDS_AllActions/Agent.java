package EDS_AllActions;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

public class Agent extends AbstractPlayer {

    private Arbitrator decisionSystem = new GeneralArbitrator();

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedCpuTimer) {
        decisionSystem.addVoice();
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        return decisionSystem.makeDecision(stateObs, elapsedTimer);
    }

}
