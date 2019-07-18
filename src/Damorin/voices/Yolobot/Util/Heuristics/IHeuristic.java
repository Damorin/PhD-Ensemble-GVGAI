package Damorin.voices.Yolobot.Util.Heuristics;

import Damorin.voices.Yolobot.YoloState;

public abstract class IHeuristic {

	public int Weight;

	public IHeuristic() {
		Weight = 1;
	}
	
	public abstract HeuristicType GetType();
	public abstract double Evaluate(YoloState ys);
	public abstract double EvaluateWithoutNormalisation(YoloState ys);
	public abstract double GetAbsoluteMax();
	public boolean isActive(){
		return true;
	}
}
