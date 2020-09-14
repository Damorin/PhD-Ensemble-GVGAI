package concurrentMCTS;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConcurrentAgent extends AbstractPlayer {

    private static final double EPSILON = 1.0E-6;

    private final Agent[] agents = new Agent[10];
    private final ExecutorService exec = Executors.newCachedThreadPool();

    private final Random rng = new Random();

    public ConcurrentAgent (StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
       for (int i = 0 ; i < agents.length ; i++) {
           agents[i] = new Agent(stateObs, elapsedTimer);
       }
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        List<Types.ACTIONS> actions = stateObs.getAvailableActions();
        List<Future<Types.ACTIONS>> futures = new LinkedList<>();
        Map<Types.ACTIONS, Agent> actionMap = new HashMap<>();

        for (int i = 0 ; i < actions.size() ; i++) {
            StateObservation copy = stateObs.copy();
            copy.advance(actions.get(i));
            final int agent = i;
            futures.add(exec.submit(() -> agents[agent].act(copy, elapsedTimer.copy())));
            actionMap.put(actions.get(i), agents[agent]);
        }

        for(Future<Types.ACTIONS> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        int i = 0;
        double best = -100000000;
        Agent bestAgent = null;
        Types.ACTIONS bestAction = Types.ACTIONS.ACTION_NIL;

        for(Types.ACTIONS action : actionMap.keySet()) {
            Agent agent = actionMap.get(action);
            double eval = agent.mctsPlayer.getBestChildValue();
//            System.out.println(i + ": " + eval);
            eval += Math.random() * EPSILON;
            if (eval > best) {
                best = eval;
                bestAgent = agent;
                bestAction = action;
            }
            i++;
        }
        return bestAction;
    }
}
