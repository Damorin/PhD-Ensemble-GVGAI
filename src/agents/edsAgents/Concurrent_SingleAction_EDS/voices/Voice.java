package agents.edsAgents.Concurrent_SingleAction_EDS.voices;

import agents.edsAgents.Concurrent_SingleAction_EDS.CentralArbitrator;
import core.game.StateObservation;
import tools.ElapsedCpuTimer;

import java.util.concurrent.Callable;

/**
 * Template for a Voice.
 * <p>
 * TODO: Add Opinions and fully integrate the functionality with the {@link CentralArbitrator}
 * <p>
 * Created by Damorin on 11/05/2017.
 */
public interface Voice extends Callable<Opinion> {
    Opinion askOpinion();
    void initialiseVoice(StateObservation stateObs, ElapsedCpuTimer elapsedTimer);
}
