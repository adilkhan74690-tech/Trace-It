package traceit.matching;

import traceit.model.FoundItem;
import traceit.model.LostItem;
import traceit.model.MatchResult;

import java.util.List;

public interface MatchStrategy {
    MatchResult evaluate(LostItem lostItem, FoundItem foundItem);
    List<MatchResult> evaluateAll(LostItem lostItem, List<FoundItem> foundItems);
}
