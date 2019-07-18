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
 * Created by Cristina on 27/03/2017.
 */
public class KnowledgeDiscoveryHeuristic extends KnowledgeHeuristic {
    private ArrayList<Integer> sprites_acknowledge;
    private int last_spriteAcknowledge_tick;

    /**
     * When the class is instantiated it is needed to initialise the data
     */
    public KnowledgeDiscoveryHeuristic(StateObservation stateObs) {
        super(stateObs);

        // Acknowledge is initialised
        sprites_from_avatar_acknowledge = new ArrayList<>();
        sprites_acknowledge = new ArrayList<>();
        updateSpriteAcknowledge(stateObs);

        interaction_history.setUseCuriosity(true);
    }

    /* ***********************************************************************************************
     * ACKNOWLEDGE INFORMATION
     * ****************************************************************************************************/

    /**
     * updateSpriteAcknowledge
     * Checks the observations of all different categories (NPC, Immovable, Movable, Resources, Portals)
     * to add every sprite available to the the general sprite acknowledge list
     */
    @Override
    protected boolean updateSpriteAcknowledge(StateObservation stateObs) {
        boolean ack_updated = false;

        // NPC sprites
        if (addObservationToAcknowledgeSprites(stateObs.getNPCPositions(), sprites_acknowledge)) {
            ack_updated = true;
        }
        // Fixed sprites
        if (addObservationToAcknowledgeSprites(stateObs.getImmovablePositions(), sprites_acknowledge)) {
            ack_updated = true;
        }
        // Movable sprites
        if (addObservationToAcknowledgeSprites(stateObs.getMovablePositions(), sprites_acknowledge)) {
            ack_updated = true;
        }
        // Resources sprites
        if (addObservationToAcknowledgeSprites(stateObs.getResourcesPositions(), sprites_acknowledge)) {
            ack_updated = true;
        }
        // Portal sprites
        if (addObservationToAcknowledgeSprites(stateObs.getPortalsPositions(), sprites_acknowledge)) {
            ack_updated = true;
        }

        // From avatar sprites
        if (addObservationToAcknowledgeSprites(stateObs.getFromAvatarSpritesPositions(), sprites_from_avatar_acknowledge)) {
            ack_updated = true;
        }

        if (ack_updated) {
            last_spriteAcknowledge_tick = current_gametick;
        }

        return ack_updated;
    }

    /**
     * getNSpritesAcknowledge
     * Returns the number of sprites acknowledge (NOT considering those related with the avatar!)
     * The sprites have been stored in the sprites_acknowledge list during the game
     *
     * @return number of non-avatar-related sprites acknowledge
     */
    private int getNSpritesAcknowledge() {
        return sprites_acknowledge.size();
    }

    /**
     * getNSpritesFromAvatarAcknowledge
     * Returns the number of sprites created by the avatar acknowledge
     * The sprites have been stored in the sprites_from_avatar_acknowledge list during the game
     *
     * @return number of sprites created by the avatar acknowledge
     */
    private int getNSpritesFromAvatarAcknowledge() {
        return sprites_from_avatar_acknowledge.size();
    }

    /**
     * getTotalNSpritesAcknowledge
     * Returns the total number of sprites acknowledge.
     * The total number of sprites acknowledge consider both the ones generated and not from the avatar
     *
     * @return number of total sprites acknowledge
     */
    private int getTotalNSpritesAcknowledge() {
        return getNSpritesAcknowledge() + getNSpritesFromAvatarAcknowledge();
    }

    /* ***********************************************************************************************
     * HEURISTIC FUNCTIONS
     * ****************************************************************************************************/

    /**
     * isNewCollisionCuriosity
     * Checks if one of the last events considered should be included to the 'curiosity' collision
     * The curiosity stores collisions with sprites in different positions of the map
     */
    private boolean isNewCollisionCuriosity(ArrayList<Event> last_gametick_events, int avatar_stype) {
        for (int i = 0; i < last_gametick_events.size(); i++) {
            Event last_event = last_gametick_events.get(i);
            if (interaction_history.isNewCollitionCuriosity(last_event, avatar_stype)) {
                return true;
            }
        }

        return false;
    }

