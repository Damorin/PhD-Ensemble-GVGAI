package concurrentRHEA;

import java.util.Random;

public class AgentParameters {

    private static final Random RNG = new Random();

    int populationSize, simulationDepth,
            crossoverType, mutation,
            tournamentSize, noParents,
            resample, elitism;
    boolean reevaluate;
    double discount;

    public AgentParameters() {
        populationSize = 4 + RNG.nextInt(20);  // Minimum can be 1 and should still work (doing only mutation of the 1 individual).
        simulationDepth = 2 + RNG.nextInt(20);
        discount = 0.9;//RNG.nextDouble();  // This was fixed in Agent.java and should actually be better with *some* discount, but probably better bounded (i.e. [0.7, 1.0], or maybe even stricter and closer to 1).
        crossoverType = RNG.nextInt(2);
        reevaluate = false; //RNG.nextBoolean(); In most cases this is not a good idea, it's kind of a waste of computation time, so I'd keep it off
        mutation = 1 + RNG.nextInt(2);  // nextInt(n) is exclusive of n, so this should be mutation either 1 or 2, before was only 1.
        tournamentSize = Math.max(2, Math.min(5, RNG.nextInt(Math.max(2,populationSize)-2)));  // Added a maximum value of 5 to reduce selection pressure, and also a minimum on pop size in case you leave that one as a minimum of 1 just to not crash (this value wouldn't be used in that case anyway).
        noParents = 2; // Math.max(2, RNG.nextInt(tournamentSize)); This actually doesn't make much sense to have more than 2 parents, and for one version of crossover only the first 2 are used anyway
        resample = 1;
        elitism = 1; //RNG.nextInt(noParents/2);
    }

    private AgentParameters (
            int populationSize,
            int simulationDepth,
            double discount,
            int crossoverType,
            int mutation,
            int tournamentSize) {
        this.populationSize = populationSize;
        this.simulationDepth = simulationDepth;
        this.discount = discount;
        this.crossoverType = crossoverType;
        this.reevaluate = false;
        this.mutation = mutation;
        this.tournamentSize = tournamentSize;
        this.noParents = 2;
        this.resample = 1;
        this.elitism = 1;
    }

    public static AgentParameters combine(AgentParameters ap1, AgentParameters ap2) {
        int populationSize = (ap1.populationSize + ap2.populationSize) / 2;
        int simulationDepth = (ap1.simulationDepth + ap2.simulationDepth) / 2;
        double discount = (ap1.discount + ap2.discount) / 2;
        int crossoverType = (ap1.crossoverType == ap2.crossoverType) ? ap1.crossoverType : RNG.nextInt(2);
        int mutation = (ap1.mutation == ap2.mutation) ? ap1.mutation : 1 + RNG.nextInt(2);
        int tournamentSize = (ap1.tournamentSize + ap2.tournamentSize) / 2;
        return new AgentParameters(
                populationSize,
                simulationDepth,
                discount,
                crossoverType,
                mutation,
                tournamentSize
        );
    }

    protected AgentParameters mutate() {
        if (RNG.nextBoolean()) {
            populationSize = populationSize + (RNG.nextInt(populationSize) - populationSize/2)/2;
        }
        if (RNG.nextBoolean()) {
            simulationDepth = simulationDepth + (RNG.nextInt(simulationDepth) - simulationDepth / 2) / 2;
        }
        if (RNG.nextBoolean()) {
            discount = discount + (RNG.nextDouble() - 0.5) * 0.01;
        }
        if (RNG.nextBoolean()) {
            tournamentSize = tournamentSize + (RNG.nextInt(tournamentSize) - tournamentSize / 2) / 2;
        }
        return this;
    }

    public AgentParameters copy() {
        return new AgentParameters(
                populationSize,
                simulationDepth,
                discount,
                crossoverType,
                mutation,
                tournamentSize
        );
    }

    @Override
    public String toString() {
        return "AgentParameters {" +
                "\n\tpopulationSize  = " + populationSize +
                "\n\tsimulationDepth = " + simulationDepth +
                "\n\tdiscount        = " + discount +
                "\n\tcrossoverType   = " + crossoverType +
                "\n\treevaluate      = " + reevaluate +
                "\n\tmutation        = " + mutation +
                "\n\ttournamentSize  = " + tournamentSize +
                "\n\tnoParents       = " + noParents +
                "\n\tresample        = " + resample +
                "\n\telitism         = " + elitism +
                "\n}";
    }
}
