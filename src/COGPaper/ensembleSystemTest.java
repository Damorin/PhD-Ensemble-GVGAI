package COGPaper;

import javafx.scene.shape.Arc;
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
 */
public class ensembleSystemTest {
    public static void main(String[] args) {
        // SETUP

        // PATHS
        String gamesPath = "examples/gridphysics/";
        String controllersPath = "COGPaper.";
        String experimentGamesCollection = "examples/eds_heuristics_experiment_games.csv";

        // EXPERIMENT SETUP

        //All available games:
//        String games[] = new String[]{};

        // HEURISTICS

        // GAMES
        String[][] games = Utils.readGames(experimentGamesCollection);

//        String games_experiment[] = new String[]{
//                "aliens",           //0
//                "bait",             //1
//                "butterflies",      //2
//                "camelRace",        //3
//                "chase",            //4
//                "chopper",          //5
//                "crossfire",        //6
//                "digdug",           //7
//                "escape",           //8
//                "hungrybirds",      //9
//                "infection",        //10
//                "intersection",     //11
//                "lemmings",         //12
//                "missilecommand",   //13
//                "modality",         //14
//                "plaqueattack",     //15
//                "roguelike",        //16
//                "seaquest",         //17
//                "survivezombies",   //18
//                "waitforbreakfast", //19
//                "decepticoins",     //20
//                "deceptizelda",     //21
//                "flower",           //22
//                "sistersaviour",    //23
//                "waferthinmints",   //24
//                "invest"            //25
//
//        };

        //Game and level to play
        int game_id = 0;//Integer.parseInt(args[1]); //2
        int level_idx = 0; // This experiment runs just for the first level

//        String gameName = games_experiment[game_id];
//        String game = gamesPath + gameName + ".txt";
//        String level1 = gamesPath + gameName + "_lvl" + level_idx + ".txt";

        // CONTROLLERS

        List<String> agents = new ArrayList<>();
//        agents.add("tracks.singlePlayer.advanced.olets.Agent");
//        agents.add("tracks.singlePlayer.simple.sampleonesteplookahead.Agent");
//        agents.add("tracks.singlePlayer.advanced.sampleRHEA.Agent");
//        agents.add("tracks.singlePlayer.advanced.sampleRS.Agent");
//        agents.add("tracks.singlePlayer.simple.sampleRandom.Agent");
//        agents.add("tracks.singlePlayer.advanced.sampleMCTS.Agent");
        agents.add("COGPaper.ensemble_system.Agent");

        // OTHER SETTINGS
        boolean visuals = true;
        int seed = new Random().nextInt();

        String actionFile = null; //controller+"_actions_" + games[gameIdx] + "_lvl" + levelIdx + "_" + seed + ".txt";
        // TESTS
        String game = games[0][0];
        String gameName = games[0][1];
        String level = game.replace(gameName, gameName + "_lvl" + 0);
//
//        ArcadeMachine.runOneGame(game, level, visuals, agents.get(6), null, seed, 0);

        ArcadeMachine.runOneGame(game, level, visuals, agents.get(0), null, seed, 0);


        // EXPERIMENT

//        experiment(games, agents, visuals, seed);

    }

    private static void experiment(String[][] games, List<String> agents, boolean visuals, int seed) {
        int experimentRuns = 30;
        String recordActionsFile = null;
        String level = null;

        String resultsRow = ("AgentName, GameName, Level, Win, Score, Time\n");

        try {
            File resultsFile = new File("COGPaper_results_17022019.csv");
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
                            resultsRow = agentToPlay + ',' + gameName + ',' + j + ',' + (int) results[0] + ',' + results[1] + ',' + results[2] + '\n';
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
