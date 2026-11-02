package traceit.test;

import traceit.model.*;
import traceit.ui.AppContext;

import java.time.LocalDateTime;
import java.util.List;

public class GUIFunctionalTest {

    public static void main(String[] args) {
        System.out.println(">>> RUNNING FULL FUNCTIONAL WORKFLOW VERIFICATION <<<");
        java.io.File testDir = new java.io.File("data_test");
        try {
            if (testDir.exists()) {
                for (java.io.File f : testDir.listFiles()) f.delete();
                testDir.delete();
            }
            AppContext context = new AppContext("data_test");

            // 1. Add new lost item
            String testLostName = "AirPods Max Silver Test";
            User owner = new User("U999", "Test Owner", "owner@test.edu", "+1-555-9999", "Student");
            LostItem newLost = new LostItem(
                    null,
                    testLostName,
                    ItemCategory.EARBUDS,
                    "Silver",
                    "Apple",
                    new Location("Library Study Room B"),
                    LocalDateTime.now().minusHours(2),
                    "Silver over-ear wireless headphones with smart case",
                    "Small dent on left aluminum cup",
                    owner,
                    30.0,
                    "Email"
            );
            LostItem savedLost = context.getLostItemService().addLostItem(newLost);
            System.out.println("[Step 1] Added Lost Item: #" + savedLost.getId() + " - " + savedLost.getName());
            assert savedLost.getId() != null && savedLost.getId().startsWith("LST-");

            // 2. Add new found item that strongly matches
            User finder = new User("F999", "Desk Attendant", "attendant@test.edu", "+1-555-8888", "Staff");
            FoundItem newFound = new FoundItem(
                    null,
                    "Apple AirPods Max Silver",
                    ItemCategory.EARBUDS,
                    "Silver",
                    "Apple",
                    new Location("Library Study Room B"),
                    LocalDateTime.now().minusHours(1),
                    "Silver Apple headphones with magnetic case found on table",
                    "Dent on left ear cup",
                    finder,
                    "Library Vault 204",
                    "What color is the inner mesh canopy?"
            );
            FoundItem savedFound = context.getFoundItemService().addFoundItem(newFound);
            System.out.println("[Step 2] Added Found Item: #" + savedFound.getId() + " - " + savedFound.getName());
            assert savedFound.getId() != null && savedFound.getId().startsWith("FND-");

            // 3. Run Matching
            List<MatchResult> matches = context.getRecoveryService().findMatchesForLostItem(savedLost.getId());
            System.out.println("[Step 3] Run Matching: found " + matches.size() + " matches.");
            assert !matches.isEmpty();
            MatchResult topMatch = matches.get(0);
            System.out.println("         Top candidate: #" + topMatch.getFoundItem().getId() + " with score: " + topMatch.getTotalScore() + "/100 (" + topMatch.getMatchLevel().getLabel() + ")");
            MatchResult foundResult = null;
            for (MatchResult mr : matches) {
                if (mr.getFoundItem().getId().equals(savedFound.getId())) {
                    foundResult = mr;
                    break;
                }
            }
            assert foundResult != null;
            assert foundResult.getTotalScore() >= 80;

            // 4. Detailed Analysis
            MatchResult analysis = context.getRecoveryService().analyzePair(savedLost.getId(), savedFound.getId());
            System.out.println("[Step 4] Analyze Pair rules:");
            for (MatchBreakdown b : analysis.getBreakdowns()) {
                System.out.println("         • " + b.getRuleName() + ": +" + b.getScore() + "/" + b.getMaxWeight() + " [" + b.getSymbol() + "] " + b.getExplanation());
            }
            assert analysis.getBreakdowns().size() == 6;

            // 5. Update Status to POSSIBLE_MATCH
            context.getRecoveryService().markAsPossibleMatch(savedLost.getId(), savedFound.getId());
            assert context.getLostItemService().getById(savedLost.getId()).getStatus() == ItemStatus.POSSIBLE_MATCH;
            assert context.getFoundItemService().getById(savedFound.getId()).getStatus() == ItemStatus.POSSIBLE_MATCH;
            System.out.println("[Step 5] Status updated to POSSIBLE_MATCH for both items.");

            // 6. Update Status to VERIFIED
            context.getRecoveryService().markAsVerified(savedLost.getId(), savedFound.getId());
            assert context.getLostItemService().getById(savedLost.getId()).getStatus() == ItemStatus.VERIFIED;
            assert context.getFoundItemService().getById(savedFound.getId()).getStatus() == ItemStatus.VERIFIED;
            System.out.println("[Step 6] Status updated to VERIFIED for both items.");

            // 7. Complete Return & Generate RecoveryReport
            RecoveryReport rep = context.getRecoveryService().markAsReturned(
                    savedLost.getId(),
                    savedFound.getId(),
                    "Verified via student ID, serial number match, and answered verification question correctly."
            );
            System.out.println("[Step 7] Complete Return: Generated Recovery Report #" + rep.getReportId());
            assert rep.getReportId() != null && rep.getReportId().startsWith("REP-");
            assert context.getLostItemService().getById(savedLost.getId()).getStatus() == ItemStatus.RETURNED;
            assert context.getFoundItemService().getById(savedFound.getId()).getStatus() == ItemStatus.RETURNED;

            // 8. Search Verification
            List<Item> searchReturned = context.getSearchService().searchAll("AirPods", null, null, null, null, null);
            System.out.println("[Step 8] Search for 'AirPods': found " + searchReturned.size() + " items.");
            assert !searchReturned.isEmpty();

            System.out.println(">>> ALL WORKFLOW TESTS COMPLETED SUCCESSFULLY! <<<");

        } catch (Exception e) {
            System.err.println("TEST FAILURE: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (testDir.exists()) {
                for (java.io.File f : testDir.listFiles()) f.delete();
                testDir.delete();
            }
        }
    }
}
