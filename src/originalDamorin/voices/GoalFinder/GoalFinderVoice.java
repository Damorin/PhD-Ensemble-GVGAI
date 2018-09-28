package originalDamorin.voices.GoalFinder;

import core.game.StateObservation;
import originalDamorin.Opinion;
import originalDamorin.voices.Voice;
import tools.ElapsedCpuTimer;
import tools.pathfinder.AStar;
import tools.pathfinder.PathFinder;

import java.util.ArrayList;

/**
 * Created by Damien Anderson on 02/03/2018.
 */
public class GoalFinderVoice implements Voice {

    private AStar pathFinder;

    public GoalFinderVoice(StateObservation so, ElapsedCpuTimer elapsedCpuTimer) {
        ArrayList<Integer> iTypes = new ArrayList<>();
        this.pathFinder = new AStar(new PathFinder(iTypes));
    }

    @Override
    public Opinion solve(StateObservation so, ElapsedCpuTimer elapsedTimer) {
        return null;
    }
}
