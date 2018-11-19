package originalDamorin.voices.mediumRange;

import java.util.Random;

import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Utils;
import originalDamorin.Agent;
import originalDamorin.model.WorldInformation;
import core.game.StateObservation;

/**
 * Represents an individual node of the MCTS Tree.
 * 
 * This particular algorithm uses UCT (Upper Constraint bound for Trees) to
 * select the next node for expansion.
 * 
 * The design is heavily inspired from the sampleMCTS model developed by the
 * GVG-AI competition team and the enhanced version used for the Shmokin agent
 * from the 2014 competition by Blaine Ross.
 * 
 * @author Damien Anderson (Damorin)
 *
 */
public class TreeNode implements Node {

	private static final int INVALID_ACTION = -1;
	private static final int TIMEOUT = 5;
	private static final int MAX_DEPTH = 15;
	private static final double EPSILON = 1e-6;
	private static final double HUGE_NUMBER = 10000000.0;
	private static double[] bounds = new double[] { Double.MAX_VALUE,
			-Double.MAX_VALUE };

	private TreeNode parent;
	private TreeNode[] children;
	private StateObservation state;
	private Random random;
	private double value;
	private int depth;
	private int visits;
	private WorldInformation worldInformation;

	/**
	 * Basic Constructor for a new {@link TreeNode}
	 * 
	 * @param random
	 *            the {@link Random} generator
	 */
	public TreeNode(Random random) {
		this(null, null, random);
	}

	/**
	 * Constructor for creating the tree of states.
	 * 
	 * @param stateObs
	 *            the {@link StateObservation}
	 * @param parent
	 *            the {@link TreeNode} which is the parent of this
	 * @param random
	 *            the {@link Random} generator
	 */
	public TreeNode(StateObservation stateObs, TreeNode parent, Random random) {
		this.state = stateObs;
		this.parent = parent;
		this.children = new TreeNode[Agent.availableActions.length];
		this.random = random;
		this.value = 0.0;

		this.setDepth();
	}

	private void setDepth() {
		if (this.parent != null) {
			this.depth = this.parent.depth + 1;
		} else {
			this.depth = 0;
		}
	}

	@Override
	public int performSearch(ElapsedCpuTimer timer,
			WorldInformation worldInformation) {

		this.worldInformation = worldInformation;

		double avgTimeTaken = 0;
		double totalTimeTaken = 0;
		long remaining = timer.remainingTimeMillis();
		int iterations = 0;

		if (this.worldInformation.hasGoalBeenSet()) {
			double goalScore = this.rollOutToGoal();
			backUp(this, goalScore);
		}

		while (remaining > 2 * avgTimeTaken && remaining > TIMEOUT) {
			ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
			TreeNode selectedNode = selectANode();
			double score = selectedNode.rollOut();

			backUp(selectedNode, score);

			iterations++;
			totalTimeTaken += (elapsedTimerIteration.elapsedMillis());

			avgTimeTaken = totalTimeTaken / iterations;
			remaining = timer.remainingTimeMillis();
		}
		return this.mostVisitedAction();
	}

	private Double rollOutToGoal() {
		StateObservation rollOutState = state.copy();

		for (int step = 0; step < this.worldInformation.getPathToGoal().size(); step++) {
			if (step < MAX_DEPTH) {
				rollOutState.advance(this.worldInformation.getPathToGoal().get(
						step));
			}
		}

		double rollOutValue = value(rollOutState);
		if (rollOutValue < 0) {
			worldInformation.setGoalValidity(false);
		}

		return checkBounds(rollOutValue);
	}

	private double value(StateObservation a_gameState) {

		boolean gameOver = a_gameState.isGameOver();
		Types.WINNER win = a_gameState.getGameWinner();
		double rawScore = a_gameState.getGameScore();

		if (gameOver && win == Types.WINNER.PLAYER_LOSES) {
			rawScore += -HUGE_NUMBER;
		}

		if (gameOver && win == Types.WINNER.PLAYER_WINS) {
			rawScore += HUGE_NUMBER;
		}

		return rawScore;
	}

	private Double checkBounds(double delta) {
		if (delta < bounds[0]) {
			bounds[0] = delta;
		}

		if (delta > bounds[1]) {
			bounds[1] = delta;
		}
		return delta;
	}

