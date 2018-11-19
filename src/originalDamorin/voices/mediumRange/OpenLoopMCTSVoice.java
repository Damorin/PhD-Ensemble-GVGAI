package originalDamorin.voices.mediumRange;

import java.util.Random;

import tools.ElapsedCpuTimer;
import originalDamorin.model.WorldInformation;
import originalDamorin.voices.Opinion;
import originalDamorin.voices.Voice;
import core.game.StateObservation;

/**
 * A Voice for mid range decisions. Uses the Open Loop variant MCTS algorithm
 * through {@link OpenLoopTreeNode}.
 * 
 * @author Damien Anderson (Damorin)
 *
 */
public class OpenLoopMCTSVoice implements Voice {

	private OpenLoopTreeNode rootNode;
	private Random random;
	private WorldInformation worldInformation;

	public OpenLoopMCTSVoice(StateObservation stateObs) {
		this.random = new Random();
		rootNode = new OpenLoopTreeNode(random);
		rootNode.setState(stateObs);
	}

	@Override
	public Opinion askOpinion(ElapsedCpuTimer elapsedTimer,
			WorldInformation worldInformation) {
		this.worldInformation = worldInformation;
		int suggestedAction = run(elapsedTimer);
		return new Opinion(suggestedAction, 5.0);
	}

	@Override
	public void update(StateObservation stateObs) {
		rootNode = new OpenLoopTreeNode(random);
		rootNode.setState(stateObs);
	}

	@Override
	public WorldInformation getUpdatedWorldInformation() {
		return this.worldInformation;
	}

	private int run(ElapsedCpuTimer elapsedTimer) {
		return rootNode.performSearch(elapsedTimer, worldInformation);
	}
}
