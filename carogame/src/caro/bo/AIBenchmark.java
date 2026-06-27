package caro.bo;

import caro.bean.Cell;
import caro.bean.State;
import caro.bo.ai.AIConfig;
import caro.bo.ai.AIMoveHelper;
import caro.bo.ai.CaroAIStrategy;
import caro.values.Value;

/**
 * Công cụ benchmark: chạy AI vs AI, đo tỷ lệ thắng và thời gian phản hồi.
 *
 * Cách chạy:
 *   java caro.bo.TestAI benchmark          (10 ván / cặp, mặc định)
 *   java caro.bo.TestAI benchmark 5        (5 ván / cặp)
 *   java caro.bo.TestAI benchmark 5 verbose
 */
public class AIBenchmark {

    public enum MatchOutcome {
        PLAYER1_WIN,
        PLAYER2_WIN,
        DRAW
    }

    public static class MoveStats {
        long totalNanos;
        int moveCount;

        void add(long nanos) {
            totalNanos += nanos;
            moveCount++;
        }

        public int getMoveCount() {
            return moveCount;
        }

        public double getAverageMs() {
            if (moveCount == 0) {
                return 0;
            }
            return totalNanos / 1_000_000.0 / moveCount;
        }

        public double getTotalMs() {
            return totalNanos / 1_000_000.0;
        }
    }

    public static class MatchResult {
        private final AIConfig player1;
        private final AIConfig player2;
        private final MatchOutcome outcome;
        private final MoveStats player1Stats;
        private final MoveStats player2Stats;
        private final int totalMoves;

        public MatchResult(AIConfig player1, AIConfig player2, MatchOutcome outcome,
                           MoveStats player1Stats, MoveStats player2Stats, int totalMoves) {
            this.player1 = player1;
            this.player2 = player2;
            this.outcome = outcome;
            this.player1Stats = player1Stats;
            this.player2Stats = player2Stats;
            this.totalMoves = totalMoves;
        }

        public AIConfig getPlayer1() {
            return player1;
        }

        public AIConfig getPlayer2() {
            return player2;
        }

        public MatchOutcome getOutcome() {
            return outcome;
        }

        public MoveStats getPlayer1Stats() {
            return player1Stats;
        }

        public MoveStats getPlayer2Stats() {
            return player2Stats;
        }

        public int getTotalMoves() {
            return totalMoves;
        }
    }

    public static class PairStats {
        private final AIConfig configA;
        private final AIConfig configB;
        private int winsA;
        private int winsB;
        private int draws;
        private final MoveStats statsA = new MoveStats();
        private final MoveStats statsB = new MoveStats();

        public PairStats(AIConfig configA, AIConfig configB) {
            this.configA = configA;
            this.configB = configB;
        }

        void record(MatchResult result) {
            switch (result.getOutcome()) {
                case PLAYER1_WIN -> winsA++;
                case PLAYER2_WIN -> winsB++;
                case DRAW -> draws++;
            }
            statsA.moveCount += result.getPlayer1Stats().getMoveCount();
            statsA.totalNanos += result.getPlayer1Stats().totalNanos;
            statsB.moveCount += result.getPlayer2Stats().getMoveCount();
            statsB.totalNanos += result.getPlayer2Stats().totalNanos;
        }

        public AIConfig getConfigA() {
            return configA;
        }

        public AIConfig getConfigB() {
            return configB;
        }

        public int getWinsA() {
            return winsA;
        }

        public int getWinsB() {
            return winsB;
        }

        public int getDraws() {
            return draws;
        }

        public MoveStats getStatsA() {
            return statsA;
        }

        public MoveStats getStatsB() {
            return statsB;
        }

        public int getTotalGames() {
            return winsA + winsB + draws;
        }
    }

