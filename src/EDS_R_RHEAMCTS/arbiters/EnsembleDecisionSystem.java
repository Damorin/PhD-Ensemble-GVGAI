package EDS_R_RHEAMCTS.arbiters;

import EDS_R_RHEAMCTS.voices.Opinion;
import EDS_R_RHEAMCTS.voices.Voice;
import EDS_R_RHEAMCTS.voices.search.rhea.RHEAVoice;
import EDS_R_RHEAMCTS.voices.search.standardMCTS.MCTSVoice;
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnsembleDecisionSystem implements Arbiter {

    private static final int MAXIMUM_ANALYSIS_TIME = 39;
    private List<Voice> voices;
    private List<Opinion> opinions;
    private Random rng;
    private List<Types.ACTIONS> availableActions;


    public EnsembleDecisionSystem(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        voices = new ArrayList<>();
        opinions = new ArrayList<>();
        availableActions = stateObs.getAvailableActions(true);
        rng = new Random();

        initializeVoices(stateObs, elapsedTimer);

    }

    private void initializeVoices(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        voices.add(new MCTSVoice(stateObs, elapsedTimer));
        voices.add(new RHEAVoice(stateObs, elapsedTimer));
//        voices.add(new RHEAVoice(stateObs, elapsedTimer));
    }

    @Override
    public Types.ACTIONS makeDecision(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        opinions.clear();
        int i = 1;
        for (Voice voice : voices) {
            ElapsedCpuTimer copy = elapsedTimer.copy();
            copy.setMaxTimeMillis(MAXIMUM_ANALYSIS_TIME / voices.size() * i++);
            opinions.add(voice.askOpinion(stateObs, copy));
        }

        return chooseRandomly();
//        return chooseDemocratically();
    }

    private Types.ACTIONS chooseRandomly() {
        return opinions.get(rng.nextInt(opinions.size())).getAction();
    }

    private Types.ACTIONS chooseDemocratically() {
        List<Integer> actionFrequencies = new ArrayList<>();
        for (int i = 0; i < availableActions.size(); i++) {
            actionFrequencies.add(0);
        }
        for (Opinion opinion : opinions) {
            Integer actionFrequency = actionFrequencies.get(opinion.getAction().ordinal());
            actionFrequencies.set(opinion.getAction().ordinal(), actionFrequency + 1);
        }

        for (int i = 0; i < availableActions.size(); i++) {
            if (actionFrequencies.get(i) > 1) {
                System.out.println("Woo, Democracy!");
                return Types.ACTIONS.values()[i];
            }
        }

        return chooseRandomly();
    }
}
