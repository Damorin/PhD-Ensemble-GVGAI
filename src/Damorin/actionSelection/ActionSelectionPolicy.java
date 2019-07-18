package Damorin.actionSelection;

import Damorin.voices.Opinion;
import ontology.Types;

import java.util.List;

public interface ActionSelectionPolicy {

    Types.ACTIONS selectAction(List<Opinion> opinions);

}
