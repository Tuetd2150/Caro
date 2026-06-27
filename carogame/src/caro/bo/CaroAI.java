package caro.bo;
import caro.bean.Cell;
import caro.bean.State;
import caro.bo.ai.AIConfig;
import caro.bo.ai.CaroAIStrategy;
import caro.values.Value;

/**
 * Facade điều phối trò chơi Caro. Ủy quyền logic suy nghĩ cho CaroAIStrategy
 * (GreedyAI, MinimaxAI hoặc MinimaxAlphaBetaAI).
 */
public class CaroAI {
    private int nextX;
    private int nextY;
    private int mode;
    private State root;
    private CaroAIStrategy strategy;

    public CaroAI(int mode) {
        this(mode, AIConfig.MINIMAX_ALPHA_BETA);
    }

    public CaroAI(int mode, AIConfig aiConfig) {
        this.mode = mode;
        this.strategy = aiConfig.createStrategy();
        if (this.mode == 1) {
            root = new State();
            root.update(Value.SIZE / 2, Value.SIZE / 2, Value.AI_VALUE);
            nextX = Value.SIZE / 2;
            nextY = Value.SIZE / 2;
        } else {
            root = new State();
        }
    }

    public void setStrategy(AIConfig aiConfig) {
        this.strategy = aiConfig.createStrategy();
    }

    public String getStrategyName() {
        return strategy.getDisplayName();
    }

    public int getNextX() {
        return nextX;
    }

    public int getNextY() {
        return nextY;
    }

    public boolean checkWinner(int player) {
        return root.checkWinner(player);
    }

    public void update(int x, int y, int value) {
        root.update(x, y, value);
    }

    public boolean isClickable(int x, int y) {
        return root.isClickable(x, y);
    }

    public boolean isOver() {
        return root.isOver();
    }

    public void nextStep() {
        System.out.println("=> Mô hình AI: " + strategy.getDisplayName());
        Cell choice = strategy.findBestMove(root);
        if (choice == null) {
            System.out.println("~ Lỗi! Không tìm thấy nước đi!");
        } else {
            this.nextX = choice.getX();
            this.nextY = choice.getY();
            System.out.println("=> Nước đi của AI: " + this.nextX + " " + this.nextY);
            if (!isClickable(this.nextX, this.nextY)) {
                System.out.println("~ Lỗi! nước đi bị trùng!");
            } else {
                update(nextX, nextY, Value.AI_VALUE);
            }
        }
    }
}
