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
public class AsyncArbitrator {

    private List<Voice> voices;
    private List<Opinion> opinions;
    private Random randomGenerator = new Random();
    private List<Types.ACTIONS> actions;
    private int current_voice;
    private final int SAFE_CHECKS = 5;

    public AsyncArbitrator() {
        this.voices = new ArrayList<>();
        this.opinions = new ArrayList<>();
        this.actions = new ArrayList<>();
        this.current_voice = 0;
    }

    public void addVoice(Voice voice) {
        this.voices.add(voice);
    }

    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        if(current_voice == 0)
        {
            opinions.clear();
        }

        Voice v = voices.get(current_voice);
        StateObservation startingState = advanceState(stateObs.copy());
        if(startingState == null)
        {
            //Oops, something went wrong. Let's go for a central arbitration.
            current_voice = 0;

            return first_voice(stateObs, elapsedTimer);
            //return central_arbitration(stateObs, elapsedTimer);
        }

        Opinion op = v.askOpinion(startingState, elapsedTimer, (int) elapsedTimer.remainingTimeMillis());
        this.opinions.add(op);
        current_voice++;

        if(current_voice == voices.size())
        {
            //Time to decide:
            current_voice = 0;
            return selectHighestValueOpinion().getAction();
        }

        return Types.ACTIONS.ACTION_NIL;

    }


    private StateObservation advanceState(StateObservation stateObs)
    {
        int n_voices = voices.size();
        int remaining_cycles = n_voices - current_voice - 1;

        while(remaining_cycles > 0)
        {
            boolean canDoNIL = nilIsSafe(stateObs);
            if(canDoNIL)
            {
                stateObs.advance(Types.ACTIONS.ACTION_NIL);
            }else
            {
                return null;
            }
            remaining_cycles--;
        }

        return stateObs;
    }

    private boolean nilIsSafe(StateObservation stateObs)
    {
        boolean safe = true;
        int safe_checks = SAFE_CHECKS;

        while(safe && safe_checks > 0)
        {
            StateObservation stateCopy = stateObs.copy();
            stateCopy.advance(Types.ACTIONS.ACTION_NIL);
            safe = (stateCopy.getGameWinner() != Types.WINNER.PLAYER_LOSES);
            safe_checks--;
        }

        return safe;
    }

    public Types.ACTIONS central_arbitration(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        opinions.clear();
        int analysis_time = (int) (elapsedTimer.remainingTimeMillis() / voices.size()) - 1;
        int voiceNumber = 1;
        for (Voice voice : voices) {
            elapsedTimer.setMaxTimeMillis(analysis_time * voiceNumber);
            this.opinions.add(voice.askOpinion(stateObs, elapsedTimer, analysis_time));
            voiceNumber++;
        }
        return selectHighestValueOpinion().getAction();
//        return selectRandomOpinion().getAction();
//        return selectDemocraticOption().getAction();
    }

    public Types.ACTIONS first_voice(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
//        System.out.println("Deflecting to first opinion " + stateObs.getGameTick());
        this.opinions.clear();
        this.opinions.add(voices.get(0).askOpinion(stateObs, elapsedTimer, (int) (elapsedTimer.remainingTimeMillis() - 1)));
        return this.opinions.get(0).getAction();
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
//        System.out.println(bestOpinion.getName());
        return bestOpinion;
    }

    private Opinion selectDemocraticOption() {
        Map<Types.ACTIONS, Integer> actionFrequencies = new HashMap<>();
        Map.Entry<Types.ACTIONS, Integer> maxEntry = null;


        for (Opinion opinion : this.opinions) {
            Types.ACTIONS action = opinion.getAction();
            actionFrequencies.putIfAbsent(action, 0);
            actionFrequencies.put(action, actionFrequencies.get(action) + 1);
        }

        for (Map.Entry<Types.ACTIONS, Integer> entry : actionFrequencies.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }

        return new Opinion(maxEntry.getKey(), 0, "Us");
    }

    private Opinion selectRandomOpinion() {
        int opinion = randomGenerator.nextInt(opinions.size());
        return opinions.get(opinion);
    }

    public void setAvailableActions(ArrayList<Types.ACTIONS> availableActions) {
        this.actions.addAll(availableActions);
    }
}