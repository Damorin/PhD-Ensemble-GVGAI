package agents.edsAgents.EDS_R_RHEAMCTS.voices.search.standardMCTS;

import agents.edsAgents.EDS_R_RHEAMCTS.voices.Opinion;
import agents.edsAgents.EDS_R_RHEAMCTS.voices.Voice;
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is an implementation of search UCT
 */
public class MCTSVoice implements Voice {

    public int num_actions;
    public Types.ACTIONS[] actions;

    protected SingleMCTSPlayer mctsPlayer;

    /**
     * Public constructor with state observation and time due.
     * @param so state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public MCTSVoice(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        //Get the actions in a static array.
        ArrayList<Types.ACTIONS> act = so.getAvailableActions();
        actions = new Types.ACTIONS[act.size()];
        for(int i = 0; i < actions.length; ++i)
        {
            actions[i] = act.get(i);
        }
        num_actions = actions.length;

        //Create the player.

        mctsPlayer = getPlayer(so, elapsedTimer);
    }

    public SingleMCTSPlayer getPlayer(StateObservation so, ElapsedCpuTimer elapsedTimer) {
        return new SingleMCTSPlayer(new Random(), num_actions, actions);
    }


    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Opinion act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        //Set the state observation object as the new root of the tree.
        mctsPlayer.init(stateObs);

        //Determine the action using search...
        int action = mctsPlayer.run(elapsedTimer);

        //... and return it.
        return new Opinion(actions[action]);
    }

    @Override
    public Opinion askOpinion(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        return this.act(stateObs, elapsedTimer);
    }
}
