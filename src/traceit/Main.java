package traceit;

import traceit.exception.TraceItException;
import traceit.model.*;
import traceit.repository.FileRepository;
import traceit.service.*;
import traceit.util.InputValidator;
import traceit.util.SampleDataGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {

    private final FileRepository repository;
    private final LostItemService lostItemService;
    private final FoundItemService foundItemService;
    private final RecoveryService recoveryService;
    private final SearchService searchService;
    private final Scanner scanner;

    public Main() {
        this.repository = new FileRepository("data");
        this.lostItemService = new LostItemService(repository);
        this.foundItemService = new FoundItemService(repository);
        this.recoveryService = new RecoveryService(lostItemService, foundItemService, repository);
        this.searchService = new SearchService(lostItemService, foundItemService);
        this.scanner = new Scanner(System.in);

        seedDataIfEmpty();
    }

    private void seedDataIfEmpty() {
        if (lostItemService.getAllLostItems().isEmpty() && foundItemService.getAllFoundItems().isEmpty()) {
            lostItemService.seedSampleData(SampleDataGenerator.generateSampleLostItems());
            foundItemService.seedSampleData(SampleDataGenerator.generateSampleFoundItems());
        }
    }

    public void run() {
        printBanner();

        while (true) {
            printMainMenu();
            int choice = InputValidator.readInt(scanner, "Select an option (1-11): ", 1, 11);
            System.out.println();

            try {
                switch (choice) {
                    case 1:
                        reportLostItem();
                        break;
                    case 2:
                        reportFoundItem();
                        break;
                    case 3:
                        findPossibleMatches();
                        break;
                    case 4:
                        viewMatchAnalysis();
                        break;
                    case 5:
                        searchItems();
                        break;
                    case 6:
                        viewLostItems();
                        break;
                    case 7:
                        viewFoundItems();
                        break;
                    case 8:
                        updateItemStatus();
                        break;
                    case 9:
                        viewRecoveryReports();
                        break;
                    case 10:
                        runDemoFlow();
                        break;
                    case 11:
                        System.out.println("==================================================");
                        System.out.println(" Thank you for using TraceIt!");
                        System.out.println(" Tagline: \"Lost it? Trace it.\"");
                        System.out.println("==================================================");
                        return;
                }
            } catch (TraceItException e) {
                System.out.println("[Error] " + e.getMessage());
            } catch (Exception e) {
                System.out.println("[Error] Unexpected error: " + e.getMessage());
            }

            System.out.println("\nPress ENTER to return to the main menu...");
            scanner.nextLine();
        }
    }

    private void printBanner() {
        System.out.println("==================================================");
        System.out.println("                  TRACEIT                         ");
        System.out.println("     Smart Lost-Item Recovery & Matching Engine   ");
        System.out.println("               \"Lost it? Trace it.\"               ");
        System.out.println("==================================================");
    }

    private void printMainMenu() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("                    MAIN MENU                     ");
        System.out.println("--------------------------------------------------");
        System.out.println(" 1. Report Lost Item");
        System.out.println(" 2. Report Found Item");
        System.out.println(" 3. Find Possible Matches");
        System.out.println(" 4. View Detailed Match Analysis");
        System.out.println(" 5. Search Items");
        System.out.println(" 6. View All Lost Items");
        System.out.println(" 7. View All Found Items");
        System.out.println(" 8. Update Item Status");
        System.out.println(" 9. View Recovery Reports");
        System.out.println("10. Run Demo Workflow");
        System.out.println("11. Exit");
        System.out.println("--------------------------------------------------");
    }

    private void reportLostItem() throws TraceItException {
        System.out.println("--- [1] REPORT LOST ITEM ---");
        String name = InputValidator.readNonEmptyString(scanner, "Item name: ");
        ItemCategory category = InputValidator.readCategory(scanner, "Select category:");
        String color = InputValidator.readNonEmptyString(scanner, "Color: ");
        String brand = InputValidator.readOptionalString(scanner, "Brand / Manufacturer", "Generic");
        String locationStr = InputValidator.readNonEmptyString(scanner, "Lost location: ");
        LocalDateTime dt = InputValidator.readDateTime(scanner, "Estimated date/time lost");
        String description = InputValidator.readNonEmptyString(scanner, "Description: ");
        String features = InputValidator.readOptionalString(scanner, "Distinctive features", "None");

        System.out.println("\n--- Reporter Contact Details ---");
        String userName = InputValidator.readNonEmptyString(scanner, "Your Name: ");
        String userEmail = InputValidator.readNonEmptyString(scanner, "Your Email: ");
        String userPhone = InputValidator.readNonEmptyString(scanner, "Your Phone: ");
        String userRole = InputValidator.readOptionalString(scanner, "Role (Student/Faculty/Staff)", "Student");
        double reward = InputValidator.readDouble(scanner, "Reward amount ($): ", 0.0);
        String contactPref = InputValidator.readOptionalString(scanner, "Contact preference", "Email");

        User reporter = new User("U-" + System.currentTimeMillis() % 10000, userName, userEmail, userPhone, userRole);
        LostItem lostItem = new LostItem(null, name, category, color, brand, new Location(locationStr), dt, description, features, reporter, reward, contactPref);

        LostItem saved = lostItemService.addLostItem(lostItem);
        System.out.println("\n[Success] Lost Item reported! Assigned ID: #" + saved.getId());
    }

    private void reportFoundItem() throws TraceItException {
        System.out.println("--- [2] REPORT FOUND ITEM ---");
        String name = InputValidator.readNonEmptyString(scanner, "Item name: ");
        ItemCategory category = InputValidator.readCategory(scanner, "Select category:");
        String color = InputValidator.readNonEmptyString(scanner, "Color: ");
        String brand = InputValidator.readOptionalString(scanner, "Brand / Manufacturer", "Generic");
        String locationStr = InputValidator.readNonEmptyString(scanner, "Found location: ");
        LocalDateTime dt = InputValidator.readDateTime(scanner, "Date/time found");
        String description = InputValidator.readNonEmptyString(scanner, "Description: ");
        String features = InputValidator.readOptionalString(scanner, "Distinctive features", "None");

        System.out.println("\n--- Finder Details ---");
        String userName = InputValidator.readNonEmptyString(scanner, "Finder Name: ");
        String userEmail = InputValidator.readNonEmptyString(scanner, "Finder Email: ");
        String userPhone = InputValidator.readNonEmptyString(scanner, "Finder Phone: ");
        String userRole = InputValidator.readOptionalString(scanner, "Role (Student/Faculty/Staff)", "Staff");
        String storage = InputValidator.readNonEmptyString(scanner, "Storage location: ");
        String vQuestion = InputValidator.readOptionalString(scanner, "Verification question for claim", "Describe inner contents");

        User reporter = new User("F-" + System.currentTimeMillis() % 10000, userName, userEmail, userPhone, userRole);
        FoundItem foundItem = new FoundItem(null, name, category, color, brand, new Location(locationStr), dt, description, features, reporter, storage, vQuestion);

        FoundItem saved = foundItemService.addFoundItem(foundItem);
        System.out.println("\n[Success] Found Item reported! Assigned ID: #" + saved.getId());
    }

    private void findPossibleMatches() throws TraceItException {
        System.out.println("--- [3] FIND POSSIBLE MATCHES ---");
        List<LostItem> lostList = lostItemService.getAllLostItems();
        if (lostList.isEmpty()) {
            System.out.println("No lost items registered.");
            return;
        }

        System.out.println("Select a Lost Item:");
        for (int i = 0; i < lostList.size(); i++) {
            System.out.printf("  %2d. %s\n", (i + 1), lostList.get(i).toString());
        }

        int choice = InputValidator.readInt(scanner, "Enter choice (1-" + lostList.size() + "): ", 1, lostList.size());
        LostItem selectedLost = lostList.get(choice - 1);

        System.out.println("\nRunning Match Engine for Lost Item #" + selectedLost.getId() + "...");
        List<MatchResult> matches = recoveryService.findMatchesForLostItem(selectedLost.getId());

        if (matches.isEmpty()) {
            System.out.println("No potential matches found.");
            return;
        }

        System.out.println("\n==================================================");
        System.out.println("           RANKED MATCHING RESULTS               ");
        System.out.println("==================================================");
        System.out.printf("%-4s | %-10s | %-25s | %-12s | %-18s\n", "Rank", "Found ID", "Item Name", "Score", "Match Level");
        System.out.println("----------------------------------------------------------------------------------");

        for (int i = 0; i < matches.size(); i++) {
            MatchResult res = matches.get(i);
            FoundItem f = res.getFoundItem();
            System.out.printf("%-4d | %-10s | %-25s | %3d / 100  | %-18s\n",
                    (i + 1), f.getId(), f.getName(), res.getTotalScore(), res.getMatchLevel().getLabel());
        }
        System.out.println("----------------------------------------------------------------------------------");

        String analyzeChoice = InputValidator.readOptionalString(scanner, "Enter Found Item ID to view full score breakdown (or press Enter to skip)", "");
        if (!analyzeChoice.isEmpty()) {
            MatchResult pairResult = recoveryService.analyzePair(selectedLost.getId(), analyzeChoice);
            System.out.println("\n" + pairResult.generateExplainableAnalysisReport());

            String action = InputValidator.readOptionalString(scanner, "Mark this pair as POSSIBLE_MATCH status? (Y/N)", "N");
            if (action.equalsIgnoreCase("Y")) {
                recoveryService.markAsPossibleMatch(selectedLost.getId(), pairResult.getFoundItem().getId());
                System.out.println("[Success] Status updated to POSSIBLE_MATCH for both items.");
            }
        }
    }

    private void viewMatchAnalysis() throws TraceItException {
        System.out.println("--- [4] VIEW DETAILED MATCH ANALYSIS ---");
        String lostId = InputValidator.readNonEmptyString(scanner, "Enter Lost Item ID (e.g. LST-1001): ");
        String foundId = InputValidator.readNonEmptyString(scanner, "Enter Found Item ID (e.g. FND-1001): ");

        MatchResult result = recoveryService.analyzePair(lostId, foundId);
        System.out.println("\n" + result.generateExplainableAnalysisReport());
    }

    private void searchItems() {
        System.out.println("--- [5] MULTI-ATTRIBUTE SEARCH ---");
        String keyword = InputValidator.readOptionalString(scanner, "Keyword search (or Enter to skip)", "");
        String color = InputValidator.readOptionalString(scanner, "Color filter (or Enter to skip)", "");
        String brand = InputValidator.readOptionalString(scanner, "Brand filter (or Enter to skip)", "");
        String location = InputValidator.readOptionalString(scanner, "Location filter (or Enter to skip)", "");

        System.out.println("Category filter?");
        System.out.println("  0. All Categories");
        ItemCategory[] cats = ItemCategory.values();
        for (int i = 0; i < cats.length; i++) {
            System.out.printf("  %2d. %s\n", (i + 1), cats[i].getDisplayName());
        }
        int catChoice = InputValidator.readInt(scanner, "Select category (0-" + cats.length + "): ", 0, cats.length);
        ItemCategory selectedCat = catChoice == 0 ? null : cats[catChoice - 1];

        List<Item> searchResults = searchService.searchAll(keyword, selectedCat, color, brand, location, null);

        System.out.println("\nFound " + searchResults.size() + " matching item(s):");
        System.out.println("----------------------------------------------------------------------------------");
        for (Item item : searchResults) {
            System.out.println("• " + item.toString());
        }
        System.out.println("----------------------------------------------------------------------------------");
    }

    private void viewLostItems() {
        System.out.println("--- [6] VIEW ALL LOST ITEMS ---");
        List<LostItem> items = lostItemService.getAllLostItems();
        if (items.isEmpty()) {
            System.out.println("No lost items found.");
            return;
        }
        for (LostItem item : items) {
            System.out.println("• " + item.toString() + " | Reward: $" + item.getRewardAmount());
        }
    }

    private void viewFoundItems() {
        System.out.println("--- [7] VIEW ALL FOUND ITEMS ---");
        List<FoundItem> items = foundItemService.getAllFoundItems();
        if (items.isEmpty()) {
            System.out.println("No found items found.");
            return;
        }
        for (FoundItem item : items) {
            System.out.println("• " + item.toString() + " | Storage: " + item.getStorageLocation());
        }
    }

    private void updateItemStatus() throws TraceItException {
        System.out.println("--- [8] UPDATE ITEM STATUS ---");
        String type = InputValidator.readNonEmptyString(scanner, "Item type (LOST / FOUND): ");
        String id = InputValidator.readNonEmptyString(scanner, "Enter Item ID: ");

        System.out.println("Select new status:");
        ItemStatus[] statuses = ItemStatus.values();
        for (int i = 0; i < statuses.length; i++) {
            System.out.printf("  %2d. %s\n", (i + 1), statuses[i].getDisplayName());
        }
        int choice = InputValidator.readInt(scanner, "Choice (1-" + statuses.length + "): ", 1, statuses.length);
        ItemStatus newStatus = statuses[choice - 1];

        if (type.equalsIgnoreCase("LOST")) {
            lostItemService.updateStatus(id, newStatus);
            System.out.println("[Success] Lost Item #" + id + " updated to: " + newStatus.getDisplayName());
        } else if (type.equalsIgnoreCase("FOUND")) {
            foundItemService.updateStatus(id, newStatus);
            System.out.println("[Success] Found Item #" + id + " updated to: " + newStatus.getDisplayName());
        } else {
            System.out.println("[Error] Invalid item type specified.");
        }
    }

    private void viewRecoveryReports() {
        System.out.println("--- [9] RECOVERY REPORTS ---");
        List<RecoveryReport> reports = recoveryService.getAllRecoveryReports();
        if (reports.isEmpty()) {
            System.out.println("No recovery reports found.");
            return;
        }
        for (RecoveryReport r : reports) {
            System.out.println("--------------------------------------------------");
            System.out.println(r.generateSummary());
            System.out.println("Notes: " + r.getResolutionNotes());
        }
        System.out.println("--------------------------------------------------");
    }

    private void runDemoFlow() throws TraceItException {
        System.out.println("==================================================");
        System.out.println("           TRACEIT DEMO WORKFLOW                  ");
        System.out.println("==================================================");

        System.out.println("\n[1] Target Lost Item LST-1001:");
        LostItem lost = lostItemService.getById("LST-1001");
        System.out.println("Lost Item: " + lost);

        System.out.println("\n[2] Found items in repository: " + foundItemService.getAllFoundItems().size());

        System.out.println("\n[3] Running Match Engine evaluation...");
        List<MatchResult> matches = recoveryService.findMatchesForLostItem("LST-1001");

        System.out.println("\n[4] Ranked Match Results:");
        for (int i = 0; i < matches.size(); i++) {
            MatchResult mr = matches.get(i);
            System.out.printf("  Rank %d: Found #%s (%s) -> Score: %d/100 [%s]\n",
                    (i + 1), mr.getFoundItem().getId(), mr.getFoundItem().getName(), mr.getTotalScore(), mr.getMatchLevel().getLabel());
        }

        System.out.println("\n[5] Analysis Breakdown for Top Result (FND-1001):");
        MatchResult topMatch = matches.get(0);
        System.out.println(topMatch.generateExplainableAnalysisReport());

        System.out.println("\n[6] Updating status to POSSIBLE_MATCH...");
        recoveryService.markAsPossibleMatch(lost.getId(), topMatch.getFoundItem().getId());

        System.out.println("\n[7] Verifying match...");
        recoveryService.markAsVerified(lost.getId(), topMatch.getFoundItem().getId());

        System.out.println("\n[8] Marking RETURNED and generating recovery report...");
        RecoveryReport report = recoveryService.markAsReturned(
                lost.getId(),
                topMatch.getFoundItem().getId(),
                "Owner verified via student ID photo & matched silver zip."
        );

        System.out.println("\nGenerated Report:\n" + report.generateSummary());
        System.out.println("==================================================");
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--cli")) {
            Main app = new Main();
            app.run();
        } else {
            traceit.ui.TraceItApp.launch();
        }
    }
}
