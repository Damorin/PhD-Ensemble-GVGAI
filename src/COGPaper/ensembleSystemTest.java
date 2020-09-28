package COGPaper;

import tools.Utils;
import tracks.ArcadeMachine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Test class for testing the Ensemble Decision System Agent.
 * <p>
 * Created by Damorin on 03/03/17.
 *
 * UPDATED 28/09/2020 for new concurrency experiments.
 */
public class ensembleSystemTest {
    public static void main(String[] args) {
        // SETUP

        // PATHS
        String experimentGamesCollection = "examples/eds_heuristic_experiment_games.csv";

        // EXPERIMENT SETUP

        // GAMES
        String[][] games = Utils.readGames(experimentGamesCollection);

        // CONTROLLERS

        List<String> agents = new ArrayList<>();
        agents.add("tracks.singlePlayer.advanced.olets.Agent");
        agents.add("tracks.singlePlayer.advanced.sampleRHEA.Agent");
        agents.add("tracks.singlePlayer.advanced.sampleRS.Agent");
        agents.add("tracks.singlePlayer.advanced.sampleMCTS.Agent");
        agents.add("YOLOBOT.Agent");
//        agents.add("COGPaper.ensemble_system.Agent");
//        agents.add("EDS_AllActions.Agent");
//        agents.add("Concurrent_All_Actions_EDS.Agent");
//        agents.add("Concurrent_SingleAction_EDS.Agent");

        // OTHER SETTINGS
        boolean visuals = false;
        int seed = new Random().nextInt();

        String actionFile = null; //controller+"_actions_" + games[gameIdx] + "_lvl" + levelIdx + "_" + seed + ".txt";
        // TESTS
        String game = games[10][0];
        String gameName = games[10][1];
        String level = game.replace(gameName, gameName + "_lvl" + 0);
//

//        ArcadeMachine.runOneGame(game, level, visuals, agents.get(0), null, seed, 0);


        // EXPERIMENT

        experiment(games, agents, visuals, seed);
    }

    private static void experiment(String[][] games, List<String> agents, boolean visuals, int seed) {
        int experimentRuns = 50;
        String recordActionsFile = null;
        String level = null;

        String resultsRow = ("AgentName, GameName, Level, Win, Score, Time\n");

        try {
            File resultsFile = new File("Concurrency_Experiments_ST_Samples.csv");
            resultsFile.createNewFile();
            FileWriter fileWriter = new FileWriter(resultsFile, true);
            fileWriter.write(resultsRow);

            for (int gameId = 0; gameId < games.length; gameId++) {
                String game = games[gameId][0];
                String gameName = games[gameId][1];
                for (String agentToPlay : agents) {
                    for (int j = 0; j < 5; j++) {
                        for (int i = 0; i < experimentRuns; i++) {
                            level = game.replace(gameName, gameName + "_lvl" + j);
                            System.out.println("Running game " + i + " of " + gameName + " with " + agentToPlay);
                            double[] results = ArcadeMachine.runOneGame(game, level, visuals, agentToPlay, recordActionsFile, seed, 0);
                            resultsRow = agentToPlay + "," + gameName + ',' + j + ',' + (int) results[0] + ',' + results[1] + ',' + results[2] + '\n';
                            fileWriter.write(resultsRow);
                        }
                    }
                }
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
