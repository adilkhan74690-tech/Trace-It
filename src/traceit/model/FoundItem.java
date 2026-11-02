package traceit.model;

import java.time.LocalDateTime;

public class FoundItem extends Item {
    private String storageLocation;
    private String verificationQuestion;

    public FoundItem() {
        super();
        setStatus(ItemStatus.FOUND);
    }

    public FoundItem(String id, String name, ItemCategory category, String color, String brand,
                     Location location, LocalDateTime dateTime, String description,
                     String distinctiveFeatures, User reporter, String storageLocation, String verificationQuestion) {
        super(id, name, category, color, brand, location, dateTime, description, distinctiveFeatures, ItemStatus.FOUND, reporter);
        this.storageLocation = storageLocation;
        this.verificationQuestion = verificationQuestion;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getVerificationQuestion() {
        return verificationQuestion;
    }

    public void setVerificationQuestion(String verificationQuestion) {
        this.verificationQuestion = verificationQuestion;
    }

    @Override
    public String getReportType() {
        return "FOUND";
    }
}