    /**
     * isNewActionCuriosity
     * Checks if one of the last events considered should be included to the 'curiosity' action-onto list
     * The action curiosity stores the sprites from avatar collisions with other sprites in different positions of the map
     */
    private boolean isNewActionCuriosity(ArrayList<Event> last_gametick_events, int avatar_stype) {
        for (int i = 0; i < last_gametick_events.size(); i++) {
            Event last_event = last_gametick_events.get(i);
            if (interaction_history.isNewActionCuriosity(last_event, sprites_from_avatar_acknowledge)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public double evaluateState(StateObservation stateObs) {
        boolean gameOver = stateObs.isGameOver();
        Types.WINNER win = stateObs.getGameWinner();

        // The different sprites in the evaluated state are added to the 'sprite acknowledgement' of the agent
        boolean ack_update = updateSpriteAcknowledge(stateObs);

        // If it is game over and it has lost, it's bad
        if (gameOver && win == Types.WINNER.PLAYER_LOSES) {
            return HUGE_NEGATIVE;
        }

        // This heuristic has been updated to always reward winning
        if (gameOver && win == Types.WINNER.PLAYER_WINS) {
            return HUGE_POSITIVE;
        }

        Vector2d currentPosition = stateObs.getAvatarPosition();

        if (isOutOfBounds(currentPosition)) {
            // If the new position is out of bounds then dont go there
            return HUGE_NEGATIVE;
        }

        /* Heuristic to reward discovery and interaction
        * 1) A new sprite appears
        * 2) Interact with different sprites. This interaction is considered either the avatar colliding with a sprite or one of the sprites from avatar
        * colliding with other sprites
        * 3) No new elements to discover or interact with, it is rewarded interactions in different places even if it is with a sprite already interacted with
        * */

        // New sprite has appeared, this is GOOD
        if (ack_update) {
            return (HUGE_POSITIVE / 2);
        }

        ArrayList<Event> last_gametick_events = getLastGametickEvents(stateObs, last_stateObs);

        if (!last_gametick_events.isEmpty()) {
            if (isNewStypeInteraction(last_gametick_events, last_stateObs.getAvatarType())) {
                return 1000;
            }

            if (isNewCollisionCuriosity(last_gametick_events, last_stateObs.getAvatarType())) {
                return 50;
            }

            if (isNewActionCuriosity(last_gametick_events, last_stateObs.getAvatarType())) {
                return 25;
            }

        }

        return -25;
    }

    @Override
    public void recordDataOnFile(Game played, String fileName, int randomSeed, int[] recordIds) {
        endOfGameProcess(played.getObservation());

        printStats(played.getObservation());

        // Data:
        // gameId controllerId randomSeed winnerId score gameTicks
        // nSpritesAck nSpritesFromAvatarAck nTotalAck gameticksAck
        // nCollided nActioned nTotalInteracions gameTicksColl gameTicksAct gameTicksInt
        // curiosity gameTicksCur curiosityAction gameTicksCurAction

        int gameId = recordIds[0];

        try {
            if (fileName != null && !fileName.equals("")) {
                writer = new BufferedWriter(new FileWriter(new File(fileName), true));
                writer.write(gameId + " " + recordIds[1] + " " + randomSeed +
                        " " + (played.getWinner() == Types.WINNER.PLAYER_WINS ? 1 : 0) +
                        " " + played.getScore() + " " + played.getGameTick() +
                        " " + getNSpritesAcknowledge() + " " + getNSpritesFromAvatarAcknowledge() + " " + getTotalNSpritesAcknowledge() + " " + last_spriteAcknowledge_tick +
                        " " + interaction_history.getNStypesCollidedWith() + " " + interaction_history.getNStypesActionedOnto() + " " + interaction_history.getNInteraction() +
                        " " + interaction_history.getLastNewCollitionTick() + " " + interaction_history.getLastNewActionontoTick() + " " + interaction_history.getLastNewInteractonTick() +
                        " " + interaction_history.getNCuriosity() + " " + interaction_history.getLastCuriosityTick() +
                        " " + interaction_history.getNCuriosityAction() + " " + interaction_history.getLastCuriosityActionTick() +
                        "\n");

                //printExplorationMatrix();

                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawInScreen(Graphics2D g) {
    }

    protected void printStats(StateObservation stateObs) {
        System.out.println("--- GAME FINISHED at " + stateObs.getGameTick() + " ----------------- ");
        System.out.println("Last tick acknowledge: " + last_spriteAcknowledge_tick);
        System.out.println("Ack :" + sprites_acknowledge);
        System.out.println("Ack Sprites from: " + sprites_from_avatar_acknowledge);
        System.out.println("sprites ack: " + getNSpritesAcknowledge());
        System.out.println("from-avatar ack: " + getNSpritesFromAvatarAcknowledge());
        System.out.println("Total ack: " + getTotalNSpritesAcknowledge());
        System.out.println("Last new collision tick: " + interaction_history.getLastNewCollitionTick());
        System.out.println("Last new action-onto tick: " + interaction_history.getLastNewActionontoTick());
        System.out.println("Last new interaction tick: " + interaction_history.getLastNewInteractonTick());
        System.out.println("Collided with: " + interaction_history.getStypesCollidedWith());
        System.out.println("Actioned onto: " + interaction_history.getStypesActionedOnto());
        System.out.println("Last curiosity tick: " + interaction_history.getLastCuriosityTick());
        System.out.println("Curiosity: " + interaction_history.getCuriosityMap());
        System.out.println("Curiosity: " + interaction_history.getNCuriosity());
        System.out.println("last Curiosity action tick: " + interaction_history.getLastCuriosityActionTick());
        System.out.println("Curiosity action: " + interaction_history.getCuriosityActionMap());
        System.out.println("Curiosity action: " + interaction_history.getNCuriosityAction());
        System.out.println("---------------------------------------------------------------------");
    }
}
