package originalDamorin.voices.sampleMCTS;

import core.game.StateObservation;
import originalDamorin.Agent;
import originalDamorin.Opinion;
import originalDamorin.voices.Voice;
import tools.ElapsedCpuTimer;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is an implementation of search UCT
 */
public class MCTSVoice implements Voice {

    private Opinion opinion;

    protected SingleMCTSPlayer mctsPlayer;

    /**
     * Public constructor with state observation and time due.
     *
     * @param so           state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public MCTSVoice(StateObservation so, ElapsedCpuTimer elapsedTimer) {
        //Create the player.
        mctsPlayer = getPlayer(so, elapsedTimer);

        //Create blank Opinion
        opinion = new Opinion();
    }

    public SingleMCTSPlayer getPlayer(StateObservation so, ElapsedCpuTimer elapsedTimer) {
        return new SingleMCTSPlayer(new Random(), Agent.getNumActions(), Agent.getActions());
    }


    /**
     * Picks an action. This function is called every game step to request an
     * action from the player.
     *
     * @param stateObs     Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    @Override
    public Opinion solve(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        //Set the state observation object as the new root of the tree.
        mctsPlayer.init(stateObs);

        //Determine the action
        opinion.setAction(mctsPlayer.run(elapsedTimer));

        //Determine the value of the action
        opinion.setActionValue(mctsPlayer.getActionValue());

        //... and return it.
        return opinion;
    }

}
