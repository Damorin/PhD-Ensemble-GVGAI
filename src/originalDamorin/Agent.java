package originalDamorin;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import originalDamorin.arbiter.UninformedEnsembleDecisionSystem;
import tools.ElapsedCpuTimer;

import java.util.List;

/**
 * Created by Damien Anderson on 20/02/2018.
 */
public class Agent extends AbstractPlayer {

    private UninformedEnsembleDecisionSystem eds;
    private static List<Types.ACTIONS> actions;
    private static int numActions = 0;

    public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer) {
        actions = so.getAvailableActions();
        numActions = actions.size();
        eds = new UninformedEnsembleDecisionSystem(so, elapsedTimer);
    }

    public static int getNumActions() {
        return numActions;
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        return actions.get(eds.solve(stateObs, elapsedTimer).getAction());
    }

    public static List<Types.ACTIONS> getActions() {
        return actions;
    }
}
