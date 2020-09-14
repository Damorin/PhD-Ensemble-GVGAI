package concurrentMCTS;

import tracks.ArcadeMachine;

public class Playground {
    public static void main(String[] args) {
        String game = "examples/gridphysics/chopper.txt";
//        String agent = "concurrentMCTS.ConcurrentAgent";
        String agent = "tracks.singlePlayer.advanced.sampleMCTS.Agent";

        int nRounds = 10;

        double totalScore = 0;
        for (int i = 0 ; i < nRounds ; i++) {
            for (int l = 0 ; l < 5 ; l++) {
                String level = game.substring(0, game.length() - 4) + "_lvl" + l + ".txt";

                double[] result = ArcadeMachine.runOneGame(
                        game, level, true, agent, null,
                        (int) (Math.random() * 10000), 0);

                String line = game + " " + level + " ";
                for (int r = 0; r < result.length; r++) {
                    line += r + ": " + result[r] + ", ";
                }
                System.out.println(line);
                totalScore += result[1];
            }
        }
        System.out.println("Final average score: " + totalScore/nRounds);

    }
}