    /**
     * Chạy một ván: player1 = quân X (1), player2 = quân O (2).
     */
    public static MatchResult playMatch(AIConfig player1Config, AIConfig player2Config,
                                        boolean player1First, boolean verbose) {
        State state = new State();
        CaroAIStrategy strategy1 = player1Config.createStrategy();
        CaroAIStrategy strategy2 = player2Config.createStrategy();
        MoveStats stats1 = new MoveStats();
        MoveStats stats2 = new MoveStats();
        int currentPlayer = player1First ? Value.USER_VALUE : Value.AI_VALUE;
        int totalMoves = 0;

        PrintStreamHolder holder = verbose ? null : PrintStreamHolder.suppress();
        try {
            while (true) {
                if (state.checkWinner(Value.USER_VALUE)) {
                    return new MatchResult(player1Config, player2Config, MatchOutcome.PLAYER1_WIN,
                            stats1, stats2, totalMoves);
                }
                if (state.checkWinner(Value.AI_VALUE)) {
                    return new MatchResult(player1Config, player2Config, MatchOutcome.PLAYER2_WIN,
                            stats1, stats2, totalMoves);
                }
                if (state.isOver()) {
                    return new MatchResult(player1Config, player2Config, MatchOutcome.DRAW,
                            stats1, stats2, totalMoves);
                }

                long start = System.nanoTime();
                Cell move = findMoveForPlayer(state, currentPlayer, strategy1, strategy2);
                long elapsed = System.nanoTime() - start;

                if (move == null) {
                    return new MatchResult(player1Config, player2Config, MatchOutcome.DRAW,
                            stats1, stats2, totalMoves);
                }

                if (currentPlayer == Value.USER_VALUE) {
                    stats1.add(elapsed);
                } else {
                    stats2.add(elapsed);
                }

                state.update(move.getX(), move.getY(), currentPlayer);
                totalMoves++;

                if (verbose) {
                    String name = (currentPlayer == Value.USER_VALUE)
                            ? player1Config.getDisplayName()
                            : player2Config.getDisplayName();
                    System.out.println("  Nước " + totalMoves + " [" + name + "]: "
                            + move.getX() + "," + move.getY()
                            + " (" + String.format("%.1f ms", elapsed / 1_000_000.0) + ")");
                }

                currentPlayer = (currentPlayer == Value.USER_VALUE) ? Value.AI_VALUE : Value.USER_VALUE;
            }
        } finally {
            if (holder != null) {
                holder.restore();
            }
        }
    }

    /**
     * Strategy luôn coi mình là quân 2 (AI). Khi player1 (quân 1) đi,
     * lật bàn cờ 1↔2 rồi gọi strategy — Caro đối xứng nên kết quả đúng.
     */
    private static Cell findMoveForPlayer(State state, int player,
                                          CaroAIStrategy strategy1, CaroAIStrategy strategy2) {
        Cell move;
        if (player == Value.AI_VALUE) {
            move = strategy2.findBestMove(state);
        } else {
            State flipped = new State(flipBoard(state.getState()));
            move = strategy1.findBestMove(flipped);
        }
        return AIMoveHelper.ensureValidMove(state, move);
    }

    private static int[][] flipBoard(int[][] board) {
        int[][] flipped = new int[Value.SIZE][Value.SIZE];
        for (int i = 0; i < Value.SIZE; i++) {
            for (int j = 0; j < Value.SIZE; j++) {
                if (board[i][j] == Value.USER_VALUE) {
                    flipped[i][j] = Value.AI_VALUE;
                } else if (board[i][j] == Value.AI_VALUE) {
                    flipped[i][j] = Value.USER_VALUE;
                } else {
                    flipped[i][j] = 0;
                }
            }
        }
        return flipped;
    }

