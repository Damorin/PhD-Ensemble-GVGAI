package Damorin.voices.Yolobot;

import core.game.Observation;
import tools.Vector2d;

import java.util.List;

public abstract class Helper {

	public static int max_itype;

	public static Observation getAgent(List<Observation>[][] grid, YoloState so){

		int block_size = so.getBlockSize();
		Vector2d agentPos = so.getAvatarPosition();
		int agentX = (int)agentPos.x/block_size;
		int agentY = (int)agentPos.y/block_size;
		Observation agent = null;	//Last possible is Player
		int nr = 0;
		if(agentX > 0 && agentY > 0)
			for (Observation obs : grid[agentX][agentY]) {
				if(obs.category == ontology.Types.TYPE_AVATAR){
					if(obs.itype>nr){
						agent = obs;
						nr = obs.itype;
					}
				}
			}
		return agent;
	}
}
