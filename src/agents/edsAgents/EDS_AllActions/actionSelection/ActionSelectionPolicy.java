package agents.edsAgents.EDS_AllActions.actionSelection;

import agents.edsAgents.EDS_AllActions.voices.Opinion;
import ontology.Types;

import java.util.List;

public interface ActionSelectionPolicy {

    Types.ACTIONS selectAction(List<Opinion> opinions);

}
