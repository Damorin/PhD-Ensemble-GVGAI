package EDS_R_MCTSPair.voices;

import core.game.StateObservation;
import tools.ElapsedCpuTimer;

public interface Voice {

    Opinion askOpinion(StateObservation stateObs, ElapsedCpuTimer elapsedTimer);
}
