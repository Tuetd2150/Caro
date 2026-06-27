package caro.bo.ai;

import java.util.ArrayList;

import caro.bean.EvalCell;
import caro.bean.State;
import caro.values.Value;

/**
 * Mô hình AI Minimax + Alpha-Beta: tìm kiếm đối kháng có cắt tỉa Alpha-Beta.
 *
 * Khác biệt so với MinimaxAI:
 * - Dùng biến alpha (điểm tốt nhất AI đảm bảo) và beta (điểm tốt nhất User đảm bảo).
 * - Khi alpha >= beta, cắt bỏ các nhánh còn lại vì không ảnh hưởng kết quả cuối cùng.
 * - Giảm đáng kể số nút cần duyệt so với Minimax thuần.
 */
public class MinimaxAlphaBetaAI extends MinimaxAI {

    public MinimaxAlphaBetaAI() {
        super();
    }

    @Override
    protected int maxValue(State state, int depth) {
        if (isTerminal(state, Value.AI_VALUE, depth)) {
            return heuristic.evaluateState(state);
        }

        heuristic.evaluateEachCell(state, Value.AI_VALUE);
        ArrayList<EvalCell> moves = heuristic.getOptimalList();
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (EvalCell move : moves) {
            int x = move.getCell().getX();
            int y = move.getCell().getY();
            state.getState()[x][y] = Value.AI_VALUE;
            alpha = Math.max(alpha, minValue(state, depth + 1, alpha, beta));
            state.getState()[x][y] = 0;
            if (alpha >= beta) {
                break;
            }
        }
        return alpha;
    }

    @Override
    protected int minValue(State state, int depth) {
        return minValue(state, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private int minValue(State state, int depth, int alpha, int beta) {
        if (isTerminal(state, Value.USER_VALUE, depth)) {
            return heuristic.evaluateState(state);
        }

        heuristic.evaluateEachCell(state, Value.USER_VALUE);
        ArrayList<EvalCell> moves = heuristic.getOptimalList();

        for (EvalCell move : moves) {
            int x = move.getCell().getX();
            int y = move.getCell().getY();
            state.getState()[x][y] = Value.USER_VALUE;
            beta = Math.min(beta, maxValue(state, depth + 1, alpha, beta));
            state.getState()[x][y] = 0;
            if (alpha >= beta) {
                break;
            }
        }
        return beta;
    }

    private int maxValue(State state, int depth, int alpha, int beta) {
        if (isTerminal(state, Value.AI_VALUE, depth)) {
            return heuristic.evaluateState(state);
        }

        heuristic.evaluateEachCell(state, Value.AI_VALUE);
        ArrayList<EvalCell> moves = heuristic.getOptimalList();

        for (EvalCell move : moves) {
            int x = move.getCell().getX();
            int y = move.getCell().getY();
            state.getState()[x][y] = Value.AI_VALUE;
            alpha = Math.max(alpha, minValue(state, depth + 1, alpha, beta));
            state.getState()[x][y] = 0;
            if (alpha >= beta) {
                break;
            }
        }
        return alpha;
    }

    @Override
    protected String getLogTag() {
        return "MinimaxAlphaBetaAI";
    }

    @Override
    public String getDisplayName() {
        return AIConfig.MINIMAX_ALPHA_BETA.getDisplayName();
    }
}
