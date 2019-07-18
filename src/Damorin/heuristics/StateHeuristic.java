package Damorin.heuristics;

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

    abstract public double evaluateState(StateObservation stateObs);

    abstract public void updateHeuristicBasedOnCurrentState(StateObservation stateObs);

    abstract public void recordDataOnFile(Game played, String fileName, int randomSeed, int[] recordIds);

    abstract public void drawInScreen(Graphics2D g);

    public void initHeuristicAccumulation() {
        heuristic_acc = 0;
        last_stateObs = last_visited_stateObs;
    }

    public void accumulateHeuristic(StateObservation stateObs) {
        heuristic_acc += evaluateState(stateObs);
        if (stateObs != null) {
            last_stateObs = stateObs.copy();
        }
//        System.out.println("Heuristic acc: " + heuristic_acc);
    }

    public double endHeuristicAccumulation(StateObservation stateObs) {
        double h = heuristic_acc;
        initHeuristicAccumulation();
//        System.out.println("Total heuristic: " + h);
        return h;
    }
}
