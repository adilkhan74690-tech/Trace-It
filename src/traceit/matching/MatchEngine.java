package traceit.matching;

import traceit.model.FoundItem;
import traceit.model.LostItem;
import traceit.model.MatchResult;

import java.util.List;

public class MatchEngine {

    private MatchStrategy matchStrategy;

    public MatchEngine() {
        this.matchStrategy = new WeightedMatchStrategy();
    }

    public MatchEngine(MatchStrategy matchStrategy) {
        this.matchStrategy = matchStrategy;
    }

    public MatchStrategy getMatchStrategy() {
        return matchStrategy;
    }

    public void setMatchStrategy(MatchStrategy matchStrategy) {
        this.matchStrategy = matchStrategy;
    }

    public List<MatchResult> findMatches(LostItem lostItem, List<FoundItem> foundItems) {
        return matchStrategy.evaluateAll(lostItem, foundItems);
    }

    public MatchResult analyzePair(LostItem lostItem, FoundItem foundItem) {
        return matchStrategy.evaluate(lostItem, foundItem);
    }
}
