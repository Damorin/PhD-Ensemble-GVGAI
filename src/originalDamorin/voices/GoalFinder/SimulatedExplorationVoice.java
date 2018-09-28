package originalDamorin.voices.GoalFinder;

import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;
import ontology.Types.ACTIONS;
import originalDamorin.Agent;
import originalDamorin.Opinion;
import originalDamorin.voices.Voice;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

import java.util.ArrayList;
import java.util.List;

/**
 * A Long Range {@link Voice} which plots a path towards a goal. This
 * {@link Voice} simulates the path with a simplified map and does not take
 * evaluations or enemies into account.
 *
 * @author Damien Anderson (Damorin)
 */
public class SimulatedExplorationVoice implements Voice {

    private static final int FIRST_OBSERVATION = 0;

    private StateObservation stateObs;

    private List<Observation> staticsToExplore;
    private List<Observation> staticsExplored;

    private List<Observation>[] immovablePositions;

    private List<ACTIONS> path;

    private Observation goal;

    private Vector2d agentPosition;
    private Vector2d goalPosition;

    private Opinion opinion;

    /**
     * The constructor takes in the current state of the game as a
     * {@link StateObservation}.
     *
     * @param stateObs the state of the game
     */
    public SimulatedExplorationVoice(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        this.stateObs = stateObs;
        this.staticsToExplore = new ArrayList<>();
        this.staticsExplored = new ArrayList<>();
        this.path = new ArrayList<>();
        this.opinion = new Opinion();
    }

    @Override
    public Opinion solve(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        scanForImmovables();
        selectAGoal();
        opinion.setAction(calculateNextStep());
        opinion.setActionValue(this.value(stateObs));
        return opinion;
    }

    private void scanForImmovables() {
        for (Observation observation : getObservations()) {
            if (observation.itype != Types.TYPE_STATIC) {
                if (!staticsExplored.contains(observation)) {
                    if (staticsToExplore.contains(observation)) {
                        staticsToExplore.remove(observation);
                    }
                    staticsToExplore.add(observation);
                }
            }
        }
    }

    private List<Observation> getObservations() {
        List<Observation> list = new ArrayList<Observation>();
        immovablePositions = stateObs.getImmovablePositions();

        if (immovablePositions != null) {
            for (List<Observation> observations : immovablePositions) {
                for (Observation observation : observations) {
                    list.add(observation);
                }
            }
        }
        return list;
    }

    private int calculateNextStep() {
        for (int action = 0; action < Agent.getNumActions(); action++) {
            if (!path.isEmpty()) {
                if (path.get(FIRST_OBSERVATION) == Agent.getActions().get(action)) {
                    path.remove(FIRST_OBSERVATION);
                    return action;
                }
            }
        }
        return 0;
    }

    private void selectAGoal() {

        Observation observation = null;

        if (!staticsToExplore.isEmpty()) {

            if (goal == null) {
                observation = staticsToExplore.remove(FIRST_OBSERVATION);
                goalPosition = observation.position;
                staticsExplored.add(observation);
                goal = observation;
            }
            agentPosition = stateObs.getAvatarPosition();
            if (path.isEmpty() && goal != null) {
                createPathTo(goal);
            }
        }
    }

    private void createPathTo(Observation goal) {
        path = new ArrayList<>();
        double dist = agentPosition.dist(goalPosition);
        while (dist >= this.stateObs.getBlockSize()) {
            if (Math.abs(agentPosition.x - goalPosition.x) > Math
                    .abs(agentPosition.y - goalPosition.y)) {
                path.add(checkHorizontalMovement(goal));
            } else {
                path.add(checkVerticalMovement(goal));
            }
            dist = agentPosition.dist(goalPosition);
        }
    }

    private ACTIONS checkHorizontalMovement(Observation goal) {
        double distanceToGoal = agentPosition.x - goalPosition.x;

        if (distanceToGoal > 0) {
            agentPosition.x -= this.stateObs.getBlockSize();
            return ACTIONS.ACTION_LEFT;
        } else {
            agentPosition.x += this.stateObs.getBlockSize();
            return ACTIONS.ACTION_RIGHT;
        }
    }

    private ACTIONS checkVerticalMovement(Observation goal) {
        double distanceToGoal = agentPosition.y - goalPosition.y;

        if (distanceToGoal > 0) {
            agentPosition.y -= this.stateObs.getBlockSize();
            return ACTIONS.ACTION_UP;
        } else {
            agentPosition.y += this.stateObs.getBlockSize();
            return ACTIONS.ACTION_DOWN;
        }
    }


    public double value(StateObservation a_gameState) {
        a_gameState.advance(Agent.getActions().get(this.opinion.getAction()));

        boolean gameOver = a_gameState.isGameOver();
        Types.WINNER win = a_gameState.getGameWinner();
        double rawScore = a_gameState.getGameScore();

        if (gameOver && win == Types.WINNER.PLAYER_LOSES)
            rawScore += -Double.MAX_VALUE;

        if (gameOver && win == Types.WINNER.PLAYER_WINS)
            rawScore += Double.MAX_VALUE;

        return rawScore;
    }
}
