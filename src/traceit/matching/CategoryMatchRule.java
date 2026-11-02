package traceit.matching;

import traceit.model.FoundItem;
import traceit.model.ItemCategory;
import traceit.model.LostItem;
import traceit.model.MatchBreakdown;

public class CategoryMatchRule extends MatchRule {

    public CategoryMatchRule(int maxWeight) {
        super("Category Match", maxWeight);
    }

    @Override
    public MatchBreakdown evaluate(LostItem lost, FoundItem found) {
        ItemCategory cat1 = lost.getCategory();
        ItemCategory cat2 = found.getCategory();

        int max = getMaxWeight();

        if (cat1 != null && cat1 == cat2) {
            return new MatchBreakdown(
                    getRuleName(),
                    max,
                    max,
                    "[OK]",
                    "Same category (" + cat1.getDisplayName() + ")"
            );
        } else if (cat1 == ItemCategory.OTHER || cat2 == ItemCategory.OTHER) {
            int partialScore = max / 2;
            return new MatchBreakdown(
                    getRuleName(),
                    partialScore,
                    max,
                    "[PARTIAL]",
                    "One or both items categorized as " + (cat1 == ItemCategory.OTHER ? cat1.getDisplayName() : cat2.getDisplayName())
            );
        } else {
            return new MatchBreakdown(
                    getRuleName(),
                    0,
                    max,
                    "[DIFF]",
                    "Different categories (" + (cat1 != null ? cat1.getDisplayName() : "Unknown") + " vs " + (cat2 != null ? cat2.getDisplayName() : "Unknown") + ")"
            );
        }
    }
}
