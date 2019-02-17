package COGPaper.ensemble_system.voices;

import core.game.StateObservation;
import tools.ElapsedCpuTimer;

/**
 * Template for a Voice.
 * <p>
 * TODO: Add Opinions and fully integrate the functionality with the {@link COGPaper.ensemble_system.CentralArbitrator}
 * <p>
 * Created by Damorin on 11/05/2017.
 */
public interface Voice {
    Opinion askOpinion(StateObservation stateObs, ElapsedCpuTimer elapsedTimer, int analysisTime);
}
