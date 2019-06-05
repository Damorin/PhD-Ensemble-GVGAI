package EDS_AllActions.actionSelection;

import EDS_AllActions.voices.Opinion;
import ontology.Types;

import java.util.List;

public interface ActionSelectionPolicy {

    Types.ACTIONS selectAction(List<Opinion> opinions);

}
