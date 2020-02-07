package concurrentRHEA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParameterManager {

    private List<AgentParameters> params;
    private final int[] counters;
    private boolean firstRun;

    public ParameterManager(int nAgents) {
        params = new ArrayList<>(nAgents);
        counters = new int[nAgents];
        for (int i = 0 ; i < nAgents ; i++) {
            params.add(new AgentParameters());
        }
        firstRun = true;
    }

    public AgentParameters get(int index) {
        return params.get(index);
    }

    public void inc(int index) {
        counters[index]++;
    }

    public void evolve() {
        System.out.println("Evolving ...");
        if (firstRun) {
            firstRun = false;
            return;
        }
        int firstCount = -1;
        int secondCount = -1;
        int firstIndex = -1;
        int secondIndex = -1;
        for (int i = 0 ; i < counters.length ; i++) {
            System.out.println(i + ": " + counters[i]);
            if (counters[i] > firstCount) {
                firstCount = counters[i];
                firstIndex = i;
            } else if (counters[i] > secondCount) {
                secondCount = counters[i];
                secondIndex = i;
            }
            counters[i] = 0;
        }
        System.out.println("First = " + firstIndex + ", Second = " + secondIndex);
        AgentParameters first = params.get(firstIndex);
        AgentParameters second = params.get(secondIndex);
        AgentParameters baby = AgentParameters.combine(first, second);
        AgentParameters firstMutant = first.copy().mutate();
        AgentParameters secondMutant = second.copy().mutate();
        AgentParameters babyMutant = baby.copy().mutate();

        params.clear();
        Collections.addAll(params, first, second, baby, firstMutant, secondMutant, babyMutant);

        for (int i = 6 ; i < counters.length ; i++) {
            params.add(new AgentParameters());
        }

    }
}
