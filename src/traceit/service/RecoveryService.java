package traceit.service;

import traceit.exception.ItemNotFoundException;
import traceit.exception.TraceItException;
import traceit.matching.MatchEngine;
import traceit.model.*;
import traceit.repository.FileRepository;
import traceit.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;

public class RecoveryService {

    private final LostItemService lostItemService;
    private final FoundItemService foundItemService;
    private final FileRepository repository;
    private final MatchEngine matchEngine;
    private final List<RecoveryReport> reports;

    public RecoveryService(LostItemService lostItemService, FoundItemService foundItemService, FileRepository repository) {
        this.lostItemService = lostItemService;
        this.foundItemService = foundItemService;
        this.repository = repository;
        this.matchEngine = new MatchEngine();
        this.reports = new ArrayList<>(repository.loadRecoveryReports());
        updateReportIdCounter();
    }

    private void updateReportIdCounter() {
        int maxId = 1000;
        for (RecoveryReport rep : reports) {
            try {
                if (rep.getReportId() != null && rep.getReportId().startsWith("REP-")) {
                    int num = Integer.parseInt(rep.getReportId().substring(4));
                    if (num > maxId) maxId = num;
                }
            } catch (Exception ignored) {}
        }
        IdGenerator.initializeCounters(1000, 1000, maxId);
    }

    public List<MatchResult> findMatchesForLostItem(String lostItemId) throws ItemNotFoundException {
        LostItem lost = lostItemService.getById(lostItemId);
        List<FoundItem> availableFound = foundItemService.getAllFoundItems();
        return matchEngine.findMatches(lost, availableFound);
    }

    public MatchResult analyzePair(String lostItemId, String foundItemId) throws ItemNotFoundException {
        LostItem lost = lostItemService.getById(lostItemId);
        FoundItem found = foundItemService.getById(foundItemId);
        return matchEngine.analyzePair(lost, found);
    }

    public void markAsPossibleMatch(String lostItemId, String foundItemId) throws ItemNotFoundException {
        lostItemService.updateStatus(lostItemId, ItemStatus.POSSIBLE_MATCH);
        foundItemService.updateStatus(foundItemId, ItemStatus.POSSIBLE_MATCH);
    }

    public void markAsVerified(String lostItemId, String foundItemId) throws ItemNotFoundException {
        lostItemService.updateStatus(lostItemId, ItemStatus.VERIFIED);
        foundItemService.updateStatus(foundItemId, ItemStatus.VERIFIED);
    }

    public RecoveryReport markAsReturned(String lostItemId, String foundItemId, String resolutionNotes) throws TraceItException {
        LostItem lost = lostItemService.getById(lostItemId);
        FoundItem found = foundItemService.getById(foundItemId);

        MatchResult matchResult = matchEngine.analyzePair(lost, found);

        lost.setStatus(ItemStatus.RETURNED);
        found.setStatus(ItemStatus.RETURNED);

        lostItemService.saveAll();
        foundItemService.saveAll();

        String reportId = IdGenerator.generateReportId();
        RecoveryReport report = new RecoveryReport(
                reportId,
                lost,
                found,
                matchResult.getTotalScore(),
                resolutionNotes != null && !resolutionNotes.trim().isEmpty()
                        ? resolutionNotes
                        : "Matched and returned to verified owner."
        );

        reports.add(report);
        repository.saveRecoveryReports(reports);
        return report;
    }

    public List<RecoveryReport> getAllRecoveryReports() {
        return new ArrayList<>(reports);
    }
}
