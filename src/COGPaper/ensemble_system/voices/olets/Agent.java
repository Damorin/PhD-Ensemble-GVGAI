package COGPaper.ensemble_system.voices.olets;

import COGPaper.controllers.AbstractHeuristicPlayer;
import COGPaper.ensemble_system.voices.Opinion;
import COGPaper.ensemble_system.voices.Voice;
import COGPaper.heuristics.StateHeuristic;
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.Random;

/**
 * Code written by Adrien Couetoux, acouetoux@ulg.ac.be.
 * Date: 15/12/2015
 *
 * @author Adrien Couëtoux
 */

public class Agent extends AbstractHeuristicPlayer implements Voice {

    /**
     * Number of feasible actions (usually from 2 to 5)
     */
    public int NUM_ACTIONS;
    /**
     * Feasible actions array, of length NUM_ACTIONS
     */
    public Types.ACTIONS[] actions;
    /**
     * The Monte Carlo Tree Search agent - the core of the algorithm
     */
    private final SingleMCTSPlayer mctsPlayer;

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
        NUM_ACTIONS = actions.length;

        //Create the player.
        mctsPlayer = new SingleMCTSPlayer(new Random(), this);
    }

    /**
     * Returns the heuristic assigned to the agent
     *
     * @return The heuristic assigned to the agent
     */
    public StateHeuristic getAgentHeuristic() {
        return heuristic;
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

        // The heuristic is updated if needed
        heuristic.updateHeuristicBasedOnCurrentState(stateObs);

        //Set the state observation object as the new root of the tree.
        mctsPlayer.init(stateObs);

        //Determine the action using MCTS...
        int action = mctsPlayer.run(elapsedTimer);

        //... and return it.
        return actions[action];
    }

    @Override
    public Opinion askOpinion(StateObservation stateObs, ElapsedCpuTimer elapsedTimer, int analysisTime) {
        return new Opinion(this.act(stateObs, elapsedTimer), mctsPlayer.value);
    }
}
