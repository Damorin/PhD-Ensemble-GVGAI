package EDS_AllActions.voices.mcts;

import COGPaper.controllers.AbstractHeuristicPlayer;
import EDS_AllActions.voices.Opinion;
import EDS_AllActions.voices.Voice;
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

    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     *
     * @param stateObs     Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @param analysisTime
     * @return An action for the current state
     */
    public List<Opinion> askOpinion(StateObservation stateObs, ElapsedCpuTimer elapsedTimer, int analysisTime) {
        return this.performAnalysis(stateObs, elapsedTimer);

    }

    @Override
    public List<Opinion> performAnalysis(StateObservation stateObs, ElapsedCpuTimer elapsedCpuTimer) {

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
}
