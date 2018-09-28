package originalDamorin.arbiter;

import core.game.StateObservation;
import originalDamorin.Opinion;
import tools.ElapsedCpuTimer;

/**
 * Created by Damorin on 03/03/2018.
 */
public interface Arbiter {

    Opinion solve(StateObservation so, ElapsedCpuTimer elapsedTimer);
}
