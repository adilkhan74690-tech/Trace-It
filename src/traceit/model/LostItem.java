package traceit.model;

import java.time.LocalDateTime;

public class LostItem extends Item {
    private double rewardAmount;
    private String contactPreference;

    public LostItem() {
        super();
        setStatus(ItemStatus.LOST);
    }

    public LostItem(String id, String name, ItemCategory category, String color, String brand,
                    Location location, LocalDateTime dateTime, String description,
                    String distinctiveFeatures, User reporter, double rewardAmount, String contactPreference) {
        super(id, name, category, color, brand, location, dateTime, description, distinctiveFeatures, ItemStatus.LOST, reporter);
        this.rewardAmount = rewardAmount;
        this.contactPreference = contactPreference;
    }

    public double getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(double rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    public String getContactPreference() {
        return contactPreference;
    }

    public void setContactPreference(String contactPreference) {
        this.contactPreference = contactPreference;
    }

    @Override
    public String getReportType() {
        return "LOST";
    }
}
