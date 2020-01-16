package agents.edsAgents.Concurrent_SingleAction_EDS.voices.Yolobot.SubAgents.HandleMCTS.RolloutPolicies;

import agents.edsAgents.Concurrent_SingleAction_EDS.voices.Yolobot.YoloState;
import ontology.Types.ACTIONS;

import java.util.ArrayList;

public class RandomNotDeadRolloutPolicy extends RolloutPolicy {

	RandomRolloutPolicy randomPolicy;
	double epsilon = 0.9;
	
	public RandomNotDeadRolloutPolicy() {
		randomPolicy = new RandomRolloutPolicy();
	}
	

	@Override
	public ArrayList<ACTIONS> possibleNextActions(YoloState state,
			ArrayList<ACTIONS> forbiddenAction, boolean forceNotEpsilon) {
		
		ArrayList<ACTIONS> validActions = randomPolicy.possibleNextActions(state, forbiddenAction, forceNotEpsilon);
		
		if(forbiddenAction != null)
			validActions.removeAll(forbiddenAction);
		
		if(validActions.isEmpty() || (!forceNotEpsilon && Math.random()>epsilon)){
			//If no action seems valid or Random:	Choose from all
			return  randomPolicy.possibleNextActions(state, forbiddenAction, forceNotEpsilon);
		}else{
			return validActions;
		}
		
	}
}
