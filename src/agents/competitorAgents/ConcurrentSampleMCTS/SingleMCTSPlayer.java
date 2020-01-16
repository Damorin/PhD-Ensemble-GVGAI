package agents.competitorAgents.ConcurrentSampleMCTS;

import core.game.StateObservation;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 07/11/13
 * Time: 17:13
 */
public class SingleMCTSPlayer implements Callable<Double> {


    /**
     * Root of the tree.
     */
    public SingleTreeNode m_root;

    public int actionToTake;

    /**
     * Random generator.
     */
    public Random m_rnd;

    public int num_actions;
    public Types.ACTIONS[] actions;

    public ElapsedCpuTimer elapsedCpuTimer;

    public SingleMCTSPlayer(Random a_rnd, int num_actions, Types.ACTIONS[] actions, int actionToTake)
    {
        this.num_actions = num_actions;
        this.actions = actions;
        m_rnd = a_rnd;
        this.actionToTake = actionToTake;
    }

    /**
     * Inits the tree with the new observation state in the root.
     * @param a_gameState current state of the game.
     */
    public void init(StateObservation a_gameState, ElapsedCpuTimer elapsedCpuTimer)
    {
        //Set the game observation to a newly root node.
        //System.out.println("learning_style = " + learning_style);
        this.elapsedCpuTimer = elapsedCpuTimer;
        m_root = new SingleTreeNode(m_rnd, num_actions, actions);
        a_gameState.advance(actions[actionToTake]);
        m_root.rootState = a_gameState;
    }

    /**
     * Runs MCTS to decide the action to take. It does not reset the tree.
     * @param elapsedTimer Timer when the action returned is due.
     * @return the action to execute in the game.
     */
    public double run(ElapsedCpuTimer elapsedTimer)
    {
        elapsedCpuTimer = elapsedTimer;
        //Do the search within the available time.
        m_root.mctsSearch(elapsedTimer);

        //Determine the best action to take and return it.
        double action = m_root.mostVisitedAction();
        //int action = m_root.bestAction();
        return action;
    }

    @Override
    public Double call() throws Exception {
        return run(elapsedCpuTimer);
    }
}
