package traceit.matching;

import traceit.model.FoundItem;
import traceit.model.LostItem;
import traceit.model.MatchBreakdown;

public class ColorMatchRule extends MatchRule {

    public ColorMatchRule(int maxWeight) {
        super("Color Match", maxWeight);
    }

    @Override
    public MatchBreakdown evaluate(LostItem lost, FoundItem found) {
        String c1 = lost.getColor() != null ? lost.getColor().trim().toLowerCase() : "";
        String c2 = found.getColor() != null ? found.getColor().trim().toLowerCase() : "";

        int max = getMaxWeight();

        if (c1.isEmpty() || c2.isEmpty()) {
            return new MatchBreakdown(getRuleName(), max / 3, max, "[PARTIAL]", "Color unspecified in one report");
        }

        if (c1.equals(c2)) {
            return new MatchBreakdown(getRuleName(), max, max, "[OK]", "Same color (" + lost.getColor() + ")");
        }

        if (c1.contains(c2) || c2.contains(c1)) {
            int score = (int) Math.round(max * 0.85);
            return new MatchBreakdown(getRuleName(), score, max, "[OK]", "Similar color tone (" + lost.getColor() + " / " + found.getColor() + ")");
        }

        String[] words1 = c1.split("\\s+");
        String[] words2 = c2.split("\\s+");
        for (String w1 : words1) {
            for (String w2 : words2) {
                if (w1.length() > 2 && w1.equals(w2)) {
                    int score = (int) Math.round(max * 0.70);
                    return new MatchBreakdown(getRuleName(), score, max, "[PARTIAL]", "Partial color overlap (" + w1 + ")");
                }
            }
        }

        return new MatchBreakdown(getRuleName(), 0, max, "[DIFF]", "Mismatched colors (" + lost.getColor() + " vs " + found.getColor() + ")");
    }
}
