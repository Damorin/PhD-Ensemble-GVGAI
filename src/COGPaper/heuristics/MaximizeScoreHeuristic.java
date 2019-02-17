package COGPaper.heuristics;

import core.game.Game;
import core.game.StateObservation;
import ontology.Types;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by kisenshi on 03/03/17.
 * This heuristic focuses on maximizing the score if the player has not win yet
 */
public class MaximizeScoreHeuristic extends StateHeuristic {
    double last_score;

    public MaximizeScoreHeuristic(StateObservation stateObs) {
        initHeuristicAccumulation();
    }

    public double evaluateState(StateObservation stateObs) {
        boolean gameOver = stateObs.isGameOver();
        Types.WINNER win = stateObs.getGameWinner();
        double rawScore = stateObs.getGameScore();

//        System.out.println("MaximizeScoreHeuristic called");

        if (gameOver && win == Types.WINNER.PLAYER_LOSES) {
            return HUGE_NEGATIVE;
        }

        if (gameOver && win == Types.WINNER.PLAYER_WINS) {
            return HUGE_POSITIVE;
        }

        // It is returned the score change as heuristic
        return (rawScore - last_score);
    }

    public void updateHeuristicBasedOnCurrentState(StateObservation stateObs) {
        // It is stored the last score to take it as reference for the heuristic
        last_score = stateObs.getGameScore();
    }

    @Override
    public void recordDataOnFile(Game played, String fileName, int randomSeed, int[] recordIds) {

        // Data:
        // gameId controllerId randomSeed winnerId score gameTicks

        try {
            if (fileName != null && !fileName.equals("")) {
                writer = new BufferedWriter(new FileWriter(new File(fileName), true));
                writer.write(recordIds[0] + " " + recordIds[1] + " " + randomSeed +
                        " " + (played.getWinner() == Types.WINNER.PLAYER_WINS ? 1 : 0) +
                        " " + played.getScore() + " " + played.getGameTick() + "\n");

                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawInScreen(Graphics2D g) {
        // For this heuristic is not needed to do anything
        return;
    }
}
