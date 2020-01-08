package COGPaper.heuristics;

import core.game.Game;
import core.game.StateObservation;

import java.awt.*;
import java.io.BufferedWriter;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 11/02/14
 * Time: 15:43
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public abstract class StateHeuristic {

    protected static final double HUGE_NEGATIVE = -10000.0;
    protected static final double LESS_HUGE_NEGATIVE = -5000.0;
    protected static final double HUGE_POSITIVE = 10000.0;
    protected BufferedWriter writer;
    protected double heuristic_acc;
    protected StateObservation last_stateObs;
    protected StateObservation last_visited_stateObs;

    private static final boolean NORMALIZE_HEURISTIC = true;

    private int heuristic_acc_counter = 0;
    private double heuristic_max = 0;
    private double heuristic_min = 0;
    private boolean extreme_positive = false;
    private boolean extreme_negative = false;

    abstract public double evaluateState(StateObservation stateObs);

    abstract public void updateHeuristicBasedOnCurrentState(StateObservation stateObs);

    abstract public void recordDataOnFile(Game played, String fileName, int randomSeed, int[] recordIds);

    abstract public void drawInScreen(Graphics2D g);

    public void initHeuristicAccumulation() {
        heuristic_acc = 0;
        heuristic_acc_counter = 0;
        extreme_positive = false;
        extreme_negative = false;
        last_stateObs = last_visited_stateObs;
    }

    public void accumulateHeuristic(StateObservation stateObs) {
        double heuristic_value = evaluateState(stateObs);

        if (!Double.isNaN(heuristic_value)) {
            if ((Double.compare(heuristic_value, HUGE_POSITIVE) != 0) && (Double.compare(heuristic_value, heuristic_max) > 0)) {
                heuristic_max = heuristic_value;
            }
            if ((Double.compare(heuristic_value, HUGE_NEGATIVE) != 0) && (Double.compare(heuristic_value, heuristic_min) < 0)) {
                heuristic_min = heuristic_value;
            }

            if (Double.compare(heuristic_value, HUGE_POSITIVE) == 0) {
                extreme_positive = true;
            } else if (Double.compare(heuristic_value, HUGE_NEGATIVE) == 0) {
                extreme_negative = true;
            }

            heuristic_acc += heuristic_value;
            heuristic_acc_counter += 1;
        }

        if (stateObs != null) {
            last_stateObs = stateObs.copy();
        }
        
//        System.out.println(heuristic_acc_counter + " Heuristic acc: " + heuristic_acc);
    }

    public double endHeuristicAccumulation(StateObservation stateObs) {
        double h = heuristic_acc;
        double h_norm;

        if (extreme_negative){
            h_norm = 0;
        } else if (extreme_positive){
            h_norm = 1;
        } else if ((heuristic_max != 0)||(heuristic_min != 0)){
            h_norm = (heuristic_acc - heuristic_min * heuristic_acc_counter) / (heuristic_acc_counter * (heuristic_max - heuristic_min));
        } else {
            h_norm = h;
        }

        initHeuristicAccumulation();

//        System.out.println("Heuristic: "+h);
//        System.out.println("Max: "+heuristic_max+"Min: "+heuristic_min+" Heuristic normz: "+h_norm);

        if (NORMALIZE_HEURISTIC) {
            return h_norm;
        }

        return h;
    }
}