    /**
     * Chạy round-robin giữa 3 mô hình AI.
     */
    public static PairStats[] runTournament(int gamesPerPair, boolean verbose) {
        AIConfig[] configs = AIConfig.values();
        PairStats[] allStats = new PairStats[3];

        int idx = 0;
        for (int i = 0; i < configs.length; i++) {
            for (int j = i + 1; j < configs.length; j++) {
                PairStats pairStats = new PairStats(configs[i], configs[j]);
                if (verbose) {
                    System.out.println("\n=== " + configs[i].getDisplayName()
                            + "  vs  " + configs[j].getDisplayName() + " ===");
                }

                for (int g = 0; g < gamesPerPair; g++) {
                    boolean player1First = (g % 2 == 0);
                    if (verbose) {
                        System.out.println("\n--- Ván " + (g + 1) + "/"
                                + gamesPerPair + (player1First ? " (X đi trước)" : " (O đi trước)") + " ---");
                    }
                    MatchResult result = playMatch(configs[i], configs[j], player1First, verbose);
                    pairStats.record(result);
                    if (verbose) {
                        printMatchSummary(result);
                    } else {
                        System.out.print(".");
                    }
                }
                if (!verbose) {
                    System.out.println();
                }
                allStats[idx++] = pairStats;
            }
        }
        return allStats;
    }

    /**
     * Đo thời gian phản hồi từng mô hình trên cùng một thế cờ mẫu.
     */
    public static void runTimingBenchmark(int movesPerModel, boolean verbose) {
        State sampleState = createSampleState();
        System.out.println("\n=== BENCHMARK THỜI GIAN (cùng thế cờ mẫu, " + movesPerModel + " lần/mô hình) ===");

        for (AIConfig config : AIConfig.values()) {
            CaroAIStrategy strategy = config.createStrategy();
            long totalNanos = 0;

            PrintStreamHolder holder = verbose ? null : PrintStreamHolder.suppress();
            try {
                for (int i = 0; i < movesPerModel; i++) {
                    State copy = new State(copyBoard(sampleState.getState()));
                    long start = System.nanoTime();
                    strategy.findBestMove(copy);
                    totalNanos += System.nanoTime() - start;
                }
            } finally {
                if (holder != null) {
                    holder.restore();
                }
            }

            double avgMs = totalNanos / 1_000_000.0 / movesPerModel;
            System.out.printf("  %-28s  %.2f ms/nước  (tổng %.0f ms)%n",
                    config.getDisplayName(), avgMs, totalNanos / 1_000_000.0);
        }
    }

    /** Thế cờ mẫu: vài nước đi ở giữa bàn, gần với ván thực tế. */
    private static State createSampleState() {
        State state = new State();
        int c = Value.SIZE / 2;
        state.update(c, c, Value.USER_VALUE);
        state.update(c, c + 1, Value.AI_VALUE);
        state.update(c + 1, c, Value.USER_VALUE);
        state.update(c - 1, c + 1, Value.AI_VALUE);
        state.update(c + 1, c + 1, Value.USER_VALUE);
        return state;
    }

