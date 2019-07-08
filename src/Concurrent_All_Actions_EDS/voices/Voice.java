package Concurrent_All_Actions_EDS.voices;

import core.game.StateObservation;
import tools.ElapsedCpuTimer;

import java.util.List;
import java.util.concurrent.Callable;

public interface Voice {
    List<Opinion> performAnalysis(StateObservation stateObs, ElapsedCpuTimer elapsedCpuTimer);
}
