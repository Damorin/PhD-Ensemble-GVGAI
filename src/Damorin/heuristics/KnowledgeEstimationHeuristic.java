package Damorin.heuristics;

import core.game.Event;
import core.game.Game;
import core.game.StateObservation;
import ontology.Types;
import tools.Vector2d;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Cristina on 29/03/2017.
 */
public class KnowledgeEstimationHeuristic extends KnowledgeHeuristic {

    /**
     * When the class is instantiated it is needed to initialise the data
     *
     * @param stateObs
     */
    public KnowledgeEstimationHeuristic(StateObservation stateObs) {
        super(stateObs);

        // Acknowledge is initialised
        sprites_from_avatar_acknowledge = new ArrayList<>();
        updateSpriteAcknowledge(stateObs);

        interaction_history.setUseStats(true);
    }

    private void updateSpriteStats(ArrayList<Event> last_gametick_events, StateObservation stateObs, StateObservation last_stateObs){
        int avatar_stype =  last_stateObs.getAvatarType();
        for (Event last_event : last_gametick_events) {
            interaction_history.updateSpritesStatsKnowledge(last_event, stateObs, last_stateObs, avatar_stype, sprites_from_avatar_acknowledge);
        }
    }

    private double getHeuristicForInteractionsInState(ArrayList<Event> last_gametick_events, StateObservation stateObs, StateObservation last_stateObs){
        int avatar_stype =  last_stateObs.getAvatarType();
        int min_n_times_checked = -1;

        for (Event last_event : last_gametick_events) {
            int n_times_checked = interaction_history.getNTimesInteractionChecked(last_event, avatar_stype, sprites_from_avatar_acknowledge);

            if ((min_n_times_checked == -1) || (min_n_times_checked > n_times_checked)){
                min_n_times_checked = n_times_checked;
            }
        }

        /* The heuristic is obtained considering the total number of states checked already.
        * It is obtained the percentage of the times checked the current state onto the total
        * If this percentage is small, it is because it has not been checked as usual as others, so the
        * heuristic must be big for these cases.
        * So the heuristic will be calculated using the formula:
        * h = (1 - percentage)*100
        * It will always be returned a number between 0 an 100
        * */

        double percentage_checked = (double)min_n_times_checked/interaction_history.getNTotalStatesChecked();

        return (1 - percentage_checked)*100;
    }


    @Override
    public double evaluateState(StateObservation stateObs) {
        boolean gameOver = stateObs.isGameOver();
        Types.WINNER win = stateObs.getGameWinner();

        ArrayList<Event> last_gametick_events = getLastGametickEvents(stateObs, last_stateObs);

        // The different sprites in the evaluated state are added to the 'sprite acknowledgement' of the agent
        boolean ack_update = updateSpriteAcknowledge(stateObs);
        updateSpriteStats(last_gametick_events, stateObs, last_stateObs);

        // For this heuristic is penalised finishing the game, either when winning or losing it
        if(gameOver && win == Types.WINNER.PLAYER_LOSES){
            return HUGE_NEGATIVE;
        }

        if(gameOver && win == Types.WINNER.PLAYER_WINS) {
            return LESS_HUGE_NEGATIVE;
        }

        Vector2d currentPosition = stateObs.getAvatarPosition();

        if (isOutOfBounds(currentPosition)){
            // If the new position is out of bounds then dont go there
            return HUGE_NEGATIVE;
        }

        // New sprite has appeared, this is GOOD
        if (ack_update){
            return HUGE_POSITIVE;
        }

        if (!last_gametick_events.isEmpty()) {
            if (isNewStypeInteraction(last_gametick_events, last_stateObs.getAvatarType())) {
                return 1000;
            }

            // The REWARD here will be a number between 0 and 100
            return this.getHeuristicForInteractionsInState(last_gametick_events, stateObs, last_stateObs);
        }

        // If it is not a collision it is returned a number depending on the number of stats currently stored to have some balance
        // The PENALISATION here will be a number between -50 and 0
        int n_stype_stats = interaction_history.getNStypeStats();
        if (n_stype_stats == 0){
            return 0;
        }

        return (-50/interaction_history.getNStypeStats());
    }

    @Override
    public void recordDataOnFile(Game played, String fileName, int randomSeed, int[] recordIds) {
        endOfGameProcess(played.getObservation());
        interaction_history.printStatsResult();

        // Data:
        // gameId controllerId randomSeed winnerId score gameTicks totalIntChecked nStypeStats
        // 1 interType n_win n_los score_diff n_totalChecked winEstimation scoreEstimation
        // ....
        // n_stypestats stype interType n_win n_los score_diff n_totalChecked winEstimation scoreEstimation
        //
        // being interType:
        // 0 -> collision
        // 1 -> action

        int gameId = recordIds[0];

        try {
            if(fileName != null && !fileName.equals("")) {
                writer = new BufferedWriter(new FileWriter(new File(fileName), true));
                writer.write(gameId + " " + recordIds[1] + " " + randomSeed +
                        " " + (played.getWinner() == Types.WINNER.PLAYER_WINS ? 1 : 0) +
                        " " + played.getScore() + " " + played.getGameTick() +
                        " " + interaction_history.getNTotalStatesChecked() + " " + interaction_history.getNStypeStats() +
                        "\n");

                interaction_history.printStatsResultsInFile(writer);

                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawInScreen(Graphics2D g) {

    }
}
