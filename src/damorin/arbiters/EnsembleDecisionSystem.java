package damorin.arbiters;

import core.game.StateObservation;
import damorin.voices.Opinion;
import damorin.voices.Voice;
import damorin.voices.search.pessimisticMCTS.PessimisticMCTSVoice;
import damorin.voices.search.rhea.RHEAVoice;
import damorin.voices.search.standardMCTS.MCTSVoice;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnsembleDecisionSystem implements Arbiter {

    public static final int MAXIMUM_ANALYSIS_TIME = 39;
    private List<Voice> voices;
    private List<Opinion> opinions;
    private Random rng;


    public EnsembleDecisionSystem(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        voices = new ArrayList<>();
        opinions = new ArrayList<>();
        rng = new Random();

        initializeVoices(stateObs, elapsedTimer);

    }

    private void initializeVoices(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        voices.add(new MCTSVoice(stateObs, elapsedTimer));
//        voices.add(new PessimisticMCTSVoice(stateObs, elapsedTimer));
        voices.add(new RHEAVoice(stateObs, elapsedTimer));
    }

    @Override
    public Types.ACTIONS makeDecision(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        opinions.clear();
        for (Voice voice : voices) {
            elapsedTimer.setMaxTimeMillis(MAXIMUM_ANALYSIS_TIME / voices.size());
            opinions.add(voice.askOpinion(stateObs, elapsedTimer));
        }

        return chooseRandomly();
//        return chooseDemocratically();
    }

    private Types.ACTIONS chooseRandomly() {
        return opinions.get(rng.nextInt(opinions.size())).getAction();
    }

    private Types.ACTIONS chooseDemocratically() {
        return null;
    }
}