    private static int[][] copyBoard(int[][] board) {
        int[][] copy = new int[Value.SIZE][Value.SIZE];
        for (int i = 0; i < Value.SIZE; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, Value.SIZE);
        }
        return copy;
    }

    public static void printTournamentReport(PairStats[] pairStats, int gamesPerPair) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════╗");
        System.out.println("║           BÁO CÁO SO SÁNH HIỆU QUẢ 3 MÔ HÌNH AI CARO               ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════╝");
        System.out.println("Số ván / cặp: " + gamesPerPair + "  |  Bàn cờ: " + Value.SIZE + "x" + Value.SIZE
                + "  |  Độ sâu: " + Value.MAX_DEPTH);

        System.out.println("\n── 1. TỶ LỆ THẮNG (AI vs AI) ──");
        for (PairStats ps : pairStats) {
            int total = ps.getTotalGames();
            System.out.println("\n  " + ps.getConfigA().getDisplayName()
                    + "  vs  " + ps.getConfigB().getDisplayName());
            System.out.printf("    %s thắng : %d/%d (%.0f%%)%n",
                    ps.getConfigA().getDisplayName(), ps.getWinsA(), total,
                    100.0 * ps.getWinsA() / total);
            System.out.printf("    %s thắng : %d/%d (%.0f%%)%n",
                    ps.getConfigB().getDisplayName(), ps.getWinsB(), total,
                    100.0 * ps.getWinsB() / total);
            System.out.printf("    Hòa         : %d/%d (%.0f%%)%n",
                    ps.getDraws(), total, 100.0 * ps.getDraws() / total);
        }

        System.out.println("\n── 2. THỜI GIAN PHẢN HỒI TRUNG BÌNH TRONG VÁN ──");
        for (PairStats ps : pairStats) {
            System.out.println("\n  " + ps.getConfigA().getDisplayName()
                    + "  vs  " + ps.getConfigB().getDisplayName());
            System.out.printf("    %s : %.2f ms/nước (%d nước)%n",
                    ps.getConfigA().getDisplayName(),
                    ps.getStatsA().getAverageMs(),
                    ps.getStatsA().getMoveCount());
            System.out.printf("    %s : %.2f ms/nước (%d nước)%n",
                    ps.getConfigB().getDisplayName(),
                    ps.getStatsB().getAverageMs(),
                    ps.getStatsB().getMoveCount());
        }

        System.out.println("\n── 3. BẢNG TỔNG HỢP THỜI GIAN (trung bình tất cả ván) ──");
        printModelTiming(AIConfig.GREEDY, aggregateModelStats(pairStats, AIConfig.GREEDY));
        printModelTiming(AIConfig.MINIMAX, aggregateModelStats(pairStats, AIConfig.MINIMAX));
        printModelTiming(AIConfig.MINIMAX_ALPHA_BETA, aggregateModelStats(pairStats, AIConfig.MINIMAX_ALPHA_BETA));

        System.out.println("\n── Gợi ý đọc kết quả ──");
        System.out.println("  • Greedy nhanh nhất nhưng thường thua Minimax.");
        System.out.println("  • Minimax và Minimax+AB cho kết quả tương đương, AB nhanh hơn.");
        System.out.println("  • So sánh thêm trên GUI: đổi mô hình AI và chơi cùng một thế cờ.");
    }

    private static MoveStats aggregateModelStats(PairStats[] pairStats, AIConfig target) {
        MoveStats total = new MoveStats();
        for (PairStats ps : pairStats) {
            if (ps.getConfigA() == target) {
                total.moveCount += ps.getStatsA().moveCount;
                total.totalNanos += ps.getStatsA().totalNanos;
            }
            if (ps.getConfigB() == target) {
                total.moveCount += ps.getStatsB().moveCount;
                total.totalNanos += ps.getStatsB().totalNanos;
            }
        }
        return total;
    }

    private static void printModelTiming(AIConfig config, MoveStats stats) {
        System.out.printf("  %-28s  %.2f ms/nước  (%d nước tổng)%n",
                config.getDisplayName(), stats.getAverageMs(), stats.getMoveCount());
    }

    public static void printMatchSummary(MatchResult result) {
        String winner;
        switch (result.getOutcome()) {
            case PLAYER1_WIN -> winner = result.getPlayer1().getDisplayName() + " thắng";
            case PLAYER2_WIN -> winner = result.getPlayer2().getDisplayName() + " thắng";
            default -> winner = "Hòa";
        }
        System.out.println("  => " + winner + " sau " + result.getTotalMoves() + " nước");
        System.out.printf("  %s: %.1f ms/nước | %s: %.1f ms/nước%n",
                result.getPlayer1().getDisplayName(),
                result.getPlayer1Stats().getAverageMs(),
                result.getPlayer2().getDisplayName(),
                result.getPlayer2Stats().getAverageMs());
    }

    /** Tắt/bật System.out khi benchmark để đo thời gian chính xác. */
    private static class PrintStreamHolder {
        private final java.io.PrintStream original;

        private PrintStreamHolder(java.io.PrintStream original) {
            this.original = original;
        }

        static PrintStreamHolder suppress() {
            java.io.PrintStream original = System.out;
            System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
            return new PrintStreamHolder(original);
        }

        void restore() {
            System.setOut(original);
        }
    }
}
