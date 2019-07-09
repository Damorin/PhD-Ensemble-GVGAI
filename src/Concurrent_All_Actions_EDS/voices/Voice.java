package Concurrent_All_Actions_EDS.voices;

import core.game.StateObservation;
import tools.ElapsedCpuTimer;

import java.util.List;
import java.util.concurrent.Callable;

public interface Voice extends Callable<List<Opinion>> {
    void initializeAnalysis(StateObservation stateObs, ElapsedCpuTimer elapsedCpuTimer);
    List<Opinion> performAnalysis();
}
