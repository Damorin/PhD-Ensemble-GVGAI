package EDS_AllActions.voices;

import ontology.Types;

public class Opinion {

    private Types.ACTIONS action;
    private double value;

    public Opinion(Types.ACTIONS action, double value) {
        this.action = action;
        this.value = value;
    }

    public Types.ACTIONS getAction() {
        return action;
    }

    public double getValue() {
        return value;
    }
}
