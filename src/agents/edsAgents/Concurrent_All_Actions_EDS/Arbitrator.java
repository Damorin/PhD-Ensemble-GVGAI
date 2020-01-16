package agents.edsAgents.Concurrent_All_Actions_EDS;

import agents.edsAgents.Concurrent_All_Actions_EDS.voices.Voice;
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

public interface Arbitrator {

    void addVoice(Voice voice);
    Types.ACTIONS makeDecision(StateObservation stateObs, ElapsedCpuTimer elapsedCpuTimer);

    void initializeThreadPool();
}
