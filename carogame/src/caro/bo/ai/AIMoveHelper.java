package caro.bo.ai;

import caro.bean.Cell;
import caro.bean.State;
import caro.values.Value;

/**
 * Hỗ trợ chọn nước đi dự phòng khi heuristic không tìm được ô hợp lệ
 * (ví dụ: bàn cờ trống hoàn toàn).
 */
public final class AIMoveHelper {

    private AIMoveHelper() {
    }

    public static Cell fallbackMove(State state) {
        int center = Value.SIZE / 2;
        if (state.isClickable(center, center)) {
            return new Cell(center, center);
        }
        for (int i = 0; i < Value.SIZE; i++) {
            for (int j = 0; j < Value.SIZE; j++) {
                if (state.isClickable(i, j)) {
                    return new Cell(i, j);
                }
            }
        }
        return null;
    }

    public static Cell ensureValidMove(State state, Cell move) {
        if (move != null && state.isClickable(move.getX(), move.getY())) {
            return move;
        }
        return fallbackMove(state);
    }
}
