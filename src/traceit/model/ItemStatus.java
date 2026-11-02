package traceit.model;

public enum ItemStatus {
    LOST("Lost"),
    FOUND("Found"),
    POSSIBLE_MATCH("Possible Match"),
    VERIFIED("Verified Match"),
    RETURNED("Returned to Owner"),
    CLOSED("Closed / Unresolved");

    private final String displayName;

    ItemStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ItemStatus fromString(String text) {
        if (text == null) return LOST;
        for (ItemStatus status : ItemStatus.values()) {
            if (status.name().equalsIgnoreCase(text) || status.displayName.equalsIgnoreCase(text)) {
                return status;
            }
        }
        return LOST;
    }
}
