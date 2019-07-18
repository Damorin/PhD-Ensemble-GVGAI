package Damorin.heuristics;

import core.game.Event;
import core.game.StateObservation;
import ontology.Types;
import tools.Vector2d;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Cristina on 29/03/2017.
 */
public class InteractionHistory {
    private ArrayList<Integer> stypes_collided_with;
    private ArrayList<Integer> stypes_actioned_onto;
    private HashMap<Integer, ArrayList<Vector2d>> stypes_curiosity_map;
    private HashMap<Integer, ArrayList<Vector2d>> stypes_curiosity_action_map;
    private HashMap<Integer, SpriteStats> stypes_stats_collision;
    private HashMap<Integer, SpriteStats> stypes_stats_actions;
    private int last_new_collition_tick;
    private int last_new_actiononto_tick;
    private int last_new_interacton_tick;
    private int last_curiosity_tick;
    private int last_curiosity_action_tick;
    private int stats_total_states_checked;

    private boolean use_curiosity;
    private boolean use_stats;

    private int COLLISION = 0;
    private int ACTIONONTO = 1;

    private class SpriteStats {
        private int n_win_encountered;
        private int n_lose_encountered;
        private double score_diff_sum;
        private int n_states_checked;

        SpriteStats(){
            this.n_win_encountered = 0;
            this.n_lose_encountered = 0;
            this.score_diff_sum = 0;
            this.n_states_checked = 0;
        }

        private void addWinInformation(int win){
            if (win == 1){
                this.n_win_encountered++;
            } else if (win == -1){
                this.n_lose_encountered++;
            }
        }

        private void addScoreInformation(double score){
            this.score_diff_sum+=score;
        }

        void addStateInfoToStats(int win, double score){
            this.addWinInformation(win);
            this.addScoreInformation(score);
            this.n_states_checked++;
        }

        int getNTimesChecked(){
            return this.n_states_checked;
        }

        double getWinEstimation(){
            if (this.n_states_checked > 0){
                return ((double)(this.n_win_encountered - this.n_lose_encountered))/n_states_checked;
            }
            return 0;
        }

        double getScoreEstimation(){
            if (this.n_states_checked > 0){
                return this.score_diff_sum/this.n_states_checked;
            }
            return 0;
        }

        String statsStringRow(){
            return this.n_win_encountered + " " + this.n_lose_encountered + " " + this.score_diff_sum + " " + this.n_states_checked + " " + this.getWinEstimation() + " " + this.getScoreEstimation();
        }

        void printStats(){
            System.out.println("n_win_encountered: "+this.n_win_encountered);
            System.out.println("n_lose_encountered: "+this.n_lose_encountered);
            System.out.println("score_diff_encountered: "+this.score_diff_sum);
            System.out.println("n_states_checked: "+this.n_states_checked);
            System.out.println("win estimation: "+this.getWinEstimation());
            System.out.println("score estimation: "+this.getScoreEstimation());
        }
    }

    InteractionHistory(int current_gametick){
        this.stypes_collided_with = new ArrayList<>();
        this.stypes_actioned_onto = new ArrayList<>();
        this.stypes_curiosity_map = new HashMap<>();
        this.stypes_curiosity_action_map = new HashMap<>();
        this.stypes_stats_collision = new HashMap<>();
        this.stypes_stats_actions = new HashMap<>();
        this.stats_total_states_checked = 0;

        // Game ticks are initialised
        this.last_new_collition_tick = current_gametick;
        this.last_new_actiononto_tick = current_gametick;
        this.last_new_interacton_tick = current_gametick;
        this.last_curiosity_tick = current_gametick;
        this.last_curiosity_action_tick = current_gametick;

        // Approach to be used
        this.use_curiosity = false;
        this.use_stats = false;
    }

    void setUseCuriosity(boolean use_curiosity){
        this.use_curiosity = use_curiosity;
    }

    void setUseStats(boolean use_stats){
        this.use_stats = use_stats;
    }

    private int getStypeActionedOnto(Event event, ArrayList<Integer> avatar_from_stypes){
        if(avatar_from_stypes.contains(event.activeTypeId)){
            return event.passiveTypeId;
        } else if(avatar_from_stypes.contains(event.passiveTypeId)){
            return event.activeTypeId;
        }

        return -1;
    }

