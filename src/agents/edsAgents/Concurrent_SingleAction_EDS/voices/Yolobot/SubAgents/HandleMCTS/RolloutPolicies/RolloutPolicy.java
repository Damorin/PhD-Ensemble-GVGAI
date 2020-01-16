package agents.edsAgents.Concurrent_SingleAction_EDS.voices.Yolobot.SubAgents.HandleMCTS.RolloutPolicies;

import agents.edsAgents.Concurrent_SingleAction_EDS.voices.Yolobot.YoloState;
import ontology.Types.ACTIONS;

import java.util.ArrayList;

public abstract class RolloutPolicy {

	public ACTIONS nextAction(YoloState state, ArrayList<ACTIONS> forbiddenActions) {
		return nextAction(state, forbiddenActions, false);
	}
	public ACTIONS nextAction(YoloState state, ArrayList<ACTIONS> forbiddenActions, boolean forceNotEpsilon){
		ArrayList<ACTIONS> validActions = possibleNextActions(state, forbiddenActions, forceNotEpsilon);
		validActions = possibleNextActions(state, forbiddenActions, true);
		return validActions.get((int)(validActions.size()*Math.random()));
	}

	public abstract ArrayList<ACTIONS> possibleNextActions(YoloState state, ArrayList<ACTIONS> forbiddenAction, boolean forceNotEpsilon);

}
