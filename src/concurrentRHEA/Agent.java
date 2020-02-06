package concurrentRHEA;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tracks.singlePlayer.tools.Heuristics.StateHeuristic;
import tracks.singlePlayer.tools.Heuristics.WinScoreHeuristic;

import java.util.*;

public class Agent extends AbstractPlayer {

    private final AgentParameters params;

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

    private StateHeuristic heuristic;
    private double acumTimeTakenEval = 0,avgTimeTakenEval = 0, avgTimeTaken = 0, acumTimeTaken = 0;
    private int numEvals = 0, numIters = 0;
    private boolean keepIterating = true;
    private long remaining;

    /**
     * Public constructor with state observation and time due.
     *
     * @param stateObs     state observation of the current game.
     * @param elapsedTimer Timer for the controller creation.
     */
    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer, AgentParameters params) {
        this.params = params;
        randomGenerator = new Random();
        heuristic = new WinScoreHeuristic(stateObs);
        this.timer = elapsedTimer;
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
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
        return best;
    }

    /**
     * Run evolutionary process for one generation
     * @param stateObs - current game state
     */
    private void runIteration(StateObservation stateObs) {
        ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();

        if (params.reevaluate) {
            for (int i = 0; i < params.elitism; i++) {
                if (remaining > 2*avgTimeTakenEval && remaining > BREAK_MS) { // if enough time to evaluate one more individual
                    evaluate(population[i], heuristic, stateObs);
                } else {keepIterating = false;}
            }
        }

        if (NUM_INDIVIDUALS > 1) {
            for (int i = params.elitism; i < NUM_INDIVIDUALS; i++) {
                if (remaining > 2*avgTimeTakenEval && remaining > BREAK_MS) { // if enough time to evaluate one more individual
                    Individual newind;

                    newind = crossover();
                    newind = newind.mutate(params.mutation);

                    // evaluate new individual, insert into population
                    add_individual(newind, nextPop, i, stateObs);

                    remaining = timer.remainingTimeMillis();

                } else {keepIterating = false; break;}
            }
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
        } else if (NUM_INDIVIDUALS == 1){
            Individual newind = new Individual(params.simulationDepth, N_ACTIONS, randomGenerator).mutate(params.mutation);
            evaluate(newind, heuristic, stateObs);
            if (newind.value > population[0].value)
                nextPop[0] = newind;
        }

        population = nextPop.clone();

        numIters++;
        acumTimeTaken += (elapsedTimerIteration.elapsedMillis());
        avgTimeTaken = acumTimeTaken / numIters;
    }

    /**
     * Evaluates an individual by rolling the current state with the actions in the individual
     * and returning the value of the resulting state; random action chosen for the opponent
     * @param individual - individual to be valued
     * @param heuristic - heuristic to be used for state evaluation
     * @param state - current state, root of rollouts
     * @return - value of last state reached
     */
    private double evaluate(Individual individual, StateHeuristic heuristic, StateObservation state) {

        ElapsedCpuTimer elapsedTimerIterationEval = new ElapsedCpuTimer();

        StateObservation st = state.copy();
        int i;
        double acum = 0, avg;
        double value = 0;

        for (i = 0; i < params.simulationDepth; i++) {
            if (! st.isGameOver()) {
                ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();

                st.advance(action_mapping.get(individual.actions[i]));
                value += heuristic.evaluateState(st) * Math.pow(params.discount, i);

                acum += elapsedTimerIteration.elapsedMillis();
                avg = acum / (i+1);
                remaining = timer.remainingTimeMillis();
                if (remaining < 2*avg || remaining < BREAK_MS) break;
            } else {
                break;
            }
        }

        individual.value = value;

        numEvals++;
        acumTimeTakenEval += (elapsedTimerIterationEval.elapsedMillis());
        avgTimeTakenEval = acumTimeTakenEval / numEvals;
        remaining = timer.remainingTimeMillis();

        return value;
    }

    /**
     * @return - the individual resulting from crossover applied to the specified population
     */
    private Individual crossover() {
        Individual individual = null;
        if (NUM_INDIVIDUALS > 1) {
            individual = new Individual(params.simulationDepth, N_ACTIONS, randomGenerator);
            Individual[] tournament = new Individual[params.tournamentSize];
            Individual[] parents = new Individual[params.noParents];

            ArrayList<Individual> list = new ArrayList<>();
            if (NUM_INDIVIDUALS > params.tournamentSize) {
                list.addAll(Arrays.asList(population).subList(params.elitism, NUM_INDIVIDUALS));
            } else {
                list.addAll(Arrays.asList(population));
            }

            if (list.isEmpty()) {
                list.addAll(Arrays.asList(population));
            }

            //Select a number of random distinct individuals for tournament and sort them based on value
            for (int i = 0; i < params.tournamentSize; i++) {
                int index = randomGenerator.nextInt(list.size());
                tournament[i] = list.get(index);
//                list.remove(index);
            }
            Arrays.sort(tournament);

            //get best individuals in tournament as parents
            if (params.noParents <= params.tournamentSize) {
                for (int i = 0; i < params.noParents; i++) {
                    try {
                        parents[i] = list.get(i);
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                        System.out.println("Parents: " + parents.length);
                        System.out.println("List size: " + list.size());
                        System.exit(1);
                    }
                }
                individual.crossover(parents, params.crossoverType);
            } else {
                System.out.println("WARNING: Number of parents must be LESS than tournament size.");
            }
        }
        return individual;
    }

    /**
     * Insert a new individual into the population at the specified position by replacing the old one.
     * @param newind - individual to be inserted into population
     * @param pop - population
     * @param idx - position where individual should be inserted
     * @param stateObs - current game state
     */
    private void add_individual(Individual newind, Individual[] pop, int idx, StateObservation stateObs) {
        evaluate(newind, heuristic, stateObs);
        pop[idx] = newind.copy();
    }

    /**
     * Initialize population
     * @param stateObs - current game state
     */
    private void init_pop(StateObservation stateObs) {

        double remaining = timer.remainingTimeMillis();

        N_ACTIONS = stateObs.getAvailableActions().size() + 1;
        action_mapping = new HashMap<>();
        int k = 0;
        for (Types.ACTIONS action : stateObs.getAvailableActions()) {
            action_mapping.put(k, action);
            k++;
        }
        action_mapping.put(k, Types.ACTIONS.ACTION_NIL);

        population = new Individual[params.populationSize];
        nextPop = new Individual[params.populationSize];
        for (int i = 0; i < params.populationSize; i++) {
            if (i == 0 || remaining > avgTimeTakenEval && remaining > BREAK_MS) {
                population[i] = new Individual(params.simulationDepth, N_ACTIONS, randomGenerator);
                evaluate(population[i], heuristic, stateObs);
                remaining = timer.remainingTimeMillis();
                NUM_INDIVIDUALS = i+1;
            } else {break;}
        }

        if (NUM_INDIVIDUALS > 1)
            Arrays.sort(population, (o1, o2) -> {
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

    protected double getBestEvaluation() {
        return population[0].value;
    }

    protected Types.ACTIONS getBestAction() {
        return action_mapping.get(population[0].actions[0]);
    }

}
