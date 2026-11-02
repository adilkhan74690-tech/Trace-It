package traceit.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Location {
    private String rawLocation;
    private String buildingOrZone;
    private String areaDetail;

    public Location(String rawLocation) {
        this.rawLocation = rawLocation != null ? rawLocation.trim() : "";
        extractBuildingAndArea();
    }

    public Location(String buildingOrZone, String areaDetail) {
        this.buildingOrZone = buildingOrZone != null ? buildingOrZone.trim() : "";
        this.areaDetail = areaDetail != null ? areaDetail.trim() : "";
        this.rawLocation = (this.buildingOrZone + " " + this.areaDetail).trim();
    }

    private void extractBuildingAndArea() {
        if (rawLocation.isEmpty()) {
            this.buildingOrZone = "Unknown";
            this.areaDetail = "";
            return;
        }

        String lower = rawLocation.toLowerCase();
        if (lower.contains("library")) {
            this.buildingOrZone = "Library";
        } else if (lower.contains("cafeteria") || lower.contains("canteen") || lower.contains("food court")) {
            this.buildingOrZone = "Cafeteria / Food Court";
        } else if (lower.contains("lab") || lower.contains("computer lab") || lower.contains("science block")) {
            this.buildingOrZone = "Science & Tech Labs";
        } else if (lower.contains("auditorium") || lower.contains("main hall") || lower.contains("hall")) {
            this.buildingOrZone = "Main Auditorium";
        } else if (lower.contains("sports") || lower.contains("gym") || lower.contains("ground") || lower.contains("stadium")) {
            this.buildingOrZone = "Sports Complex";
        } else if (lower.contains("hostel") || lower.contains("dorm")) {
            this.buildingOrZone = "Student Hostel";
        } else if (lower.contains("admin") || lower.contains("office")) {
            this.buildingOrZone = "Admin Building";
        } else {
            String[] parts = rawLocation.split("[-,]", 2);
            this.buildingOrZone = parts[0].trim();
            this.areaDetail = parts.length > 1 ? parts[1].trim() : "";
            return;
        }

        this.areaDetail = rawLocation;
    }

    public String getRawLocation() {
        return rawLocation;
    }

    public void setRawLocation(String rawLocation) {
        this.rawLocation = rawLocation;
        extractBuildingAndArea();
    }

    public String getBuildingOrZone() {
        return buildingOrZone;
    }

    public String getAreaDetail() {
        return areaDetail;
    }

    public double calculateSimilarity(Location other) {
        if (other == null || other.rawLocation == null || this.rawLocation.isEmpty()) {
            return 0.0;
        }

        String s1 = this.rawLocation.trim().toLowerCase();
        String s2 = other.rawLocation.trim().toLowerCase();
        if (s1.equals(s2)) {
            return 1.0;
        }

        boolean sameZone = this.buildingOrZone.equalsIgnoreCase(other.buildingOrZone);

        if (s1.contains(s2) || s2.contains(s1)) {
            return sameZone ? 0.95 : 0.85;
        }

        Set<String> tokens1 = cleanTokens(s1);
        Set<String> tokens2 = cleanTokens(s2);

        if (tokens1.isEmpty() || tokens2.isEmpty()) {
            return 0.0;
        }

        Set<String> intersection = new HashSet<>(tokens1);
        intersection.retainAll(tokens2);

        Set<String> union = new HashSet<>(tokens1);
        union.addAll(tokens2);

        double jaccard = (double) intersection.size() / union.size();

        if (sameZone) {
            return Math.min(1.0, 0.70 + (jaccard * 0.30));
        }

        return jaccard;
    }

    private Set<String> cleanTokens(String input) {
        Set<String> set = new HashSet<>();
        String[] words = input.replaceAll("[^a-zA-Z0-9\\s]", " ").split("\\s+");
        Set<String> stopwords = new HashSet<>(Arrays.asList("near", "the", "at", "in", "by", "of", "and", "or", "block", "floor"));
        for (String w : words) {
            if (w.length() > 1 && !stopwords.contains(w)) {
                set.add(w);
            }
        }
        return set;
    }

    @Override
    public String toString() {
        return rawLocation;
    }
}
