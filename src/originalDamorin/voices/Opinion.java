package originalDamorin.voices;

/**
 * Represents what a {@link Voice} is saying. The action and how urgent the
 * action is are represented here.
 * 
 * @author Damien Anderson (Damorin)
 *
 */
public class Opinion {

	private int action;
	private double urgency;

	/**
	 * The constructor for a new {@link Opinion}.
	 * 
	 * @param action
	 *            the action the {@link Voice} wishes to take
	 * @param urgency
	 *            how important the action is to take
	 */
	public Opinion(int action, double urgency) {
		this.action = action;
		this.urgency = urgency;
	}

	/**
	 * Returns the action that a {@link Voice} wishes to take. Defined as an
	 * int.
	 * 
	 * @return an action to take
	 */
	public int getAction() {
		return action;
	}

	/**
	 * Returns the importance of the action.
	 * 
	 * @return the urgency of the action
	 */
	public double getUrgency() {
		return urgency;
	}

}
