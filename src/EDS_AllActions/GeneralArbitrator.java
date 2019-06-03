package EDS_AllActions;

import EDS_AllActions.voices.Opinion;
import EDS_AllActions.voices.Voice;
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.List;

public class GeneralArbitrator implements Arbitrator {

    private List<Voice> voices = new ArrayList<>();
    private List<Opinion> opinions = new ArrayList<>();
    private List<Types.ACTIONS> actions = new ArrayList<>();

    @Override
    public void addVoice(Voice voice) {
        voices.add(voice);
    }

    @Override
    public Types.ACTIONS makeDecision(StateObservation stateObs, ElapsedCpuTimer elapsedCpuTimer) {
        actions = stateObs.getAvailableActions(true);
        for (Voice voice : voices) {
            opinions.addAll(voice.performAnalysis(stateObs, elapsedCpuTimer));
        }
        return null;
    }
}
