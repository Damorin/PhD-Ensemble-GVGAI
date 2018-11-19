package originalDamorin.voices.mediumRange;

import java.util.Random;

import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Utils;
import originalDamorin.Agent;
import originalDamorin.model.WorldInformation;
import core.game.StateObservation;

/**
 * This is an experimental version of the MCTS version which uses open-loop
 * scheduling.
 * 
 * This version is based heavily upon the SampleOLMCTS agent provided by the GVG
 * Framework.
 * 
 * @author Damien Anderson (Damorin)
 *
 */
public class OpenLoopTreeNode implements Node {
	private static final int INVALID_ACTION = -1;
	private final double HUGE_NEGATIVE = -10000000.0;
	private final double HUGE_POSITIVE = 10000000.0;
	private final int MAX_DEPTH = 10;
	private double epsilon = 1e-6;
	private OpenLoopTreeNode parent;
	private OpenLoopTreeNode[] children;
	private double totalValue;
	private int numberOfVisits;
	private Random m_rnd;
	private int depth;
	private static double[] bounds = new double[] { Double.MAX_VALUE,
			-Double.MAX_VALUE };
	private int childIdx;

	private StateObservation rootState;
	private WorldInformation worldInformation;

	public OpenLoopTreeNode(Random rnd) {
		this(null, INVALID_ACTION, rnd);
	}

