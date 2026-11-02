package traceit.model;

import java.time.LocalDateTime;

public class RecoveryReport {
    private String reportId;
    private String lostItemId;
    private String lostItemName;
    private String foundItemId;
    private String foundItemName;
    private int matchScore;
    private MatchLevel matchLevel;
    private LocalDateTime recoveryDate;
    private String ownerContact;
    private String finderContact;
    private String resolutionNotes;

    public RecoveryReport(String reportId, LostItem lostItem, FoundItem foundItem, int matchScore, String resolutionNotes) {
        this.reportId = reportId;
        this.lostItemId = lostItem.getId();
        this.lostItemName = lostItem.getName();
        this.foundItemId = foundItem.getId();
        this.foundItemName = foundItem.getName();
        this.matchScore = matchScore;
        this.matchLevel = MatchLevel.fromScore(matchScore);
        this.recoveryDate = LocalDateTime.now();
        this.ownerContact = lostItem.getReporter() != null ? lostItem.getReporter().toString() : "N/A";
        this.finderContact = foundItem.getReporter() != null ? foundItem.getReporter().toString() : "N/A";
        this.resolutionNotes = resolutionNotes;
    }

    public RecoveryReport(String reportId, String lostItemId, String lostItemName, String foundItemId,
                          String foundItemName, int matchScore, MatchLevel matchLevel,
                          LocalDateTime recoveryDate, String ownerContact, String finderContact, String resolutionNotes) {
        this.reportId = reportId;
        this.lostItemId = lostItemId;
        this.lostItemName = lostItemName;
        this.foundItemId = foundItemId;
        this.foundItemName = foundItemName;
        this.matchScore = matchScore;
        this.matchLevel = matchLevel;
        this.recoveryDate = recoveryDate;
        this.ownerContact = ownerContact;
        this.finderContact = finderContact;
        this.resolutionNotes = resolutionNotes;
    }

    public String getReportId() {
        return reportId;
    }

    public String getLostItemId() {
        return lostItemId;
    }

    public String getLostItemName() {
        return lostItemName;
    }

    public String getFoundItemId() {
        return foundItemId;
    }

    public String getFoundItemName() {
        return foundItemName;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public MatchLevel getMatchLevel() {
        return matchLevel;
    }

    public LocalDateTime getRecoveryDate() {
        return recoveryDate;
    }

    public String getOwnerContact() {
        return ownerContact;
    }

    public String getFinderContact() {
        return finderContact;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public String generateSummary() {
        return String.format("REPORT #%s | Lost: #%s (%s) <-> Found: #%s (%s) | Score: %d/100 (%s) | Date: %s",
                reportId, lostItemId, lostItemName, foundItemId, foundItemName, matchScore, matchLevel.getLabel(), recoveryDate.toString());
    }
}
