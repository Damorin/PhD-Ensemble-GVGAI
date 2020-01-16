package agents.edsAgents.EDS_R_MCTSPair.voices;

import ontology.Types;

public class Opinion {

    private Types.ACTIONS action;
    private double value;

    public Opinion(Types.ACTIONS action) {
        this.action = action;
    }

    public Types.ACTIONS getAction() {
//        System.out.println("Action selected: " + this.action);
        return this.action;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
