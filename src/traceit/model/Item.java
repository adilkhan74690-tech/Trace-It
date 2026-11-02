package traceit.model;

import java.time.LocalDateTime;

public abstract class Item {
    private String id;
    private String name;
    private ItemCategory category;
    private String color;
    private String brand;
    private Location location;
    private LocalDateTime dateTime;
    private String description;
    private String distinctiveFeatures;
    private ItemStatus status;
    private User reporter;

    public Item() {
    }

    public Item(String id, String name, ItemCategory category, String color, String brand,
                Location location, LocalDateTime dateTime, String description,
                String distinctiveFeatures, ItemStatus status, User reporter) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.color = color;
        this.brand = brand;
        this.location = location;
        this.dateTime = dateTime;
        this.description = description;
        this.distinctiveFeatures = distinctiveFeatures;
        this.status = status;
        this.reporter = reporter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public void setCategory(ItemCategory category) {
        this.category = category;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDistinctiveFeatures() {
        return distinctiveFeatures;
    }

    public void setDistinctiveFeatures(String distinctiveFeatures) {
        this.distinctiveFeatures = distinctiveFeatures;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public abstract String getReportType();

    @Override
    public String toString() {
        return "[" + getReportType() + " #" + id + "] " + name + " (" + category.getDisplayName() + ") - "
                + color + " " + brand + " @ " + location.getRawLocation() + " [" + status.getDisplayName() + "]";
    }
}
