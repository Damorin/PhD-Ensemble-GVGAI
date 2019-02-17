package COGPaper.ensemble_system.voices;

import ontology.Types;

/**
 * The recommended action from a {@link Voice}.
 * <p>
 * Created by Damorin on 12/05/2017.
 */
public class Opinion {

    private Types.ACTIONS action;
    private double estimatedValue;

    public Opinion(Types.ACTIONS action, double estimatedValue) {
        this.action = action;
        this.estimatedValue = estimatedValue;
    }

    public Types.ACTIONS getAction() {
        return this.action;
    }

    public double getEstimatedValue() {
        return this.estimatedValue;
    }

}
