package tracks.singlePlayer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import core.logging.Logger;
import tools.Utils;
import tracks.ArcadeMachine;

/**
 * Created with IntelliJ IDEA. User: Diego Date: 04/10/13 Time: 16:29 This is a
 * Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class EDSTest {

    public static void main(String[] args) {

        // Available sample agents:
        String sampleRandomController = "tracks.singlePlayer.simple.sampleRandom.Agent";
        String sampleFlatMCTSController = "tracks.singlePlayer.simple.greedyTreeSearch.Agent";
        String sampleMCTSController = "pessimisticMCTS.Agent";
        String sampleRSController = "tracks.singlePlayer.advanced.sampleRS.Agent";
        String sampleRHEAController = "tracks.singlePlayer.advanced.sampleRHEA.Agent";
        String sampleOLETSController = "tracks.singlePlayer.advanced.olets.Agent";

        // My agents
        String pessimisticMCTS = "pessimisticMCTS.Agent";
        String originalDamorin = "originalDamorin.Agent";
        String edsRMCTSPair = "EDS_R_MCTSPair.Agent";
        String edsROletsMCTS = "EDS_R_OLETSMCTS.Agent";
        String edsRHEAMCTS = "EDS_R_RHEAMCTS.Agent";

        //Load available games
        String allGamesCollection = "examples/all_games_sp.csv";
        String deceptiveGamesCollection = "examples/deceptive_games.csv";
        String experimentGamesCollection = "examples/eds_experiment_games.csv";
        String[][] games = Utils.readGames(experimentGamesCollection);

        List<String> agents = new ArrayList<>();
//        agents.add(sampleRandomController);
//        agents.add(sampleMCTSController);
//        agents.add(sampleRHEAController);
//        agents.add(sampleOLETSController);
//        agents.add(originalDamorin);
//        agents.add(pessimisticMCTS);
//        agents.add(edsRMCTSPair);
        agents.add(edsRHEAMCTS);
//        agents.add(edsROletsMCTS);

        //Game settings
        boolean visuals = false;
        int seed = new Random().nextInt();

        // Game and level to play
        int gameIdx = 0;
        int levelIdx = 4; // level names from 0 to 4 (game_lvlN.txt).
        String game = games[gameIdx][0];
        String gameName = games[gameIdx][1];
        String level = game.replace(gameName, gameName + "_lvl" + levelIdx);


        String recordActionsFile = null;// "actions_" + games[gameIdx] + "_lvl"
//        double[] results = ArcadeMachine.runOneGame(game, level, visuals, originalDamorin, recordActionsFile, seed, 0);
        // + levelIdx + "_" + seed + ".txt";
        // where to record the actions
        // executed. null if not to save.

        // 1. This starts a game, in a level, played by a human.
//        ArcadeMachine.playOneGame(game, level, recordActionsFile, seed);

        // 2. This plays a game in a level by the controller.
        int experimentRuns = 100;

//        String agent = sampleRandomController;
        String resultsRow = ("AgentName, GameName, Level, Win, Score, Time\n");

        try {
            File resultsFile = new File("results_28102018.csv");
            resultsFile.createNewFile();
            FileWriter fileWriter = new FileWriter(resultsFile, true);
            fileWriter.write(resultsRow);

            for (String agentToPlay : agents) {
                for (int j = 4; j < 5; j++) {
                    for (int i = 0; i < experimentRuns; i++) {
                        level = game.replace(gameName, gameName + "_lvl" + j);
                        System.out.println("Running game " + i + " of " + gameName + " with " + agentToPlay);
                        double[] results = ArcadeMachine.runOneGame(game, level, visuals, agentToPlay, recordActionsFile, seed, 0);
                        resultsRow = agentToPlay + ',' + gameName + ',' + j + ',' + (int) results[0] + ',' + results[1] + ',' + results[2] + '\n';
                        fileWriter.write(resultsRow);
                    }
                }
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // 3. This replays a game from an action file previously recorded
        //	 String readActionsFile = recordActionsFile;
        //	 ArcadeMachine.replayGame(game, level1, visuals, readActionsFile);

        // 4. This plays a single game, in N levels, M times :
//		String level2 = new String(game).replace(gameName, gameName + "_lvl" + 1);
//		int M = 10;
//		for(int i=0; i<games.length; i++){
//			game = games[i][0];
//			gameName = games[i][1];
//			level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);
//			ArcadeMachine.runGames(game, new String[]{level1}, M, sampleMCTSController, null);
//		}

        //5. This plays N games, in the first L levels, M times each. Actions to file optional (set saveActions to true).
//		int N = games.length, L = 2, M = 1;
//		boolean saveActions = false;
//		String[] levels = new String[L];
//		String[] actionFiles = new String[L*M];
//		for(int i = 0; i < N; ++i)
//		{
//			int actionIdx = 0;
//			game = games[i][0];
//			gameName = games[i][1];
//			for(int j = 0; j < L; ++j){
//				levels[j] = game.replace(gameName, gameName + "_lvl" + j);
//				if(saveActions) for(int k = 0; k < M; ++k)
//				actionFiles[actionIdx++] = "actions_game_" + i + "_level_" + j + "_" + k + ".txt";
//			}
//			ArcadeMachine.runGames(game, levels, M, sampleRHEAController, saveActions? actionFiles:null);
//		}


    }
}
