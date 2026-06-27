package caro.bo.ai;

import caro.bean.Cell;
import caro.bean.State;

public interface CaroAIStrategy {

    Cell findBestMove(State state);
    String getDisplayName();
}