    private int getStypeCollidedWith(Event event, int avatar_stype){
        if(event.activeTypeId == avatar_stype){
            return event.passiveTypeId;
        } else if(event.passiveTypeId == avatar_stype){
            return event.activeTypeId;
        }

        return -1;
    }

    private void updateCollisionCuriosity(Event event, int stype_collided_with){
        ArrayList<Vector2d> stype_collision_positions = new ArrayList<>();
        Vector2d collision_position = event.position.copy();

        if (this.stypes_curiosity_map.containsKey(stype_collided_with)){
            stype_collision_positions = stypes_curiosity_map.get(stype_collided_with);
            if (!stype_collision_positions.contains(collision_position)){
                stype_collision_positions.add(collision_position);
                this.stypes_curiosity_map.put(stype_collided_with, stype_collision_positions);
                this.last_curiosity_tick = event.gameStep;
            }
        }else{
            stype_collision_positions.add(collision_position);
            this.stypes_curiosity_map.put(stype_collided_with, stype_collision_positions);
            this.last_curiosity_tick = event.gameStep;
        }
    }

    private int getWinValue(StateObservation stateObs){
        int win = 0;
        Types.WINNER winner = stateObs.getGameWinner();
        if (stateObs.isGameOver()){
            if(winner == Types.WINNER.PLAYER_LOSES){
                win = -1;
            }
            if(winner == Types.WINNER.PLAYER_WINS){
                win = 1;
            }
        }
        return win;
    }

    boolean updateCollitionEventHistory(Event event, int avatar_stype, StateObservation stateObs, StateObservation last_stateObs){
        int stype_collided_with = getStypeCollidedWith(event, avatar_stype);

        boolean updated = false;
        if (stype_collided_with >= 0){
            if (!this.stypes_collided_with.contains(stype_collided_with)){
                this.stypes_collided_with.add(stype_collided_with);
                this.last_new_collition_tick = event.gameStep;
                this.last_new_interacton_tick = event.gameStep;
                updated = true;
            }

            if (this.use_curiosity){
                this.updateCollisionCuriosity(event, stype_collided_with);
            }

            if (this.use_stats) {
                this.updateInteractionSpriteStatsKnowledge(stype_collided_with, stypes_stats_collision, stateObs, last_stateObs);
            }
        }

        return updated;
    }

    private void updateActionOntoCuriosity(Event event, int stype_actioned_onto){
        ArrayList<Vector2d> stype_actionedonto_positions = new ArrayList<>();
        Vector2d actionedonto_position = event.position.copy();

        if (this.stypes_curiosity_action_map.containsKey(stype_actioned_onto)){
            stype_actionedonto_positions = stypes_curiosity_action_map.get(stype_actioned_onto);
            if (!stype_actionedonto_positions.contains(actionedonto_position)){
                stype_actionedonto_positions.add(actionedonto_position);
                this.stypes_curiosity_action_map.put(stype_actioned_onto, stype_actionedonto_positions);
                this.last_curiosity_action_tick = event.gameStep;
            }
        }else{
            stype_actionedonto_positions.add(actionedonto_position);
            this.stypes_curiosity_action_map.put(stype_actioned_onto, stype_actionedonto_positions);
            this.last_curiosity_action_tick = event.gameStep;
        }
    }

    private void updateInteractionSpriteStatsKnowledge(int stype, HashMap<Integer, SpriteStats> stypes_stats, StateObservation stateObs, StateObservation last_stateObs){
        // Checks if it is a winning/losing move
        int win = getWinValue(stateObs);

        // Checks difference of score
        double score_diff = stateObs.getGameScore() - last_stateObs.getGameScore();

        if (stypes_stats.containsKey(stype)){
            // New stats added to the sprite
            SpriteStats stype_stats = stypes_stats.get(stype);
            stype_stats.addStateInfoToStats(win, score_diff);
        }else{
            // First time there is an action onto this sprite, needs to be initialised
            SpriteStats stype_stats = new SpriteStats();
            stype_stats.addStateInfoToStats(win, score_diff);
            stypes_stats.put(stype, stype_stats);
        }

        this.stats_total_states_checked++;
    }

