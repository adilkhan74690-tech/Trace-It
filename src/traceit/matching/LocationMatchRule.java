package traceit.matching;

import traceit.model.FoundItem;
import traceit.model.Location;
import traceit.model.LostItem;
import traceit.model.MatchBreakdown;

public class LocationMatchRule extends MatchRule {

    public LocationMatchRule(int maxWeight) {
        super("Location Match", maxWeight);
    }

    @Override
    public MatchBreakdown evaluate(LostItem lost, FoundItem found) {
        Location loc1 = lost.getLocation();
        Location loc2 = found.getLocation();

        int max = getMaxWeight();

        if (loc1 == null || loc2 == null) {
            return new MatchBreakdown(getRuleName(), max / 4, max, "[PARTIAL]", "Location missing from report");
        }

        double similarity = loc1.calculateSimilarity(loc2);
        int score = (int) Math.round(similarity * max);

        if (similarity >= 0.85) {
            return new MatchBreakdown(
                    getRuleName(),
                    score,
                    max,
                    "[OK]",
                    "Nearby / matching location (" + loc1.getRawLocation() + " & " + loc2.getRawLocation() + ")"
            );
        } else if (similarity >= 0.40) {
            return new MatchBreakdown(
                    getRuleName(),
                    score,
                    max,
                    "[PARTIAL]",
                    "Related campus area (" + loc1.getBuildingOrZone() + ")"
            );
        } else {
            return new MatchBreakdown(
                    getRuleName(),
                    score,
                    max,
                    "[DIFF]",
                    "Different campus locations (" + loc1.getRawLocation() + " vs " + loc2.getRawLocation() + ")"
            );
        }
    }
}
