package Damorin;

import Damorin.actionSelection.ActionSelectionPolicy;
import Damorin.actionSelection.HighestValueActionSelectionPolicy;
import Damorin.voices.Opinion;
import Damorin.voices.Voice;
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneralArbitrator implements Arbitrator {

    private int ANALYSIS_TIME; // Constant to define the amount of analysis time per voice
    private boolean DEBUG = false;

    private List<Voice> voices = new ArrayList<>();
    private List<Opinion> opinions = new ArrayList<>();
    private List<Types.ACTIONS> actions = new ArrayList<>();

    private ActionSelectionPolicy policy;

    public GeneralArbitrator() {
        policy = new HighestValueActionSelectionPolicy();
    }

    @Override
    public void addVoice(Voice voice) {
        voices.add(voice);
        ANALYSIS_TIME = (40 / voices.size()) - 1;
    }

    @Override
    public Types.ACTIONS makeDecision(StateObservation stateObs, ElapsedCpuTimer elapsedCpuTimer) {
        actions = stateObs.getAvailableActions(true);
        opinions.clear();
        int voiceNumber = 1;
        for (Voice voice : voices) {
            elapsedCpuTimer.setMaxTimeMillis(ANALYSIS_TIME * voiceNumber);
            opinions.addAll(voice.performAnalysis(stateObs, elapsedCpuTimer));
            voiceNumber++;
        }

        if (DEBUG) {
            System.out.println("Opinions before combining: " + opinions.size());
        }
        opinions = combineOpinions();

        if (DEBUG) {
            System.out.println("Opinions after combining: " + opinions.size());
        }

        printOpinions();

        return policy.selectAction(opinions);
    }

    private List<Opinion> combineOpinions() {
        Map<Types.ACTIONS, Double> opinionMap = new HashMap<>();

        for (Types.ACTIONS action : actions) {
            for (Opinion opinion : opinions) {
                if (opinion.getAction().equals(action)) {
                    if (opinionMap.get(action) == null) {
                        opinionMap.put(action, opinion.getValue());
                    } else {
                        double value = opinionMap.get(action) + opinion.getValue();
                        opinionMap.put(action, value);
                    }
                }
            }
        }
        List<Opinion> opinionList = new ArrayList<>();
        for (Types.ACTIONS action : opinionMap.keySet()) {
            opinionList.add(new Opinion(action, opinionMap.get(action)));
        }
        return opinionList;
    }

    private void printOpinions() {
        for (Opinion opinion : opinions) {
            System.out.println("Action: " + opinion.getAction() + " Value: " + opinion.getValue());
        }
    }
}
