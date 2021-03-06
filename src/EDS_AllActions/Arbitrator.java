package EDS_AllActions;

import EDS_AllActions.voices.Voice;
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

public interface Arbitrator {

    void addVoice(Voice voice);
    Types.ACTIONS makeDecision(StateObservation stateObs, ElapsedCpuTimer elapsedCpuTimer);

}
