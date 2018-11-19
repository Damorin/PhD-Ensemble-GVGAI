package Damorin2;

/**
 * Created by Damien Anderson on 22/02/2018.
 */
public class Opinion {

    private int action;
    private double value;

    public void setAction(int action) {
        this.action = action;
    }

    public void setActionValue(double actionValue) {
        this.value = actionValue;
    }

    public int getAction() {
        return this.action;
    }

    public double getValue() {
        return this.value;
    }
}
