package caro.bo.ai;

import java.util.ArrayList;
import java.util.Random;

import caro.bean.Cell;
import caro.bean.EvalCell;
import caro.bean.State;
import caro.bo.Heuristic;
import caro.values.Value;

/**
 * Mô hình AI Minimax: tìm kiếm đối kháng đa tầng, KHÔNG dùng cắt tỉa Alpha-Beta.
 *
 * Thuật toán:
 * - maxValue: AI cố tối đa hóa điểm đánh giá.
 * - minValue: Người chơi cố tối thiểu hóa điểm đánh giá.
 * - Duyệt đầy đủ các nhánh con tại mỗi nút (không cắt tỉa).
 */
public class MinimaxAI implements CaroAIStrategy {

    protected final Heuristic heuristic;
    protected final Random rand;

    public MinimaxAI() {
        this.heuristic = new Heuristic();
        this.rand = new Random();
    }

    @Override
    public Cell findBestMove(State state) {
        State searchState = new State(state.getState());
        heuristic.evaluateEachCell(searchState, Value.AI_VALUE);
        System.out.println("[" + getLogTag() + "] Bảng lượng giá các ô:");
        heuristic.printEvalState();

        ArrayList<EvalCell> candidates = heuristic.getOptimalList();
        if (candidates.isEmpty()) {
            return AIMoveHelper.fallbackMove(searchState);
        }
        System.out.println("[" + getLogTag() + "] Danh sách nước đi triển vọng (tối đa " + Value.MAX_NUM_OF_HIGHEST_CELL_LIST + "):");
        for (EvalCell candidate : candidates) {
            System.out.println(candidate.getX() + " " + candidate.getY() + ":" + candidate.getValue());
        }

        int maxValue = Integer.MIN_VALUE;
        ArrayList<EvalCell> bestMoves = new ArrayList<>();
        for (EvalCell candidate : candidates) {
            searchState.getState()[candidate.getX()][candidate.getY()] = Value.AI_VALUE;
            System.out.println("[" + getLogTag() + "] Thử nước đi " + candidate.getX() + " " + candidate.getY() + ":");
            int value = minValue(searchState, 0);
            System.out.println("[" + getLogTag() + "] Lượng giá: " + value);
            searchState.getState()[candidate.getX()][candidate.getY()] = 0;

            if (maxValue < value) {
                maxValue = value;
                bestMoves.clear();
                bestMoves.add(candidate);
            } else if (maxValue == value) {
                bestMoves.add(candidate);
            }
        }

        if (bestMoves.isEmpty()) {
            return AIMoveHelper.fallbackMove(searchState);
        }
        return AIMoveHelper.ensureValidMove(searchState,
                bestMoves.get(rand.nextInt(bestMoves.size())).getCell());
    }

    protected int maxValue(State state, int depth) {
        if (isTerminal(state, Value.AI_VALUE, depth)) {
            return heuristic.evaluateState(state);
        }

        heuristic.evaluateEachCell(state, Value.AI_VALUE);
        ArrayList<EvalCell> moves = heuristic.getOptimalList();
        int best = Integer.MIN_VALUE;

        for (EvalCell move : moves) {
            int x = move.getCell().getX();
            int y = move.getCell().getY();
            state.getState()[x][y] = Value.AI_VALUE;
            best = Math.max(best, minValue(state, depth + 1));
            state.getState()[x][y] = 0;
        }
        return best;
    }

    protected int minValue(State state, int depth) {
        if (isTerminal(state, Value.USER_VALUE, depth)) {
            return heuristic.evaluateState(state);
        }

        heuristic.evaluateEachCell(state, Value.USER_VALUE);
        ArrayList<EvalCell> moves = heuristic.getOptimalList();
        int best = Integer.MAX_VALUE;

        for (EvalCell move : moves) {
            int x = move.getCell().getX();
            int y = move.getCell().getY();
            state.getState()[x][y] = Value.USER_VALUE;
            best = Math.min(best, maxValue(state, depth + 1));
            state.getState()[x][y] = 0;
        }
        return best;
    }

    protected boolean isTerminal(State state, int player, int depth) {
        return depth >= Value.MAX_DEPTH || state.checkWinner(player) || state.isOver();
    }

    protected String getLogTag() {
        return "MinimaxAI";
    }

    @Override
    public String getDisplayName() {
        return AIConfig.MINIMAX.getDisplayName();
    }
}