    void updateSpritesStatsKnowledge(Event event, StateObservation stateObs, StateObservation last_stateObs, int avatar_stype, ArrayList<Integer> sprites_from_avatar){
        if (event.activeTypeId == avatar_stype || event.passiveTypeId == avatar_stype){
            // Avatar collision stats
            int stype_collided_with = this.getStypeCollidedWith(event, avatar_stype);
            this.updateInteractionSpriteStatsKnowledge(stype_collided_with, stypes_stats_collision, stateObs, last_stateObs);
        } else if (sprites_from_avatar.contains(event.activeTypeId) || sprites_from_avatar.contains(event.passiveTypeId)) {
            // Actioned onto stats
            int stype_actioned_onto = this.getStypeActionedOnto(event, sprites_from_avatar);
            this.updateInteractionSpriteStatsKnowledge(stype_actioned_onto, stypes_stats_actions, stateObs, last_stateObs);
        }
    }

    boolean updateActionOntoEventHistory(Event event, ArrayList<Integer> avatar_from_stypes, StateObservation stateObs, StateObservation last_StateObs){
        int stype_actioned_onto = this.getStypeActionedOnto(event, avatar_from_stypes);

        boolean updated = false;
        if (stype_actioned_onto >= 0){
            if (!this.stypes_actioned_onto.contains(stype_actioned_onto)){
                this.stypes_actioned_onto.add(stype_actioned_onto);
                this.last_new_actiononto_tick = event.gameStep;
                this.last_new_interacton_tick = event.gameStep;
                updated = true;
            }

            if (this.use_curiosity) {
                this.updateActionOntoCuriosity(event, stype_actioned_onto);
            }

            if (this.use_stats) {
                this.updateInteractionSpriteStatsKnowledge(stype_actioned_onto, stypes_stats_actions, stateObs, last_StateObs);
            }
        }

        return updated;
    }

    boolean isNewStypeCollition(Event event, int avatar_stype){
        int stype_collided_with = this.getStypeCollidedWith(event, avatar_stype);

        if ((stype_collided_with >= 0) && (!this.stypes_collided_with.contains(stype_collided_with))){
            return true;
        }

        return false;
    }

    boolean isNewCollitionCuriosity(Event event, int avatar_stype){
        int stype_collided_with = this.getStypeCollidedWith(event, avatar_stype);

        if (stype_collided_with >= 0){
            ArrayList<Vector2d> stype_collision_positions = new ArrayList<>();

            if (this.stypes_curiosity_map.containsKey(stype_collided_with)){
                stype_collision_positions = this.stypes_curiosity_map.get(stype_collided_with);
                if (!stype_collision_positions.contains(event.position)){
                    return true;
                }
            }else{
                return true;
            }
        }

        return false;
    }

    boolean isNewActionCuriosity(Event event, ArrayList<Integer> avatar_from_stypes){
        int stype_actioned_onto = this.getStypeActionedOnto(event, avatar_from_stypes);

        if (stype_actioned_onto >= 0){
            ArrayList<Vector2d> stype_actionedonto_positions = new ArrayList<>();

            if (this.stypes_curiosity_action_map.containsKey(stype_actioned_onto)){
                stype_actionedonto_positions = this.stypes_curiosity_action_map.get(stype_actioned_onto);
                if (!stype_actionedonto_positions.contains(event.position)){
                    return true;
                }
            }else{
                return true;
            }
        }

        return false;
    }

    boolean isNewStypeActionedOnto(Event event, ArrayList<Integer> avatar_from_stypes){
        int stype_actioned_onto = this.getStypeActionedOnto(event, avatar_from_stypes);

        if ((stype_actioned_onto >= 0) && (!this.stypes_actioned_onto.contains(stype_actioned_onto))){
            return true;
        }

        return false;
    }

    private int getNStatsStored(int stype, HashMap<Integer, SpriteStats> stypes_stats){
        if (stypes_stats.containsKey(stype)){
            SpriteStats stype_stats = stypes_stats.get(stype);
            return stype_stats.getNTimesChecked();
        }

        return 0;
    }

