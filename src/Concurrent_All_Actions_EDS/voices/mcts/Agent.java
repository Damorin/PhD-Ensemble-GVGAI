package Concurrent_All_Actions_EDS.voices.mcts;

import COGPaper.controllers.AbstractHeuristicPlayer;
import Concurrent_All_Actions_EDS.voices.Opinion;
import Concurrent_All_Actions_EDS.voices.Voice;
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Agent extends AbstractHeuristicPlayer implements Voice {

    public int num_actions;
    public Types.ACTIONS[] actions;

    protected SingleMCTSPlayer mctsPlayer;
    private StateObservation stateObs;
    private ElapsedCpuTimer elapsedCpuTimer;

    /**
     * Public constructor with state observation and time due.
     *
     * @param stateObs     state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer, String heuristicName) {
        super(stateObs, heuristicName);

        //Get the actions in a static array.
        ArrayList<Types.ACTIONS> act = stateObs.getAvailableActions();
        actions = new Types.ACTIONS[act.size()];
        for (int i = 0; i < actions.length; ++i) {
            actions[i] = act.get(i);
        }
        num_actions = actions.length;

        //Create the player.
        mctsPlayer = getPlayer(stateObs, elapsedTimer);
    }

    public SingleMCTSPlayer getPlayer(StateObservation so, ElapsedCpuTimer elapsedTimer) {
        return new SingleMCTSPlayer(new Random(), num_actions, actions, heuristic);
    }

    @Override
    public List<Opinion> performAnalysis() {

        // The heuristic is updated if needed
        heuristic.updateHeuristicBasedOnCurrentState(stateObs);

        //Set the state observation object as the new root of the tree.
        mctsPlayer.init(stateObs);

        //Determine the action using MCTS...
        List<Opinion> opinions = mctsPlayer.run(elapsedCpuTimer);
        //... and return it.
        return opinions;
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        return null;
    }

    @Override
    public void initializeAnalysis(StateObservation stateObs, ElapsedCpuTimer elapsedCpuTimer) {
        this.stateObs = stateObs;
        this.elapsedCpuTimer = elapsedCpuTimer;

    }

    @Override
    public List<Opinion> call() throws Exception {
        return null;
    }
}
