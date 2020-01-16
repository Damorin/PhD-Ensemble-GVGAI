package agents.edsAgents.Concurrent_SingleAction_EDS.voices.rhea;

import COGPaper.controllers.AbstractHeuristicPlayer;
import COGPaper.heuristics.StateHeuristic;
import agents.edsAgents.Concurrent_SingleAction_EDS.voices.Opinion;
import agents.edsAgents.Concurrent_SingleAction_EDS.voices.Voice;
import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.*;

public class Agent extends AbstractHeuristicPlayer implements Voice {

    // variable
    private int POPULATION_SIZE = 10;
    private int SIMULATION_DEPTH = 10;
    private double DISCOUNT = 1; //0.99;
    private int CROSSOVER_TYPE = UNIFORM_CROSS;

    // set
    private boolean REEVALUATE = false;
    //    private boolean REPLACE = false;
    private int MUTATION = 1;
    private int TOURNAMENT_SIZE = 2;
    private int RESAMPLE = 1;
    private int ELITISM = 1;

    // constants
    private final long BREAK_MS = 10;
    public static final double epsilon = 1e-6;
    static final int POINT1_CROSS = 0;
    static final int UNIFORM_CROSS = 1;

    private Individual[] population, nextPop;
    private int NUM_INDIVIDUALS;
    private int N_ACTIONS;
    private HashMap<Integer, Types.ACTIONS> action_mapping;

    private ElapsedCpuTimer timer;
    private Random randomGenerator;

    private double acumTimeTakenEval = 0, avgTimeTakenEval = 0, avgTimeTaken = 0, acumTimeTaken = 0;
    private int numEvals = 0, numIters = 0;
    private double value;
    private boolean keepIterating = true;
    private long remaining;
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
        // The heuristic is updated if needed
        heuristic.updateHeuristicBasedOnCurrentState(stateObs);

        this.timer = elapsedTimer;
        avgTimeTaken = 0;
        acumTimeTaken = 0;
        numEvals = 0;
        acumTimeTakenEval = 0;
        numIters = 0;
        remaining = timer.remainingTimeMillis();
        NUM_INDIVIDUALS = 0;
        keepIterating = true;

        // INITIALISE POPULATION
        init_pop(stateObs);


        // RUN EVOLUTION
        remaining = timer.remainingTimeMillis();
        while (remaining > avgTimeTaken && remaining > BREAK_MS && keepIterating) {
            runIteration(stateObs);
            remaining = timer.remainingTimeMillis();
        }

        // RETURN ACTION
        Types.ACTIONS best = get_best_action(population);
        value = population[0].value;
        return best;
    }

    /**
     * Run evolutionary process for one generation
     *
     * @param stateObs - current game state
     */
    private void runIteration(StateObservation stateObs) {
        ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();

        if (REEVALUATE) {
            for (int i = 0; i < ELITISM; i++) {
                if (remaining > 2 * avgTimeTakenEval && remaining > BREAK_MS) { // if enough time to evaluate one more individual
                    evaluate(population[i], heuristic, stateObs);
                } else {
                    keepIterating = false;
                }
            }
        }

        // mutate one individual randomly
        for (int i = ELITISM; i < NUM_INDIVIDUALS; i++) {
            if (remaining > 2 * avgTimeTakenEval && remaining > BREAK_MS) { // if enough time to evaluate one more individual
                Individual newind;

                newind = crossover(population, population[0]);
                newind = newind.mutate(MUTATION);

                // evaluate new individual, insert into population
                add_individual(newind, nextPop, i, stateObs);

                remaining = timer.remainingTimeMillis();

            } else {
                keepIterating = false;
                break;
            }
        }
        if (NUM_INDIVIDUALS > 1)
            Arrays.sort(nextPop, new Comparator<Individual>() {
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

        population = nextPop.clone();

        numIters++;
        acumTimeTaken += (elapsedTimerIteration.elapsedMillis());
        avgTimeTaken = acumTimeTaken / numIters;
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

        return value;
    }

    /**
     * @param pop - the population from which a new individual should be produced
     * @return - the individual resulting from crossover applied to the specified population
     */
    private Individual crossover(Individual[] pop, Individual newind) {
        if (NUM_INDIVIDUALS > 1) {
            Individual[] tournament = new Individual[TOURNAMENT_SIZE];
            if (NUM_INDIVIDUALS > 2) {
                ArrayList<Individual> list = new ArrayList<>();
                list.addAll(Arrays.asList(pop).subList(1, NUM_INDIVIDUALS));
                Collections.shuffle(list);
                for (int i = 0; i < TOURNAMENT_SIZE; i++) {
                    tournament[i] = list.get(i);
                }
            } else {
                tournament[0] = pop[0];
                tournament[1] = pop[1];
            }
            newind.crossover(tournament, CROSSOVER_TYPE);
        }
        return newind;
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

        population = new Individual[POPULATION_SIZE];
        nextPop = new Individual[POPULATION_SIZE];
        for (int i = 0; i < POPULATION_SIZE; i++) {
            if (i == 0 || remaining > avgTimeTakenEval && remaining > BREAK_MS) {
                population[i] = new Individual(SIMULATION_DEPTH, N_ACTIONS, randomGenerator);
                evaluate(population[i], heuristic, stateObs);
                remaining = timer.remainingTimeMillis();
                NUM_INDIVIDUALS = i + 1;
            } else {
                break;
            }
        }

        if (NUM_INDIVIDUALS > 1)
            Arrays.sort(population, new Comparator<Individual>() {
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
        for (int i = 0; i < NUM_INDIVIDUALS; i++) {
            if (population[i] != null)
                nextPop[i] = population[i].copy();
        }

    }

    /**
     * @param pop - last population obtained after evolution
     * @return - first action of best individual in the population (found at index 0)
     */
    private Types.ACTIONS get_best_action(Individual[] pop) {
        int bestAction = pop[0].actions[0];
        return action_mapping.get(bestAction);
    }

    private void advanceState(StateObservation state, Types.ACTIONS action) {
        state.advance(action);
        heuristic.accumulateHeuristic(state);
    }

    @Override
    public Opinion askOpinion() {
        return new Opinion(this.act(stateObs, elapsedCpuTimer), this.value, getHeuristicName());
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
