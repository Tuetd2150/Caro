package caro.bo;

import java.util.Random;

import caro.bo.ai.AIConfig;

/**
 * Chạy thử AI trên console.
 *
 * Cách dùng:
 *   java caro.bo.TestAI                         Demo chơi với AI (Minimax+AB)
 *   java caro.bo.TestAI 0                         Demo với Greedy
 *   java caro.bo.TestAI benchmark                 AI vs AI, 10 ván/cặp
 *   java caro.bo.TestAI benchmark 5              AI vs AI, 5 ván/cặp
 *   java caro.bo.TestAI benchmark 3 verbose       Chi tiết từng nước đi
 *   java caro.bo.TestAI timing                    Đo thời gian trên thế cờ mẫu
 */
public class TestAI {
    private static final Random rand = new Random();
    private static int x;
    private static int y;

    public static void main(String[] args) {
        if (args.length > 0 && "benchmark".equalsIgnoreCase(args[0])) {
            runBenchmark(args);
            return;
        }
        if (args.length > 0 && "timing".equalsIgnoreCase(args[0])) {
            AIBenchmark.runTimingBenchmark(5, false);
            return;
        }
        runDemo(args);
    }

    private static void runBenchmark(String[] args) {
        int gamesPerPair = 10;
        boolean verbose = false;

        if (args.length > 1) {
            try {
                gamesPerPair = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                if ("verbose".equalsIgnoreCase(args[1])) {
                    verbose = true;
                }
            }
        }
        if (args.length > 2 && "verbose".equalsIgnoreCase(args[2])) {
            verbose = true;
        }

        System.out.println("Bắt đầu benchmark AI vs AI...");
        System.out.println("Số ván mỗi cặp: " + gamesPerPair);
        System.out.println("(Minimax chậm — có thể mất vài phút)\n");

        long start = System.currentTimeMillis();
        AIBenchmark.PairStats[] results = AIBenchmark.runTournament(gamesPerPair, verbose);
        long elapsed = System.currentTimeMillis() - start;

        AIBenchmark.printTournamentReport(results, gamesPerPair);
        AIBenchmark.runTimingBenchmark(3, false);
        System.out.println("\nTổng thời gian benchmark: " + (elapsed / 1000) + " giây");
    }

    private static void runDemo(String[] args) {
        AIConfig config = AIConfig.MINIMAX_ALPHA_BETA;
        if (args.length > 0) {
            config = AIConfig.fromCode(Integer.parseInt(args[0]));
        }

        System.out.println("Mô hình AI: " + config.getDisplayName());
        CaroAI caroAI = new CaroAI(1, config);

        for (int i = 0; i < 5; i++) {
            random();
            while (!caroAI.isClickable(x, y)) {
                random();
            }
            System.out.println("Nước đi của người chơi: " + x + " " + y);
            caroAI.update(x, y, 1);
            if (caroAI.checkWinner(1)) {
                System.out.println("Người chơi thắng!");
                break;
            }

            System.out.println("Bước đi thứ " + (i + 1) + ":");
            caroAI.nextStep();
            System.out.println();
            if (caroAI.checkWinner(2)) {
                System.out.println("AI thắng!");
                break;
            }
        }
    }

    private static void random() {
        x = rand.nextInt(0, 4);
        y = rand.nextInt(0, 4);
    }
}
