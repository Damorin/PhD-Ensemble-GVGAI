package agents.competitorAgents.Return42.heuristics.patterns;

import agents.competitorAgents.Return42.GameStateCache;
import ontology.Types;

public interface Pattern {
    public boolean appliesToGame(GameStateCache state);
    public double applies(GameStateCache state);
    public Types.ACTIONS getAction();
}
