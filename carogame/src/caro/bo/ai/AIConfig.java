package caro.bo.ai;

/**
 * Cấu hình mô hình AI. Mỗi giá trị tương ứng một thuật toán riêng biệt.
 */
public enum AIConfig {
    GREEDY(0, "AI Greedy"),
    MINIMAX(1, "AI Minimax"),
    MINIMAX_ALPHA_BETA(2, "AI Minimax + Alpha-Beta");

    private final int code;
    private final String displayName;

    AIConfig(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public int getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static AIConfig fromCode(int code) {
        for (AIConfig config : values()) {
            if (config.code == code) {
                return config;
            }
        }
        return MINIMAX_ALPHA_BETA;
    }

    public CaroAIStrategy createStrategy() {
        return switch (this) {
            case GREEDY -> new GreedyAI();
            case MINIMAX -> new MinimaxAI();
            case MINIMAX_ALPHA_BETA -> new MinimaxAlphaBetaAI();
        };
    }
}