	private void backUp(TreeNode node, double result) {
		TreeNode nodeToPropogate = node;
		while (nodeHasParent(nodeToPropogate)) {
			nodeToPropogate.visits++;
			nodeToPropogate.value += result;
			nodeToPropogate = nodeToPropogate.parent;
		}
	}

	private boolean nodeHasParent(TreeNode nodeToPropogate) {
		return nodeToPropogate != null;
	}

	private TreeNode selectANode() {
		TreeNode current = this;

		while (!current.state.isGameOver() && current.depth < MAX_DEPTH) {
			if (current.hasNotExpanded()) {
				return current.expand();
			} else {
				TreeNode next = current.uct();
				current = next;
			}
		}
		return current;
	}

	private boolean hasNotExpanded() {
		for (TreeNode child : this.children) {
			if (child == null) {
				return true;
			}
		}
		return false;
	}

	private TreeNode expand() {

		int bestAction = 0;
		double bestValue = INVALID_ACTION;

		for (int i = 0; i < children.length; i++) {
			StateObservation nextState = state.copy();
			nextState.advance(Agent.availableActions[i]);

			double nextDouble = nextState.getGameScore();

			if (nextDouble > bestValue && children[i] == null) {
				bestAction = i;
				bestValue = nextDouble;
			}
		}

		StateObservation returnState = state.copy();
		returnState.advance(Agent.availableActions[bestAction]);

		TreeNode node = new TreeNode(returnState, this, this.random);

		children[bestAction] = node;
		return node;
	}

	private TreeNode uct() {

		TreeNode selected = null;
		double bestValue = -Double.MAX_VALUE;
		for (TreeNode child : this.children) {
			double initialValue = child.value;
			double childValue = initialValue / (child.visits + EPSILON);

			double uctValue = childValue(child, childValue);

			if (uctValue > bestValue) {
				selected = child;
				bestValue = uctValue;
			}
		}

		if (selected == null) {
			throw new RuntimeException("Warning! returning null: " + bestValue
					+ " : " + this.children.length);
		}

		return selected;
	}

	private double childValue(TreeNode child, double childValue) {
		return childValue
				+ Math.sqrt(2)
				* Math.sqrt(Math.log(this.visits + 1)
						/ (child.visits + EPSILON)) + this.random.nextDouble()
				* EPSILON;
	}

	private Double rollOut() {

		StateObservation rollOutState = state.copy();
		int thisDepth = this.depth;

		while (!isRollOutFinished(rollOutState, thisDepth)) {
			int action = random.nextInt(children.length);
			rollOutState.advance(Agent.availableActions[action]);
			thisDepth++;
		}

		double rollOutValue = value(rollOutState);

		return checkBounds(rollOutValue);
	}

	private boolean isRollOutFinished(StateObservation nextState, int depth) {
		if (depth >= MAX_DEPTH) {
			return true;
		}

		if (nextState.isGameOver()) {
			return true;
		}
		return false;
	}

	private int mostVisitedAction() {
		int selected = INVALID_ACTION;
		double bestValue = -Double.MAX_VALUE;
		boolean allEqual = true;
		double first = INVALID_ACTION;

		for (int i = 0; i < children.length; i++) {

			if (children[i] != null) {
				if (first == INVALID_ACTION)
					first = children[i].visits;
				else if (first != children[i].visits) {
					allEqual = false;
				}

				double childValue = children[i].visits;
				childValue = Utils.noise(childValue, EPSILON,
						this.random.nextDouble()); // break ties randomly
				if (childValue > bestValue) {
					bestValue = childValue;
					selected = i;
				}
			}
		}

		if (actionIsValid(selected)) {
			System.out.println("Unexpected selection!");
			selected = 0;
		} else if (allEqual) {
			selected = bestAction();
		}
		return selected;
	}

	private boolean actionIsValid(int selected) {
		return selected == INVALID_ACTION;
	}

	private int bestAction() {
		int selected = INVALID_ACTION;
		double bestValue = -Double.MAX_VALUE;

		for (int i = 0; i < children.length; i++) {

			if (children[i] != null
					&& children[i].value + random.nextDouble() * EPSILON > bestValue) {
				bestValue = children[i].value;
				selected = i;
			}
		}

		if (actionIsValid(selected)) {
			System.out.println("Unexpected selection!");
			selected = 0;
		}

		return selected;
	}

	@Override
	public void setState(StateObservation stateObs) {
		this.state = stateObs;
	}

}
