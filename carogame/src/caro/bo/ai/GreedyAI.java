package caro.bo.ai;

import java.util.ArrayList;
import java.util.Random;

import caro.bean.Cell;
import caro.bean.EvalCell;
import caro.bean.State;
import caro.bo.Heuristic;
import caro.values.Value;

/**
 * Mô hình AI Greedy: chọn nước đi có điểm heuristic cao nhất, không tìm kiếm đa tầng.
 *
 * Thuật toán:
 * 1. Đánh giá từng ô trống bằng hàm heuristic (tấn công / phòng thủ).
 * 2. Lọc ra các ô triển vọng nhất.
 * 3. Chọn ngẫu nhiên một trong các ô có điểm cao nhất.
 */
public class GreedyAI implements CaroAIStrategy {

    private final Heuristic heuristic;
    private final Random rand;

    public GreedyAI() {
        this.heuristic = new Heuristic();
        this.rand = new Random();
    }

    @Override
    public Cell findBestMove(State state) {
        heuristic.evaluateEachCell(state, Value.AI_VALUE);
        System.out.println("[GreedyAI] Bảng lượng giá các ô:");
        heuristic.printEvalState();

        ArrayList<EvalCell> candidates = heuristic.getOptimalList();
        if (candidates.isEmpty()) {
            return AIMoveHelper.fallbackMove(state);
        }

        int bestScore = candidates.get(0).getValue();
        ArrayList<EvalCell> bestMoves = new ArrayList<>();
        for (EvalCell candidate : candidates) {
            if (candidate.getValue() == bestScore) {
                bestMoves.add(candidate);
            } else {
                break;
            }
        }

        EvalCell chosen = bestMoves.get(rand.nextInt(bestMoves.size()));
        System.out.println("[GreedyAI] Nước đi được chọn: " + chosen.getX() + " " + chosen.getY()
                + " (điểm: " + chosen.getValue() + ")");
        return AIMoveHelper.ensureValidMove(state, chosen.getCell());
    }

    @Override
    public String getDisplayName() {
        return AIConfig.GREEDY.getDisplayName();
    }
}
