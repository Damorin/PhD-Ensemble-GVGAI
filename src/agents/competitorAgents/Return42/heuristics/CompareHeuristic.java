package agents.competitorAgents.Return42.heuristics;

import agents.competitorAgents.Return42.GameStateCache;
import agents.competitorAgents.Return42.heuristics.features.CompareFeature;
import agents.competitorAgents.Return42.heuristics.features.controller.FeatureController;

import java.util.List;

public interface CompareHeuristic {
    public double evaluate(GameStateCache newState, GameStateCache oldState);

    public List<CompareFeature> getFeatures();

    public List<FeatureController> getController();
}
