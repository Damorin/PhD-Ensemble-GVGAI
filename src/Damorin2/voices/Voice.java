package Damorin2.voices;

import core.game.StateObservation;
import Damorin2.Opinion;
import tools.ElapsedCpuTimer;

/**
 * Created by Damien Anderson on 22/02/2018.
 */
public interface Voice {

    Opinion solve(StateObservation so, ElapsedCpuTimer elapsedTimer);
}
