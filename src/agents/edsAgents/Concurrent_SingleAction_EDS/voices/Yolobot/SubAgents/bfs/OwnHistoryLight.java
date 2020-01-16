package agents.edsAgents.Concurrent_SingleAction_EDS.voices.Yolobot.SubAgents.bfs;

import agents.edsAgents.Concurrent_SingleAction_EDS.voices.Yolobot.YoloState;
import ontology.Types.ACTIONS;

import java.util.LinkedList;

public class OwnHistoryLight {

	public YoloState state;
	public LinkedList<ACTIONS> actions;
	int tick;
	int score;
	public int timeSinceAvatarChange;
	
	
	public OwnHistoryLight(OwnHistoryLight before, ACTIONS newAction){
		state = before.state.copyAdvanceLearn(newAction);
		actions = new LinkedList<ACTIONS>(before.actions);
		actions.add(newAction);
		
		if(before.state.getAvatar() != null && state.getAvatar() != null && state.getAvatar().itype == before.state.getAvatar().itype){
			timeSinceAvatarChange = before.timeSinceAvatarChange + 1;
		}else{
			timeSinceAvatarChange = 0;
		}
		
		tick = state.getGameTick();
		score = (int) state.getGameScore();
	}

	public OwnHistoryLight(YoloState so) {
		timeSinceAvatarChange = 0;
		actions = new LinkedList<ACTIONS>();
		state = so;
	}

	public boolean toPrune() {
		return false;
	}
	
	@Override
	public String toString() {
		return "Tiefe: " + actions.size();
	}
	

	public double getPriority(){
		return 0.1 * tick - score;
	}
}