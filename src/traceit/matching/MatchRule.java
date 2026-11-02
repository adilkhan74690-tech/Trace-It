package traceit.matching;

import traceit.model.FoundItem;
import traceit.model.LostItem;
import traceit.model.MatchBreakdown;

public abstract class MatchRule {
    private final String ruleName;
    private int maxWeight;

    public MatchRule(String ruleName, int maxWeight) {
        this.ruleName = ruleName;
        this.maxWeight = maxWeight;
    }

    public String getRuleName() {
        return ruleName;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
    }

    public abstract MatchBreakdown evaluate(LostItem lost, FoundItem found);
}