	public OpenLoopTreeNode(OpenLoopTreeNode parent, int childIdx, Random rnd) {
		this.parent = parent;
		this.m_rnd = rnd;
		this.children = new OpenLoopTreeNode[Agent.numberOfAvailableActions];
		this.totalValue = 0.0;
		this.childIdx = childIdx;

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
	public int performSearch(ElapsedCpuTimer elapsedTimer,
			WorldInformation worldInformation) {
		this.worldInformation = worldInformation;

		double avgTimeTaken = 0;
		double acumTimeTaken = 0;
		long timeRemaining = elapsedTimer.remainingTimeMillis();
		int numberOfIterations = 0;

		if (this.worldInformation.hasGoalBeenSet()) {
			double goalScore = this.rollOutToGoal(rootState.copy());
			backUp(this, goalScore);
		}

		int remainingLimit = 5;
		while (timeRemaining > 2 * avgTimeTaken
				&& timeRemaining > remainingLimit) {

			StateObservation state = rootState.copy();

			ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
			OpenLoopTreeNode selected = selectANode(state);
			double delta = selected.rollOut(state);
			backUp(selected, delta);

			numberOfIterations++;
			acumTimeTaken += (elapsedTimerIteration.elapsedMillis());
			avgTimeTaken = acumTimeTaken / numberOfIterations;
			timeRemaining = elapsedTimer.remainingTimeMillis();
		}

		return this.mostVisitedAction();
	}

	private Double rollOutToGoal(StateObservation rollOutState) {

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

	private Double checkBounds(double delta) {
		if (delta < bounds[0]) {
			bounds[0] = delta;
		}

		if (delta > bounds[1]) {
			bounds[1] = delta;
		}
		return delta;
	}

	private double value(StateObservation a_gameState) {

		boolean gameOver = a_gameState.isGameOver();
		Types.WINNER win = a_gameState.getGameWinner();
		double rawScore = a_gameState.getGameScore();

		if (gameOver && win == Types.WINNER.PLAYER_LOSES)
			rawScore += HUGE_NEGATIVE;

		if (gameOver && win == Types.WINNER.PLAYER_WINS)
			rawScore += HUGE_POSITIVE;

		return rawScore;
	}

	private void backUp(OpenLoopTreeNode node, double result) {
		OpenLoopTreeNode nextNode = node;
		while (nextNode != null) {
			nextNode.numberOfVisits++;
			nextNode.totalValue += result;
			nextNode = nextNode.parent;
		}
	}

	private OpenLoopTreeNode selectANode(StateObservation state) {

		OpenLoopTreeNode node = this;

		while (!state.isGameOver() && node.depth < MAX_DEPTH) {
			if (node.notFullyExpanded()) {
				return node.expand(state);
			} else {
				OpenLoopTreeNode nextNode = node.uct(state);
				node = nextNode;
			}
		}

		return node;
	}

	private boolean notFullyExpanded() {
		for (OpenLoopTreeNode tn : children) {
			if (tn == null) {
				return true;
			}
		}

		return false;
	}

	private OpenLoopTreeNode expand(StateObservation state) {

		int bestAction = 0;
		double bestValue = INVALID_ACTION;

		for (int i = 0; i < children.length; i++) {
			double randomDouble = m_rnd.nextDouble();
			if (randomDouble > bestValue && children[i] == null) {
				bestAction = i;
				bestValue = randomDouble;
			}
		}

		state.advance(Agent.availableActions[bestAction]);

		OpenLoopTreeNode expandedNode = new OpenLoopTreeNode(this, bestAction,
				this.m_rnd);
		children[bestAction] = expandedNode;
		return expandedNode;
	}

	private double rollOut(StateObservation state) {
		int thisDepth = this.depth;

		while (!finishRollout(state, thisDepth)) {

			int action = m_rnd.nextInt(Agent.numberOfAvailableActions);
			state.advance(Agent.availableActions[action]);
			thisDepth++;
		}

		double rollOutValue = value(state);

		return checkBounds(rollOutValue);
	}

	private boolean finishRollout(StateObservation rollerState, int depth) {
		if (depth >= MAX_DEPTH)
			return true;

		if (rollerState.isGameOver())
			return true;

		return false;
	}

	private OpenLoopTreeNode uct(StateObservation state) {

		OpenLoopTreeNode selected = null;
		double bestValue = -Double.MAX_VALUE;
		for (OpenLoopTreeNode child : this.children) {
			double hvVal = child.totalValue;
			double childValue = hvVal / (child.numberOfVisits + this.epsilon);

			childValue = Utils.normalise(childValue, bounds[0], bounds[1]);

			double uctValue = childValue
					+ Math.sqrt(2)
					* Math.sqrt(Math.log(this.numberOfVisits + 1)
							/ (child.numberOfVisits + this.epsilon));

			uctValue = Utils.noise(uctValue, this.epsilon,
					this.m_rnd.nextDouble()); // break ties randomly

			// small sampleRandom numbers: break ties in unexpanded nodes
			if (uctValue > bestValue) {
				selected = child;
				bestValue = uctValue;
			}
		}
		if (selected == null) {
			throw new RuntimeException("Warning! returning null: " + bestValue
					+ " : " + this.children.length + " " + +bounds[0] + " "
					+ bounds[1]);
		}

		// Roll the state:
		state.advance(Agent.availableActions[selected.childIdx]);

		return selected;
	}

	private int mostVisitedAction() {
		int selected = INVALID_ACTION;
		double bestValue = -Double.MAX_VALUE;
		boolean allEqual = true;
		double first = INVALID_ACTION;

		for (int i = 0; i < children.length; i++) {

			if (children[i] != null) {
				if (first == INVALID_ACTION)
					first = children[i].numberOfVisits;
				else if (first != children[i].numberOfVisits) {
					allEqual = false;
				}

				double childValue = children[i].numberOfVisits;
				childValue = Utils.noise(childValue, this.epsilon,
						this.m_rnd.nextDouble()); // break ties randomly
				if (childValue > bestValue) {
					bestValue = childValue;
					selected = i;
				}
			}
		}

		if (selected == INVALID_ACTION) {
			System.out.println("Unexpected selection!");
			selected = 0;
		} else if (allEqual) {
			// If all are equal, we opt to choose for the one with the best Q.
			selected = bestAction();
		}
		return selected;
	}

	private int bestAction() {
		int selected = INVALID_ACTION;
		double bestValue = -Double.MAX_VALUE;

		for (int i = 0; i < children.length; i++) {

			if (children[i] != null) {
				double childValue = children[i].totalValue
						/ (children[i].numberOfVisits + this.epsilon);
				childValue = Utils.noise(childValue, this.epsilon,
						this.m_rnd.nextDouble()); // break ties randomly
				if (childValue > bestValue) {
					bestValue = childValue;
					selected = i;
				}
			}
		}

		if (selected == INVALID_ACTION) {
			System.out.println("Unexpected selection!");
			selected = 0;
		}

		return selected;
	}

	@Override
	public void setState(StateObservation stateObs) {
		this.rootState = stateObs;
	}
}
