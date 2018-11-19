package Damorin2.arbiter;

import core.game.StateObservation;
import Damorin2.Opinion;
import tools.ElapsedCpuTimer;

/**
 * Created by Damorin on 03/03/2018.
 */
public interface Arbiter {

    Opinion solve(StateObservation so, ElapsedCpuTimer elapsedTimer);
}
