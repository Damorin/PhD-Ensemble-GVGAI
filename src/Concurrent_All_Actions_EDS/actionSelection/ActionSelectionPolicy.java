package Concurrent_All_Actions_EDS.actionSelection;

import Concurrent_All_Actions_EDS.voices.Opinion;
import ontology.Types;

import java.util.List;

public interface ActionSelectionPolicy {

    Types.ACTIONS selectAction(List<Opinion> opinions);

}
