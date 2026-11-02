package traceit.model;

public class MatchBreakdown {
    private String ruleName;
    private int score;
    private int maxWeight;
    private String symbol;
    private String explanation;

    public MatchBreakdown(String ruleName, int score, int maxWeight, String symbol, String explanation) {
        this.ruleName = ruleName;
        this.score = score;
        this.maxWeight = maxWeight;
        this.symbol = symbol;
        this.explanation = explanation;
    }

    public String getRuleName() {
        return ruleName;
    }

    public int getScore() {
        return score;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getExplanation() {
        return explanation;
    }

    @Override
    public String toString() {
        return String.format("%-20s : +%d/%d (%s %s)", ruleName, score, maxWeight, symbol, explanation);
    }
}
