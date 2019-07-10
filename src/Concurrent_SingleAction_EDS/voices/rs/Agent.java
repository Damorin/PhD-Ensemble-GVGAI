package Concurrent_SingleAction_EDS.voices.rs;

import COGPaper.controllers.AbstractHeuristicPlayer;
import COGPaper.heuristics.StateHeuristic;
import Concurrent_SingleAction_EDS.voices.Opinion;
import Concurrent_SingleAction_EDS.voices.Voice;
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.*;

public class Agent extends AbstractHeuristicPlayer implements Voice {

    // variable
    private int SIMULATION_DEPTH = 10;
    private double DISCOUNT = 1; //0.99;

    // constants
    private final long BREAK_MS = 10;
    public static final double epsilon = 1e-6;
    static final int POINT1_CROSS = 0;
    static final int UNIFORM_CROSS = 1;

    private ArrayList<Individual> population;
    private int NUM_INDIVIDUALS;
    private HashMap<Integer, Types.ACTIONS> action_mapping;
    private int N_ACTIONS;

    private ElapsedCpuTimer timer;
    private Random randomGenerator;

    private double acumTimeTakenEval = 0, avgTimeTakenEval = 0;
    private int numEvals = 0;
    private long remaining;
    private double actionValue;
    private double heuristicValue;
    private StateObservation stateObs;
    private ElapsedCpuTimer elapsedCpuTimer;

    /**
     * Public constructor with state observation and time due.
     *
     * @param stateObs     state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer, String heuristicName) {
        super(stateObs, heuristicName);
        randomGenerator = new Random();
        this.timer = elapsedTimer;

        // INITIALISE POPULATION
        init_pop(stateObs);
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        this.timer = elapsedTimer;
        numEvals = 0;
        acumTimeTakenEval = 0;
        remaining = timer.remainingTimeMillis();
        NUM_INDIVIDUALS = 0;

        // The heuristic is updated if needed
        heuristic.updateHeuristicBasedOnCurrentState(stateObs);

        // INITIALISE POPULATION
        init_pop(stateObs);

        // RETURN ACTION
        Types.ACTIONS best = get_best_action(population);
        return best;
    }


    /**
     * Evaluates an individual by rolling the current state with the actions in the individual
     * and returning the value of the resulting state; random action chosen for the opponent
     *
     * @param individual - individual to be valued
     * @param heuristic  - heuristic to be used for state evaluation
     * @param state      - current state, root of rollouts
     * @return - value of last state reached
     */
    private double evaluate(Individual individual, StateHeuristic heuristic, StateObservation state) {

        ElapsedCpuTimer elapsedTimerIterationEval = new ElapsedCpuTimer();

        StateObservation st = state.copy();
        int i;
        for (i = 0; i < SIMULATION_DEPTH; i++) {
            double acum = 0, avg;
            if (!st.isGameOver()) {
                ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
                advanceState(st, action_mapping.get(individual.actions[i]));
                acum += elapsedTimerIteration.elapsedMillis();
                avg = acum / (i + 1);
                remaining = timer.remainingTimeMillis();
                if (remaining < 2 * avg || remaining < BREAK_MS) break;
            } else {
                break;
            }
        }

        StateObservation first = st.copy();
        double value = heuristic.endHeuristicAccumulation(first);

        // Apply discount factor
        value *= Math.pow(DISCOUNT, i);

        individual.value = value;

        numEvals++;
        acumTimeTakenEval += (elapsedTimerIterationEval.elapsedMillis());
        avgTimeTakenEval = acumTimeTakenEval / numEvals;
        remaining = timer.remainingTimeMillis();
        heuristicValue = value;
        return value;
    }


    /**
     * Insert a new individual into the population at the specified position by replacing the old one.
     *
     * @param newind   - individual to be inserted into population
     * @param pop      - population
     * @param idx      - position where individual should be inserted
     * @param stateObs - current game state
     */
    private void add_individual(Individual newind, Individual[] pop, int idx, StateObservation stateObs) {
        evaluate(newind, heuristic, stateObs);
        pop[idx] = newind.copy();
    }

    /**
     * Initialize population
     *
     * @param stateObs - current game state
     */
    private void init_pop(StateObservation stateObs) {

        double remaining = timer.remainingTimeMillis();

        N_ACTIONS = stateObs.getAvailableActions().size();
        action_mapping = new HashMap<>();
        int k = 0;
        for (Types.ACTIONS action : stateObs.getAvailableActions()) {
            action_mapping.put(k, action);
            k++;
        }

        NUM_INDIVIDUALS = 0;

        population = new ArrayList<>();
        do {
            Individual newInd = new Individual(SIMULATION_DEPTH, N_ACTIONS, randomGenerator);
            evaluate(newInd, heuristic, stateObs);
            population.add(newInd);
            remaining = timer.remainingTimeMillis();
            NUM_INDIVIDUALS++;

        } while (remaining > avgTimeTakenEval && remaining > BREAK_MS);

        if (NUM_INDIVIDUALS > 1)
            Collections.sort(population, new Comparator<Individual>() {
                @Override
                public int compare(Individual o1, Individual o2) {
                    if (o1 == null && o2 == null) {
                        return 0;
                    }
                    if (o1 == null) {
                        return 1;
                    }
                    if (o2 == null) {
                        return -1;
                    }
                    return o1.compareTo(o2);
                }
            });
    }

    /**
     * @param pop - last population obtained after evolution
     * @return - first action of best individual in the population (found at index 0)
     */
    private Types.ACTIONS get_best_action(ArrayList<Individual> pop) {
        int bestAction = pop.get(0).actions[0];
        actionValue = pop.get(0).value;
        return action_mapping.get(bestAction);
    }

    private void advanceState(StateObservation state, Types.ACTIONS action) {
        state.advance(action);
        heuristic.accumulateHeuristic(state);
    }

    @Override
    public Opinion askOpinion() {
        Types.ACTIONS action = this.act(stateObs, elapsedCpuTimer);
        return new Opinion(action, this.heuristicValue, getHeuristicName());
    }

    @Override
    public void initialiseVoice(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        this.stateObs = stateObs;
        this.elapsedCpuTimer = elapsedTimer;
    }

    @Override
    public Opinion call() throws Exception {
        return askOpinion();
    }
}
