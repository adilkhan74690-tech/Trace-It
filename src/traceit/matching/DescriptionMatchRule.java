package traceit.matching;

import traceit.model.FoundItem;
import traceit.model.LostItem;
import traceit.model.MatchBreakdown;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DescriptionMatchRule extends MatchRule {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "an", "the", "with", "and", "or", "in", "on", "at", "to", "for", "of", "it",
            "is", "was", "has", "have", "had", "my", "some", "small", "big", "item", "found", "lost"
    ));

    public DescriptionMatchRule(int maxWeight) {
        super("Description Match", maxWeight);
    }

    @Override
    public MatchBreakdown evaluate(LostItem lost, FoundItem found) {
        String desc1 = (lost.getDescription() + " " + lost.getDistinctiveFeatures()).toLowerCase();
        String desc2 = (found.getDescription() + " " + found.getDistinctiveFeatures()).toLowerCase();

        int max = getMaxWeight();

        Set<String> tokens1 = extractKeywords(desc1);
        Set<String> tokens2 = extractKeywords(desc2);

        if (tokens1.isEmpty() || tokens2.isEmpty()) {
            return new MatchBreakdown(getRuleName(), max / 3, max, "[PARTIAL]", "Description missing or brief");
        }

        Set<String> intersection = new HashSet<>(tokens1);
        intersection.retainAll(tokens2);

        Set<String> union = new HashSet<>(tokens1);
        union.addAll(tokens2);

        double jaccard = (double) intersection.size() / union.size();

        double overlapRatio = (double) intersection.size() / Math.min(tokens1.size(), tokens2.size());
        double combinedScoreRatio = Math.max(jaccard, overlapRatio * 0.85);

        int score = (int) Math.round(combinedScoreRatio * max);
        score = Math.min(max, score);

        String matchedWords = String.join(", ", intersection);

        if (score >= Math.round(max * 0.70)) {
            return new MatchBreakdown(
                    getRuleName(),
                    score,
                    max,
                    "[OK]",
                    "High description overlap" + (!matchedWords.isEmpty() ? " (" + matchedWords + ")" : "")
            );
        } else if (score >= Math.round(max * 0.35)) {
            return new MatchBreakdown(
                    getRuleName(),
                    score,
                    max,
                    "[PARTIAL]",
                    "Partial description overlap" + (!matchedWords.isEmpty() ? " (" + matchedWords + ")" : "")
            );
        } else {
            return new MatchBreakdown(
                    getRuleName(),
                    score,
                    max,
                    "[DIFF]",
                    "Low description similarity"
            );
        }
    }

    private Set<String> extractKeywords(String text) {
        Set<String> keywords = new HashSet<>();
        if (text == null) return keywords;

        String clean = text.replaceAll("[^a-zA-Z0-9\\s]", " ");
        String[] words = clean.split("\\s+");

        for (String w : words) {
            w = w.trim();
            if (w.length() > 2 && !STOP_WORDS.contains(w)) {
                if (w.endsWith("per") && w.length() > 4) {
                    keywords.add(w.substring(0, w.length() - 3));
                }
                keywords.add(w);
            }
        }
        return keywords;
    }
}