    int getNTimesInteractionChecked(Event event, int avatar_stype, ArrayList<Integer> sprites_from_avatar){
        int stype_interacted;

        if (event.activeTypeId == avatar_stype || event.passiveTypeId == avatar_stype){
            // Avatar collision stats
            stype_interacted = this.getStypeCollidedWith(event, avatar_stype);
            return this.getNStatsStored(stype_interacted, this.stypes_stats_collision);
        } else if (sprites_from_avatar.contains(event.activeTypeId) || sprites_from_avatar.contains(event.passiveTypeId)) {
            // Actioned onto stats
            stype_interacted = this.getStypeActionedOnto(event, sprites_from_avatar);
            return this.getNStatsStored(stype_interacted, this.stypes_stats_actions);
        }

        // It should never reach here
        return -1;
    }

    int getNTotalStatesChecked(){
        return this.stats_total_states_checked;
    }

    ArrayList<Integer> getStypesCollidedWith(){
        return this.stypes_collided_with;
    }

    ArrayList<Integer> getStypesActionedOnto(){
        return this.stypes_actioned_onto;
    }

    HashMap<Integer, ArrayList<Vector2d>> getCuriosityMap(){
        return this.stypes_curiosity_map;
    }

    HashMap<Integer, ArrayList<Vector2d>> getCuriosityActionMap(){
        return this.stypes_curiosity_action_map;
    }

    int getLastNewCollitionTick(){
        return this.last_new_collition_tick;
    }

    int getLastNewActionontoTick(){
        return this.last_new_actiononto_tick;
    }

    int getLastNewInteractonTick(){
        return this.last_new_interacton_tick;
    }

    int getLastCuriosityTick(){
        return this.last_curiosity_tick;
    }

    int getLastCuriosityActionTick(){
        return this.last_curiosity_action_tick;
    }

    int getNStypesCollidedWith() {
        return this.stypes_collided_with.size();
    }

    int getNStypesActionedOnto() {
        return this.stypes_actioned_onto.size();
    }

    int getNInteraction(){
        return getNStypesCollidedWith() + getNStypesActionedOnto();
    }

    int getNCuriosity(){
        int total_curiosity = 0;
        for (ArrayList<Vector2d> position_list : this.stypes_curiosity_map.values()) {
            total_curiosity += position_list.size();
        }

        return total_curiosity;
    }

    int getNCuriosityAction(){
        int total_curiosity_action = 0;
        for (ArrayList<Vector2d> position_list : this.stypes_curiosity_action_map.values()) {
            total_curiosity_action += position_list.size();
        }

        return total_curiosity_action;
    }

    int getNStypeStats() {
        return stypes_stats_collision.size() + stypes_stats_actions.size();
    }

    void printStypeStatsRow(int counter, int interaction_type, BufferedWriter writer, Map.Entry<Integer, SpriteStats> stype_stats) throws IOException{
        int stype = stype_stats.getKey();
        SpriteStats stats = stype_stats.getValue();

        //n_stypestats stype interType n_win n_los score_diff n_totalChecked winEstimation scoreEstimation
        writer.write(counter + " " + stype + " " + interaction_type + " " + stats.statsStringRow() + "\n");
    }

    void printStatsResultsInFile(BufferedWriter writer) throws IOException{
        int counter = 0;
        for (Map.Entry<Integer, SpriteStats> stype_stats : this.stypes_stats_collision.entrySet()) {
            this.printStypeStatsRow(counter, this.COLLISION, writer, stype_stats);
            counter++;
        }

        for (Map.Entry<Integer, SpriteStats> stype_stats : this.stypes_stats_actions.entrySet()) {
            this.printStypeStatsRow(counter, this.ACTIONONTO, writer, stype_stats);
            counter++;
        }
    }

    void printStatsResult(){

        for (Map.Entry<Integer, SpriteStats> stype_stats : this.stypes_stats_collision.entrySet()) {
            int stype = stype_stats.getKey();
            SpriteStats stats = stype_stats.getValue();

            System.out.println();
            System.out.println("-------------Collision stats for "+stype+"-------------------");
            stats.printStats();
            System.out.println("-------------------------------------------------------");
        }

        for (Map.Entry<Integer, SpriteStats> stype_stats : stypes_stats_actions.entrySet()) {
            int stype = stype_stats.getKey();
            SpriteStats stats = stype_stats.getValue();

            System.out.println();
            System.out.println("-------------Actions stats for "+stype+"-------------------");
            stats.printStats();
            System.out.println("-------------------------------------------------------");
        }
    }
}
