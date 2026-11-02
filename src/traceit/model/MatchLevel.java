package traceit.model;

public enum MatchLevel {
    VERY_STRONG_MATCH("VERY STRONG MATCH", 90, 100),
    STRONG_MATCH("STRONG MATCH", 75, 89),
    POSSIBLE_MATCH("POSSIBLE MATCH", 60, 74),
    WEAK_MATCH("WEAK MATCH", 40, 59),
    UNLIKELY_MATCH("UNLIKELY MATCH", 0, 39);

    private final String label;
    private final int minScore;
    private final int maxScore;

    MatchLevel(String label, int minScore, int maxScore) {
        this.label = label;
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    public String getLabel() {
        return label;
    }

    public int getMinScore() {
        return minScore;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public static MatchLevel fromScore(int score) {
        if (score >= 90) return VERY_STRONG_MATCH;
        if (score >= 75) return STRONG_MATCH;
        if (score >= 60) return POSSIBLE_MATCH;
        if (score >= 40) return WEAK_MATCH;
        return UNLIKELY_MATCH;
    }
}
