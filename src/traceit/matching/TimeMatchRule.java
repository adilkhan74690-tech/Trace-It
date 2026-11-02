package traceit.matching;

import traceit.model.FoundItem;
import traceit.model.LostItem;
import traceit.model.MatchBreakdown;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeMatchRule extends MatchRule {

    public TimeMatchRule(int maxWeight) {
        super("Time Proximity", maxWeight);
    }

    @Override
    public MatchBreakdown evaluate(LostItem lost, FoundItem found) {
        LocalDateTime t1 = lost.getDateTime();
        LocalDateTime t2 = found.getDateTime();

        int max = getMaxWeight();

        if (t1 == null || t2 == null) {
            return new MatchBreakdown(getRuleName(), max / 3, max, "[PARTIAL]", "Reporting date/time unspecified");
        }

        Duration duration = Duration.between(t1, t2).abs();
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        String timeDiffStr;
        if (minutes < 60) {
            timeDiffStr = minutes + " minute(s) apart";
        } else if (hours < 24) {
            timeDiffStr = hours + " hour(s) apart";
        } else {
            timeDiffStr = days + " day(s) apart";
        }

        if (minutes <= 60) {
            return new MatchBreakdown(getRuleName(), max, max, "[OK]", "Close reporting time (" + timeDiffStr + ")");
        } else if (hours <= 4) {
            int score = (int) Math.round(max * 0.90);
            return new MatchBreakdown(getRuleName(), score, max, "[OK]", "High temporal proximity (" + timeDiffStr + ")");
        } else if (hours <= 12) {
            int score = (int) Math.round(max * 0.80);
            return new MatchBreakdown(getRuleName(), score, max, "[OK]", "Moderate temporal proximity (" + timeDiffStr + ")");
        } else if (hours <= 24) {
            int score = (int) Math.round(max * 0.70);
            return new MatchBreakdown(getRuleName(), score, max, "[OK]", "Reported within same day (" + timeDiffStr + ")");
        } else if (days <= 3) {
            int score = (int) Math.round(max * 0.50);
            return new MatchBreakdown(getRuleName(), score, max, "[PARTIAL]", "Reported within 3 days (" + timeDiffStr + ")");
        } else if (days <= 7) {
            int score = (int) Math.round(max * 0.30);
            return new MatchBreakdown(getRuleName(), score, max, "[PARTIAL]", "Reported within a week (" + timeDiffStr + ")");
        } else {
            int score = (int) Math.round(max * 0.10);
            return new MatchBreakdown(getRuleName(), score, max, "[DIFF]", "Large time difference (" + timeDiffStr + ")");
        }
    }
}
