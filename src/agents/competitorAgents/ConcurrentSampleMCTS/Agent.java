package agents.competitorAgents.ConcurrentSampleMCTS;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is an implementation of MCTS UCT
 */
public class Agent extends AbstractPlayer {

    public int num_actions;
    public Types.ACTIONS[] actions;
    protected List<SingleMCTSPlayer> mctsPlayers;
    private StateObservation mainRoot;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Public constructor with state observation and time due.
     *
     * @param so           state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer) {
        //Get the actions in a static array.
        ArrayList<Types.ACTIONS> act = so.getAvailableActions();
        mctsPlayers = new ArrayList<>();
        actions = new Types.ACTIONS[act.size()];
        for (int i = 0; i < actions.length; ++i) {
            actions[i] = act.get(i);
            mctsPlayers.add(i, getPlayer(so, elapsedTimer, i));
        }
        num_actions = actions.length;
    }

    public SingleMCTSPlayer getPlayer(StateObservation so, ElapsedCpuTimer elapsedTimer, int actionToTake) {
        return new SingleMCTSPlayer(new Random(), num_actions, actions, actionToTake);
    }


    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     *
     * @param stateObs     Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        //Set the state observation object as the new root of the tree.
        mainRoot = stateObs;
        List<Future<Double>> futures = new ArrayList<>();
        for (SingleMCTSPlayer mctsPlayer : mctsPlayers) {
            mctsPlayer.init(stateObs, elapsedTimer);
            Future<Double> future = executorService.submit(mctsPlayer);
            futures.add(future);
        }

        int action = 0;
        double actionValue = -Double.MAX_VALUE;
        //Determine the action using MCTS...
        for (int i = 0; i < futures.size(); i++) {
            try {
                if (futures.get(i).get() > actionValue) {
                    action = i;
                    actionValue = futures.get(i).get();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }

        //... and return it.
        return actions[action];
    }

}
