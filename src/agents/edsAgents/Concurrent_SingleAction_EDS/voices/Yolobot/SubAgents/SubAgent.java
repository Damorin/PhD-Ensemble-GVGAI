package agents.edsAgents.Concurrent_SingleAction_EDS.voices.Yolobot.SubAgents;

import agents.edsAgents.Concurrent_SingleAction_EDS.voices.Yolobot.YoloState;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.awt.*;

public abstract class SubAgent {
	public SubAgentStatus Status;
	
	public SubAgent() {
		Status = SubAgentStatus.IDLE;
	}
	
	public abstract Types.ACTIONS act(YoloState yoloState, ElapsedCpuTimer elapsedTimer);
	
	public abstract double EvaluateWeight(YoloState yoloState);

	public abstract void preRun(YoloState yoloState, ElapsedCpuTimer elapsedTimer);
	
    public void draw(Graphics2D g){
    }
}
