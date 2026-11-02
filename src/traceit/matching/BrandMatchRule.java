package traceit.matching;

import traceit.model.FoundItem;
import traceit.model.LostItem;
import traceit.model.MatchBreakdown;

public class BrandMatchRule extends MatchRule {

    public BrandMatchRule(int maxWeight) {
        super("Brand Match", maxWeight);
    }

    @Override
    public MatchBreakdown evaluate(LostItem lost, FoundItem found) {
        String b1 = lost.getBrand() != null ? lost.getBrand().trim().toLowerCase() : "";
        String b2 = found.getBrand() != null ? found.getBrand().trim().toLowerCase() : "";

        int max = getMaxWeight();

        boolean b1Unknown = b1.isEmpty() || b1.contains("unknown") || b1.contains("generic") || b1.contains("n/a") || b1.contains("none");
        boolean b2Unknown = b2.isEmpty() || b2.contains("unknown") || b2.contains("generic") || b2.contains("n/a") || b2.contains("none");

        if (b1Unknown || b2Unknown) {
            int score = (int) Math.round(max * 0.40);
            return new MatchBreakdown(getRuleName(), score, max, "[PARTIAL]", "Brand unspecified or generic");
        }

        if (b1.equals(b2)) {
            return new MatchBreakdown(getRuleName(), max, max, "[OK]", "Same brand (" + lost.getBrand() + ")");
        }

        if (b1.contains(b2) || b2.contains(b1)) {
            int score = (int) Math.round(max * 0.90);
            return new MatchBreakdown(getRuleName(), score, max, "[OK]", "Matching brand variant (" + lost.getBrand() + " / " + found.getBrand() + ")");
        }

        return new MatchBreakdown(getRuleName(), 0, max, "[DIFF]", "Conflicting brands (" + lost.getBrand() + " vs " + found.getBrand() + ")");
    }
}
