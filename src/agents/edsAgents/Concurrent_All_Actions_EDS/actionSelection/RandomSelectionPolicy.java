package agents.edsAgents.Concurrent_All_Actions_EDS.actionSelection;

import agents.edsAgents.Concurrent_All_Actions_EDS.voices.Opinion;
import ontology.Types;

import java.util.List;
import java.util.Random;

public class RandomSelectionPolicy implements ActionSelectionPolicy {
    @Override
    public Types.ACTIONS selectAction(List<Opinion> opinions) {
        displayOpinions(opinions);
        return opinions.get(new Random().nextInt(opinions.size())).getAction();
    }

    private void displayOpinions(List<Opinion> opinions) {
        for(Opinion opinion : opinions) {
            System.out.println(opinion.getAction() + " " + opinion.getValue());
        }
    }

}
