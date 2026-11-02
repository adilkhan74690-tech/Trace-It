package traceit.matching;

import traceit.model.FoundItem;
import traceit.model.LostItem;
import traceit.model.MatchBreakdown;
import traceit.model.MatchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeightedMatchStrategy implements MatchStrategy {

    private final List<MatchRule> rules;

    public WeightedMatchStrategy() {
        this.rules = new ArrayList<>();
        this.rules.add(new CategoryMatchRule(20));
        this.rules.add(new ColorMatchRule(15));
        this.rules.add(new BrandMatchRule(15));
        this.rules.add(new LocationMatchRule(20));
        this.rules.add(new TimeMatchRule(15));
        this.rules.add(new DescriptionMatchRule(15));
    }

    public WeightedMatchStrategy(List<MatchRule> customRules) {
        this.rules = customRules;
    }

    public List<MatchRule> getRules() {
        return rules;
    }

    @Override
    public MatchResult evaluate(LostItem lostItem, FoundItem foundItem) {
        List<MatchBreakdown> breakdowns = new ArrayList<>();
        int totalScore = 0;

        for (MatchRule rule : rules) {
            MatchBreakdown breakdown = rule.evaluate(lostItem, foundItem);
            breakdowns.add(breakdown);
            totalScore += breakdown.getScore();
        }

        return new MatchResult(lostItem, foundItem, totalScore, breakdowns);
    }

    @Override
    public List<MatchResult> evaluateAll(LostItem lostItem, List<FoundItem> foundItems) {
        List<MatchResult> results = new ArrayList<>();
        if (lostItem == null || foundItems == null) return results;

        for (FoundItem found : foundItems) {
            results.add(evaluate(lostItem, found));
        }

        Collections.sort(results);
        return results;
    }
}
