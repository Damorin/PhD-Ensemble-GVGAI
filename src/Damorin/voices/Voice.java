package Damorin.voices;

import core.game.StateObservation;
import tools.ElapsedCpuTimer;

import java.util.List;

public interface Voice {
    List<Opinion> performAnalysis(StateObservation stateObs, ElapsedCpuTimer elapsedCpuTimer);
}
