package agents.edsAgents.Concurrent_All_Actions_EDS.actionSelection;

import agents.edsAgents.Concurrent_All_Actions_EDS.voices.Opinion;
import ontology.Types;

import java.util.List;

public class HighestValueActionSelectionPolicy implements ActionSelectionPolicy {
    @Override
    public Types.ACTIONS selectAction(List<Opinion> opinions) {
//        displayOpinions(opinions);
        Opinion best = new Opinion(Types.ACTIONS.ACTION_NIL, -Double.MAX_VALUE);

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
