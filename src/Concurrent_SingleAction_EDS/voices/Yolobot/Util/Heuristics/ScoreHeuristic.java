package Concurrent_SingleAction_EDS.voices.Yolobot.Util.Heuristics;

import Concurrent_SingleAction_EDS.voices.Yolobot.Util.Wissensdatenbank.YoloKnowledge;
import Concurrent_SingleAction_EDS.voices.Yolobot.YoloState;

public class ScoreHeuristic extends IHeuristic {

	public static double max = Double.MIN_VALUE;
	
	@Override
	public double Evaluate(YoloState ys) {
		return EvaluateWithoutNormalisation(ys)/max;

	}

	@Override
	public HeuristicType GetType() {
		return HeuristicType.ScoreHeuristic;
	}

	@Override
	public double EvaluateWithoutNormalisation(YoloState ys) {
		if(ys != null) {
			double deltaScore = ys.getGameScore() - YoloState.currentGameScore;
			
			if(deltaScore<0 && !YoloKnowledge.instance.isMinusScoreBad())
				deltaScore = 0;
			
			if(max < Math.abs(deltaScore)) {
				max = Math.abs(deltaScore);				
			}
			
			return deltaScore;
		}
		return 0;
	}

	@Override
	public double GetAbsoluteMax() {
		return max;
	}
}
