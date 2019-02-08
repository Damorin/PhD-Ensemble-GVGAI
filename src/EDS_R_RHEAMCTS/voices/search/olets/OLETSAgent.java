package EDS_R_RHEAMCTS.voices.search.olets;

import EDS_R_MCTSPair.voices.Opinion;
import EDS_R_MCTSPair.voices.Voice;
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.Random;

/**
 * Code written by Adrien Couetoux, acouetoux@ulg.ac.be.
 * Date: 15/12/2015
 * @author Adrien Couëtoux
 */

public class OLETSAgent implements Voice {

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
    public SingleMCTSPlayer mctsPlayer;

    /**
     * Public constructor with state observation and time due.
     *
     * @param so           state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public OLETSAgent(StateObservation so, ElapsedCpuTimer elapsedTimer) {
        //Get the actions in a static array.
        ArrayList<Types.ACTIONS> act = so.getAvailableActions();
        actions = new Types.ACTIONS[act.size()];
        for (int i = 0; i < actions.length; ++i) {
            actions[i] = act.get(i);
        }
        NUM_ACTIONS = actions.length;

        //Create the player.
        mctsPlayer = new SingleMCTSPlayer(new Random(), this);
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
        mctsPlayer.init(stateObs);

        //Determine the action using search...
        int action = mctsPlayer.run(elapsedTimer);

        //... and return it.
        return actions[action];
    }

    @Override
    public Opinion askOpinion(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        return new Opinion(this.act(stateObs, elapsedTimer));
    }
}
