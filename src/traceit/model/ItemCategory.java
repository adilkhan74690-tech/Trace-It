package traceit.model;

public enum ItemCategory {
    WALLET("Wallet / Purse"),
    SMARTPHONE("Smartphone / Mobile Device"),
    ID_CARD("ID Card / Documents"),
    EARBUDS("Earbuds / Headphones"),
    LAPTOP_CHARGER("Laptop / Electronics Charger"),
    WATCH("Watch / Smartwatch"),
    WATER_BOTTLE("Water Bottle / Tumbler"),
    BACKPACK("Backpack / Bag"),
    KEYS("Keys / Keychains"),
    CLOTHING("Clothing / Eyewear"),
    OTHER("Other Miscellaneous");

    private final String displayName;

    ItemCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ItemCategory fromString(String text) {
        if (text == null || text.trim().isEmpty()) return OTHER;
        String clean = text.trim().toUpperCase().replace(" ", "_");
        for (ItemCategory cat : ItemCategory.values()) {
            if (cat.name().equalsIgnoreCase(clean) || cat.displayName.equalsIgnoreCase(text)) {
                return cat;
            }
        }
        for (ItemCategory cat : ItemCategory.values()) {
            if (cat.displayName.toLowerCase().contains(text.toLowerCase()) || text.toLowerCase().contains(cat.name().toLowerCase())) {
                return cat;
            }
        }
        return OTHER;
    }
}
