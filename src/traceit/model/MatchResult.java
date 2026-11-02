package traceit.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MatchResult implements Comparable<MatchResult> {
    private LostItem lostItem;
    private FoundItem foundItem;
    private int totalScore;
    private MatchLevel matchLevel;
    private List<MatchBreakdown> breakdowns;
    private LocalDateTime evaluatedAt;

    public MatchResult(LostItem lostItem, FoundItem foundItem, int totalScore, List<MatchBreakdown> breakdowns) {
        this.lostItem = lostItem;
        this.foundItem = foundItem;
        this.totalScore = Math.min(100, Math.max(0, totalScore));
        this.matchLevel = MatchLevel.fromScore(this.totalScore);
        this.breakdowns = breakdowns != null ? breakdowns : new ArrayList<>();
        this.evaluatedAt = LocalDateTime.now();
    }

    public LostItem getLostItem() {
        return lostItem;
    }

    public FoundItem getFoundItem() {
        return foundItem;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public MatchLevel getMatchLevel() {
        return matchLevel;
    }

    public List<MatchBreakdown> getBreakdowns() {
        return breakdowns;
    }

    public LocalDateTime getEvaluatedAt() {
        return evaluatedAt;
    }

    @Override
    public int compareTo(MatchResult o) {
        return Integer.compare(o.totalScore, this.totalScore);
    }

    public String generateExplainableAnalysisReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("----------------------------------------\n");
        sb.append("         TRACEIT MATCH ANALYSIS         \n");
        sb.append("----------------------------------------\n");
        sb.append("LOST ITEM  : #").append(lostItem.getId()).append(" - ").append(lostItem.getName()).append("\n");
        sb.append("FOUND ITEM : #").append(foundItem.getId()).append(" - ").append(foundItem.getName()).append("\n");
        sb.append("----------------------------------------\n\n");

        for (MatchBreakdown b : breakdowns) {
            sb.append(String.format("%-20s : +%d/%d\n", b.getRuleName(), b.getScore(), b.getMaxWeight()));
        }

        sb.append("\n----------------------------------------\n");
        sb.append(String.format("TOTAL SCORE          : %d/100\n", totalScore));
        sb.append(String.format("MATCH LEVEL          : %s\n", matchLevel.getLabel()));
        sb.append("----------------------------------------\n\n");

        List<String> matched = new ArrayList<>();
        List<String> partial = new ArrayList<>();
        List<String> differences = new ArrayList<>();

        for (MatchBreakdown b : breakdowns) {
            String symbol = b.getSymbol();
            String itemText = b.getExplanation();
            if ("[OK]".equals(symbol) || "✓".equals(symbol)) {
                matched.add("[+] " + itemText);
            } else if ("[PARTIAL]".equals(symbol) || "△".equals(symbol)) {
                partial.add("[-] " + itemText);
            } else {
                differences.add("[x] " + itemText);
            }
        }

        if (!matched.isEmpty()) {
            sb.append("Matched:\n");
            for (String m : matched) sb.append("  ").append(m).append("\n");
        }

        if (!partial.isEmpty()) {
            if (!matched.isEmpty()) sb.append("\n");
            sb.append("Partially Matched:\n");
            for (String p : partial) sb.append("  ").append(p).append("\n");
        }

        if (!differences.isEmpty()) {
            if (!matched.isEmpty() || !partial.isEmpty()) sb.append("\n");
            sb.append("Differences / Unmatched:\n");
            for (String d : differences) sb.append("  ").append(d).append("\n");
        }

        sb.append("----------------------------------------");
        return sb.toString();
    }
}
