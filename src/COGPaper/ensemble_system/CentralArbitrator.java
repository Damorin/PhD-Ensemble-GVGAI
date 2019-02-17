package COGPaper.ensemble_system;

import COGPaper.ensemble_system.voices.Opinion;
import COGPaper.ensemble_system.voices.Voice;
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.*;

/**
 * The central arbitrator which houses the {@link Voice}s and makes the final decision on what action to take.
 * <p>
 * Created by Damorin on 11/05/2017.
 */
public class CentralArbitrator {

    public static final int ANALYSIS_TIME = 12; // Constant to define the amount of analysis time per voice
    private List<Voice> voices;
    private List<Opinion> opinions;
    private Random randomGenerator = new Random();
    private List<Types.ACTIONS> actions;

    public CentralArbitrator() {
        this.voices = new ArrayList<>();
        this.opinions = new ArrayList<>();
        this.actions = new ArrayList<>();
    }

    public void addVoice(Voice voice) {
        this.voices.add(voice);
    }

    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        opinions.clear();
        for (Voice voice : voices) {
            elapsedTimer.setMaxTimeMillis(ANALYSIS_TIME);
            this.opinions.add(voice.askOpinion(stateObs, elapsedTimer, ANALYSIS_TIME));
        }
        return selectHighestValueOpinion().getAction();
//        return selectRandomOpinion().getAction();
//        return selectDemocraticOption();
    }

    private Opinion selectHighestValueOpinion() {
        List<Opinion> bestActions = new ArrayList<>();
        double bestValue = -Double.MAX_VALUE;
        Opinion bestOpinion = null;
        for (int i = 0; i < opinions.size(); i++) {
            double value = opinions.get(i).getEstimatedValue();
            if (value > bestValue) {
                bestActions.clear();
                bestValue = value;
                bestOpinion = opinions.get(i);
                bestActions.add(opinions.get(i));
            } else if (value == bestValue) {
                bestActions.add(opinions.get(i));
            }
        }

        if (bestActions.size() > 1) {
            return bestActions.get(randomGenerator.nextInt(bestActions.size()));
        }
        return bestOpinion;
    }

    private Opinion selectDemocraticOption() {
        List<Opinion> bestActions = new ArrayList<>();
        Map<Types.ACTIONS, Integer> actionFrequencies = new HashMap<>();
        Map.Entry<Types.ACTIONS, Integer> maxEntry = null;

        for (Opinion opinion : this.opinions) {
            Types.ACTIONS action = opinion.getAction();
            actionFrequencies.put(action, actionFrequencies.get(action) + 1);
        }

        for (Map.Entry<Types.ACTIONS, Integer> entry : actionFrequencies.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }

        return new Opinion(maxEntry.getKey(), 0);
    }

    private Opinion selectRandomOpinion() {
        int opinion = randomGenerator.nextInt(opinions.size());
        return opinions.get(opinion);
    }

    public void setAvailableActions(ArrayList<Types.ACTIONS> availableActions) {
        this.actions.addAll(availableActions);
    }
}
