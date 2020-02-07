package concurrentRHEA;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConcurrentAgent extends AbstractPlayer {

    private static final int N_AGENTS = 8;
    private static final double EPSILON = 1.0E-6;

    private final ExecutorService exec = Executors.newFixedThreadPool(N_AGENTS);
    private final List<Agent> agents;

    // Dirty hack, if you ask me.  Static variable data?  Who wrote this shit?  Ah.
    // Each new round creates a new instance of the Player.  Using static here
    // allows the Agent to 'remember' the evolved parameters.  Serialisation is
    // probably a much better option.
    private static final ParameterManager PARAMS = new ParameterManager(N_AGENTS);

    private final Random rng = new Random();

    public ConcurrentAgent (StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        agents = new LinkedList<>();
        PARAMS.evolve();
        for (int i = 0; i < N_AGENTS; i++) {
            agents.add(new Agent(stateObs, elapsedTimer, PARAMS.get(i)));
        }
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        List<Future<Types.ACTIONS>> futures = new LinkedList<>();
        for (Agent agent : agents) {
            futures.add(exec.submit(() -> agent.act(stateObs, elapsedTimer)));
        }

        for(Future<Types.ACTIONS> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        int i = 0;
        int bestIndex = -1;
        double best = -1;
        Agent bestAgent = null;
        for(Agent agent : agents) {
            double eval = agent.getBestEvaluation() + Math.random() * EPSILON;
            if (eval > best) {
                best = eval;
                bestAgent = agent;
                bestIndex = i;
            }
            i++;
        }

//        System.out.println("Best agent = " + bestIndex);
        if (bestAgent == null) {
            int index = rng.nextInt(agents.size());
            bestIndex = index;
            bestAgent = agents.get(index);
        }

        PARAMS.inc(bestIndex);
        Types.ACTIONS bestAction = bestAgent.getBestAction();
//        System.out.println("Best action = " + bestAction);
        return bestAction;
    }
}
