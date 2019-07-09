package Concurrent_All_Actions_EDS;

import Concurrent_All_Actions_EDS.actionSelection.ActionSelectionPolicy;
import Concurrent_All_Actions_EDS.actionSelection.HighestValueActionSelectionPolicy;
import Concurrent_All_Actions_EDS.voices.Opinion;
import Concurrent_All_Actions_EDS.voices.Voice;
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GeneralArbitrator implements Arbitrator {

    //    private int ANALYSIS_TIME; // Constant to define the amount of analysis time per voice
    private boolean DEBUG = false;

    private List<Voice> voices = new ArrayList<>();
    private List<Opinion> opinions = new ArrayList<>();
    private List<Types.ACTIONS> actions = new ArrayList<>();
    private ExecutorService executorService;

    private ActionSelectionPolicy policy;

    public GeneralArbitrator() {
        policy = new HighestValueActionSelectionPolicy();
    }

    @Override
    public void addVoice(Voice voice) {
        voices.add(voice);
//        ANALYSIS_TIME = (40 / voices.size()) - 1;
    }

    @Override
    public Types.ACTIONS makeDecision(StateObservation stateObs, ElapsedCpuTimer elapsedCpuTimer) {
        actions = stateObs.getAvailableActions(true);
        opinions.clear();
        List<Future<List<Opinion>>> futures = new ArrayList<>();
//        int voiceNumber = 1;
        for (Voice voice : voices) {
//            elapsedCpuTimer.setMaxTimeMillis(ANALYSIS_TIME * voiceNumber);
            voice.initializeAnalysis(stateObs, elapsedCpuTimer);
            Future<List<Opinion>> future = executorService.submit(voice);
            futures.add(future);
//            voiceNumber++;
        }
        for (Future<List<Opinion>> future : futures) {
            while (!future.isDone()) {
            }
            try {
                opinions.addAll(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        if (DEBUG) {
            System.out.println("Opinions before combining: " + opinions.size());
        }
        opinions = combineOpinions();

        if (DEBUG) {
            System.out.println("Opinions after combining: " + opinions.size());
        }

        return policy.selectAction(opinions);
    }

    @Override
    public void initializeThreadPool() {
        executorService = Executors.newFixedThreadPool(voices.size());
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
}
