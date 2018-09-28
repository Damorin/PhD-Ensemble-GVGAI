package damorin.voices;

import core.game.StateObservation;
import tools.ElapsedCpuTimer;

public interface Voice {

    Opinion askOpinion(StateObservation stateObs, ElapsedCpuTimer elapsedTimer);
}
