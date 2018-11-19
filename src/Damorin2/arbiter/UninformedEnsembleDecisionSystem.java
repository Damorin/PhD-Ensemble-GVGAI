package Damorin2.arbiter;

import core.game.StateObservation;
import Damorin2.Opinion;
import Damorin2.voices.Voice;
import Damorin2.voices.sampleMCTS.MCTSVoice;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Damien Anderson on 20/02/2018.
 */
public class UninformedEnsembleDecisionSystem implements Arbiter {

    private List<Voice> experts;
    private List<Opinion> opinions;


    public UninformedEnsembleDecisionSystem(StateObservation so, ElapsedCpuTimer elapsedTimer) {
        experts = new ArrayList<>();
        opinions = new ArrayList<>();
        experts.add(new MCTSVoice(so, elapsedTimer));
//        experts.add(new RHEAVoice(so, elapsedTimer));
//        experts.add(new SimulatedExplorationVoice(so, elapsedTimer));
    }

    public Opinion solve(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        for (Voice voice : experts) {
            opinions.add(voice.solve(stateObs, elapsedTimer));
        }
        return this.chooseHighestValueAction();
//        return this.chooseDemocratically();
    }

    private Opinion chooseDemocratically() {
        int lastOpinion = -1;
        for (int i = 0; i < opinions.size(); i++) {
            if (i == 0) {
                lastOpinion = opinions.get(i).getAction();
            } else {
                if (opinions.get(i).getAction() == lastOpinion) {
                    return opinions.get(i);
                }
            }
        }
        return null;
    }

    private Opinion chooseHighestValueAction() {
        double bestValue = Double.MIN_VALUE;
        Opinion bestOpinion = null;
        for (Opinion opinion : opinions) {
            if (opinion.getValue() > bestValue) {
                bestValue = opinion.getValue();
                bestOpinion = opinion;
            }
        }
        return bestOpinion;
    }
}
