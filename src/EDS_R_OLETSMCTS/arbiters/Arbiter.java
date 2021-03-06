package EDS_R_OLETSMCTS.arbiters;

import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

public interface Arbiter {
    Types.ACTIONS makeDecision(StateObservation stateObs, ElapsedCpuTimer elapsedTimer);
}
