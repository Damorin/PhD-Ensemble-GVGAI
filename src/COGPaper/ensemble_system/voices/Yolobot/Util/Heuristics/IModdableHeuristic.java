package COGPaper.ensemble_system.voices.Yolobot.Util.Heuristics;

import COGPaper.ensemble_system.voices.Yolobot.YoloState;
import tools.Vector2d;

public abstract class IModdableHeuristic extends IHeuristic {
	protected boolean targetIsToUse;
	
	public abstract double getModdedHeuristic(YoloState state, int trueX, int trueY, Vector2d avatarOrientation);
	
	public void setTargetIsToUse(boolean value){
		targetIsToUse = value;
	}
	public boolean getZargetIsToUse(){
		return targetIsToUse;
	}

	public abstract boolean canStepOn(int myX, int myY);
}
