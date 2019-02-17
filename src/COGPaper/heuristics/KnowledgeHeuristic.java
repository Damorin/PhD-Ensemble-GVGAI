package COGPaper.heuristics;

import core.game.*;
import core.game.Event;
import tools.Vector2d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Created by Cristina on 29/03/2017.
 */
public abstract class KnowledgeHeuristic extends StateHeuristic  {
    protected int block_size;
    protected int grid_width;
    protected int grid_height;
    protected int current_gametick;
    protected ArrayList<Integer> sprites_from_avatar_acknowledge;

    protected InteractionHistory interaction_history;

    public KnowledgeHeuristic(StateObservation stateObs){
        block_size = stateObs.getBlockSize();
        Dimension grid_dimension = stateObs.getWorldDimension();

        grid_width = grid_dimension.width / block_size;
        grid_height = grid_dimension.height / block_size;

        current_gametick = stateObs.getGameTick();

        // The interaction history is initialised
        interaction_history = new InteractionHistory(current_gametick);

        last_visited_stateObs = stateObs.copy();
        last_stateObs = last_visited_stateObs;

        initHeuristicAccumulation();
    }

     /* ***********************************************************************************************
     * ACKNOWLEDGE INFORMATION
     * ****************************************************************************************************/

    /**
     * updateSpriteAcknowledge
     * Adds the from-avatar sprites discovered in the information to the acknowledge list
     * */
    protected boolean updateSpriteAcknowledge(StateObservation stateObs) {
        boolean ack_updated = false;

        // From avatar sprites
        if (addObservationToAcknowledgeSprites(stateObs.getFromAvatarSpritesPositions(), sprites_from_avatar_acknowledge)){
            ack_updated = true;
        }

        return ack_updated;
    }

    /**
     * addObservationToAcknowledgeSprites
     * Checks all the observations available and updates the acknowledge list if a new sprite is found
     * @param observations
     * @param acknowledge_list
     * @return true if the acknowledge information has been updated
     */
    protected boolean addObservationToAcknowledgeSprites(ArrayList<Observation>[] observations, ArrayList<Integer> acknowledge_list){
        boolean new_ack_found = false;
        int stype;

        if (observations == null){
            return false;
        }

        for (int i=0; i < observations.length; i++){
            if (observations[i].size() > 0) {
                stype = observations[i].get(0).itype;
                if (!acknowledge_list.contains(stype)) {
                    acknowledge_list.add(stype);
                    new_ack_found = true;
                }
            }
        }

        return new_ack_found;
    }

    /* ***********************************************************************************************
     * INTERACTION INFORMATION
     * ****************************************************************************************************/

    private boolean updateInteractionHistory(Event event, StateObservation stateObs, StateObservation last_stateObs){
        boolean inthistory_updated = false;

        int avatar_stype =  last_stateObs.getAvatarType();
        if (event.activeTypeId == avatar_stype || event.passiveTypeId == avatar_stype){
            // Avatar collision
            inthistory_updated = interaction_history.updateCollitionEventHistory(event, avatar_stype, stateObs, last_stateObs);
        } else if (sprites_from_avatar_acknowledge.contains(event.activeTypeId) || sprites_from_avatar_acknowledge.contains(event.passiveTypeId)) {
            // Actioned onto
            inthistory_updated = interaction_history.updateActionOntoEventHistory(event, sprites_from_avatar_acknowledge, stateObs, last_stateObs);
        }

        //printEvent(event);

        return inthistory_updated;
    }

    protected boolean processLastEventsFromState(StateObservation stateObs, StateObservation last_stateObs){
        TreeSet<Event> events_history = stateObs.getEventsHistory();
        Iterator itr = events_history.descendingSet().iterator();

        /* For each of the events that happened during the last_gametick, the data is processed and updated */
        boolean inthistory_updated = false;
        while (itr.hasNext()){
            Event event = (Event) itr.next();
            if (event.gameStep != last_stateObs.getGameTick()){
                break;
            }
            if (updateInteractionHistory(event, stateObs, last_stateObs)){
                inthistory_updated = true;
            }
        }

        return inthistory_updated;
    }

    protected ArrayList<Event> getLastGametickEvents(StateObservation stateObs, StateObservation last_stateObs) {
        TreeSet<Event> events_history = stateObs.getEventsHistory();
        Iterator itr = events_history.descendingSet().iterator();

        ArrayList<Event> last_gametick_events = new ArrayList<>();

        while (itr.hasNext()){
            Event event = (Event) itr.next();
            if (event.gameStep != last_stateObs.getGameTick()){
                break;
            }

            last_gametick_events.add(event.copy());
        }

        return last_gametick_events;

    }

    /* ***********************************************************************************************
     * HEURISTIC FUNCTIONS
     * ****************************************************************************************************/

    /**
     * Checks if the provided position is out of bounds the map
     * @param position
     * @return true if it is out of bounds, false if not
     */
    protected boolean isOutOfBounds(Vector2d position){
        int x = (int)position.x / block_size;
        int y = (int)position.y / block_size;

        if ((x < 0) || (x >= grid_width) || (y < 0) || (y >= grid_height)){
            return true;
        }

        return false;
    }

    /**
     * isNewStypeInteraction
     * Checks if one of the last events considered is a new interaction with a stype
     * Interactions are collisions from the avatar or avatar-created sprites with other sprites of the game
     */
    protected boolean isNewStypeInteraction(ArrayList<Event> last_gametick_events, int avatar_stype){
        for (int i=0; i<last_gametick_events.size(); i++){
            Event last_event = last_gametick_events.get(i);
            if ((interaction_history.isNewStypeCollition(last_event, avatar_stype) || (interaction_history.isNewStypeActionedOnto(last_event, sprites_from_avatar_acknowledge)))){
                return true;
            }
        }

        return false;
    }


    @Override
    public void updateHeuristicBasedOnCurrentState(StateObservation stateObs) {
        // For this heuristic is needed to update the memory of the agent

        // In order to set the right game tick where the sprite is acknowledge, it is stored the current state game tick
        current_gametick = stateObs.getGameTick();
//        System.out.println("---------------GAMETICK "+current_gametick+"---------");

        // The different sprites in the current state are added to the 'sprite acknowledgement' of the agent
        updateSpriteAcknowledge(stateObs);

        processLastEventsFromState(stateObs, last_visited_stateObs);

        last_visited_stateObs = stateObs.copy();
        last_stateObs = last_visited_stateObs;
    }

    protected void endOfGameProcess(StateObservation stateObs)
    {
        /* Last updates needed */
        updateHeuristicBasedOnCurrentState(stateObs);
    }

    /* ***********************************************************************************************
     * DEBUG
     * ****************************************************************************************************/

    /**
     * Prints the number of different types of sprites available in the "positions" array.
     * Between brackets, the number of observations of each type.
     * @param positions array with observations.
     * @param str identifier to print
     */
    protected void printDebug(ArrayList<Observation>[] positions, String str)
    {
        if(positions != null){
            System.out.print(str + ":" + positions.length + "(");
            for (int i = 0; i < positions.length; i++) {
                System.out.print(positions[i].get(0).itype + ",");
            }
            System.out.print("); ");
        }else System.out.print(str + ": 0; ");
    }

    private void printEvent(Event event){
        System.out.println(event.gameStep+"-> sp1: "+event.activeTypeId + " sp2: "+event.passiveTypeId + " at "+event.position);
    }

    protected void printEvents(ArrayList<Event> events){
        for (int i=0; i < events.size(); i++){
            printEvent(events.get(i));
        }
    }
}
