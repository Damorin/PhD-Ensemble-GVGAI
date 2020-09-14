package concurrentRHEA;

import tracks.ArcadeMachine;

public class Playground {
    public static void main(String[] args) {
        String game = "examples/gridphysics/butterflies.txt";
        String level = "examples/gridphysics/butterflies_lvl1.txt";
        String agent = "concurrentRHEA.ConcurrentAgent";

        int nRounds = 100;

        double totalScore = 0;
        for (int i = 0 ; i < nRounds ; i++) {
            double[] result = ArcadeMachine.runOneGame(
                    game, level, true, agent, null,
                    (int) (Math.random() * 10000), 0);

            String line = game + " " + level + " ";
            for (int r = 0 ; r < result.length ; r++) {
                line += r + ": " + result[r] + ", ";
            }
            System.out.println(line);
            totalScore += result[1];
        }
        System.out.println("Final average score: " + totalScore/nRounds);

    }
}
