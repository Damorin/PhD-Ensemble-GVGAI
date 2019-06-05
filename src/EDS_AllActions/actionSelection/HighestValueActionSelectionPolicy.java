package EDS_AllActions.actionSelection;

import EDS_AllActions.voices.Opinion;
import ontology.Types;

import java.util.ArrayList;
import java.util.List;

public class HighestValueActionSelectionPolicy implements ActionSelectionPolicy {
    @Override
    public Types.ACTIONS selectAction(List<Opinion> opinions) {
//        displayOpinions(opinions);
        Opinion best = new Opinion(Types.ACTIONS.ACTION_NIL, Double.MIN_VALUE);

        for (Opinion opinion : opinions) {
            if (opinion.getValue() > best.getValue()) {
                best = opinion;
            }
        }
        return best.getAction();
    }

    private void displayOpinions(List<Opinion> opinions) {
        for(Opinion opinion : opinions) {
            System.out.println(opinion.getAction() + " " + opinion.getValue());
        }
    }
}
