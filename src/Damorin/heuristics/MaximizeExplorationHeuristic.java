package Damorin.heuristics;

import core.game.Game;
import core.game.StateObservation;
import ontology.Types;
import tools.Vector2d;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by kisenshi on 06/03/17.
 */
public class MaximizeExplorationHeuristic extends StateHeuristic {

    int block_size;
    int grid_width;
    int grid_height;
    boolean exploration_matrix[][];
    Vector2d last_position;
    int last_discovered_tick = 0;
    int games_lvl_navigation_sizes[] = new int[]{
            30,     //"aliens",
            9,      //"bait",
            206,    //"butterflies",
            322,    //"camelRace",
            135,    //"chase",
            184,    //"chopper",
            333,    //"crossfire",
            405,    //"digdug",
            74,     //"escape",
            79,     //"hungrybirds",
            187,    //"infection",
            243,    //"intersection",
            222,    //"lemmings",
            242,    //"missilecommand",
            15,     //"modality",
            310,    //"plaqueattack",
            266,    //"roguelike",
            189,    //"seaquest",
            121,    //"survivezombies",
            50,     //"waitforbreakfast"
    };

    /**
     * When the class is instantiated it is needed to initialise
     * the exploration matrix
     */
    public MaximizeExplorationHeuristic(StateObservation stateObs) {
        block_size = stateObs.getBlockSize();
        Dimension grid_dimension = stateObs.getWorldDimension();

        grid_width = grid_dimension.width / block_size;
        grid_height = grid_dimension.height / block_size;

        exploration_matrix = new boolean[grid_width][grid_height];

        Vector2d initialPosition = stateObs.getAvatarPosition();

        markNewPositionAsVisited(initialPosition);
        last_discovered_tick = stateObs.getGameTick();

        initHeuristicAccumulation();
    }

    private int getMapSize() {
        return grid_width * grid_height;
    }

    /**
     * Checks if the provided position is out of bounds the map
     *
     * @param position
     * @return
     */
    private boolean isOutOfBounds(Vector2d position) {
        int x = (int) position.x / block_size;
        int y = (int) position.y / block_size;

        if ((x < 0) || (x >= grid_width) || (y < 0) || (y >= grid_height)) {
            return true;
        }

        return false;
    }

    /**
     * Marks the position as visited in the exploration_matrix
     * The position is provided as a Vector2d object so it is needed to
     * calculate the valid coordinates to be considered for the matrix
     * It would be used the block_size int set when initialised
     *
     * @param position The position as a Vector2d object
     */
    private void markNewPositionAsVisited(Vector2d position) {
        if (isOutOfBounds(position)) {
            return;
        }

        int x = (int) position.x / block_size;
        int y = (int) position.y / block_size;

        //System.out.println("Marking ("+x+" , "+y+") as VISITED");

        exploration_matrix[x][y] = true;
    }

    /**
     * Checks if the position has already been visited. As it is provided as Vector2d objects,
     * it is needed to convert it to valid coordinates to be considered for the matrix
     *
     * @param position The position to be checked, as a Vector2d objects
     * @return true or false depending if the position has already been visited or not
     */
    private boolean hasBeenBefore(Vector2d position) {
        if (isOutOfBounds(position)) {
            return false;
        }

        int x = (int) position.x / block_size;
        int y = (int) position.y / block_size;

        //System.out.println("Been before to ("+x+" , "+y+")? "+exploration_matrix[x][y]);

        return exploration_matrix[x][y];
    }

    /**
     * Returns the percentage of the map explored in total
     *
     * @return
     */
    private double getNSpotsExplored() {
        double explored = 0;

        for (int i = 0; i < exploration_matrix.length; i++) {
            for (int j = 0; j < exploration_matrix[i].length; j++) {
                if (exploration_matrix[i][j]) {
                    explored++;
                }
            }
        }

        return explored;
    }

    /**
     * Evaluates the current state taking into consideration the exploration_matrix, which contains
     * the positions already visited by the avatar. It is prioritize visiting those positions the agent has not been before
     *
     * @param stateObs
     * @return
     */
    public double evaluateState(StateObservation stateObs) {
        boolean gameOver = stateObs.isGameOver();
        Types.WINNER win = stateObs.getGameWinner();

//        System.out.println("MaximizeExploration called!");

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

        if (!hasBeenBefore(currentPosition)) {
            // If it hasnt been before, it is rewarded
            return 100;
        }

        // If it has been before, it is penalised
        if (currentPosition.equals(last_position)) {
            // As it is tried to reward exploration, it is penalised more if it is the last position visited
            //System.out.println("Last position visited");
            return -50;
        }

        return -25;
    }

    /**
     * For this heuristic, it is needed to update the exploration_matrix to
     * mark the current position of the avatar as visited and be taken into consideration in the future
     *
     * @param stateObs
     */
    public void updateHeuristicBasedOnCurrentState(StateObservation stateObs) {
        // For this heuristic is needed to update the exploration_matrix to mark
        Vector2d currentPosition = stateObs.getAvatarPosition();

        //System.out.println();
        //System.out.println("-------------------------------");

        last_position = currentPosition.copy();
        if (!hasBeenBefore(currentPosition)) {
            markNewPositionAsVisited(currentPosition);
            last_discovered_tick = stateObs.getGameTick();
            //printDebugMatrix();
        }
    }

    @Override
    public void recordDataOnFile(Game played, String fileName, int randomSeed, int recordIds[]) {
        double explored = getNSpotsExplored();

        // Data:
        // gameId controllerId randomSeed winnerId score gameTicks mapSize nExplored navigationSize percentageExplored lastDiscoveredTick
        int gameId = recordIds[0];
        int navigation_size = games_lvl_navigation_sizes[gameId];

        try {
            if (fileName != null && !fileName.equals("")) {
                writer = new BufferedWriter(new FileWriter(new File(fileName), true));
                writer.write(gameId + " " + recordIds[1] + " " + randomSeed +
                        " " + (played.getWinner() == Types.WINNER.PLAYER_WINS ? 1 : 0) +
                        " " + played.getScore() + " " + played.getGameTick() +
                        " " + getMapSize() + " " + explored + " " + navigation_size + " " + explored / navigation_size + " " + last_discovered_tick + "\n");

                //printExplorationMatrix();

                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawInScreen(Graphics2D g) {
        Color rectColor = new Color(255, 255, 255, 127);

        for (int i = 0; i < exploration_matrix.length; i++) {
            for (int j = 0; j < exploration_matrix[i].length; j++) {
                if (exploration_matrix[i][j]) {
                    g.setColor(rectColor);
                    g.fillRect(i * block_size, j * block_size, block_size, block_size);
                    g.setColor(Types.WHITE);
                    g.drawRect(i * block_size, j * block_size, block_size, block_size);
                }
            }
        }
    }

    /**
     * DEBUGGING method
     *
     * @throws IOException
     */
    private void printExplorationMatrix() throws IOException {
        for (int i = 0; i < exploration_matrix.length; i++) {
            for (int j = 0; j < exploration_matrix[i].length; j++) {
                if (exploration_matrix[i][j]) {
                    if (writer != null) {
                        writer.write(" X ");
                    } else {
                        System.out.print(" X ");
                    }

                } else {
                    if (writer != null) {
                        writer.write(" - ");
                    } else {
                        System.out.print(" - ");
                    }
                }
            }
            if (writer != null) {
                writer.write("\n");
            } else {
                System.out.println();
            }
        }
    }
}
