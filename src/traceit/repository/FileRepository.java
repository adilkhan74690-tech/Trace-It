package traceit.repository;

import traceit.model.*;
import traceit.util.DateTimeUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileRepository {

    private final String dataDirPath;
    private final String lostFilePath;
    private final String foundFilePath;
    private final String reportFilePath;

    public FileRepository() {
        this("data");
    }

    public FileRepository(String dataDirPath) {
        this.dataDirPath = dataDirPath;
        this.lostFilePath = dataDirPath + File.separator + "lost_items.txt";
        this.foundFilePath = dataDirPath + File.separator + "found_items.txt";
        this.reportFilePath = dataDirPath + File.separator + "recovery_reports.txt";
        ensureDataDirectoryExists();
    }

    private void ensureDataDirectoryExists() {
        try {
            Path path = Paths.get(dataDirPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            System.err.println("[Warning] Failed to create data directory: " + e.getMessage());
        }
    }

    public synchronized void saveLostItems(List<LostItem> items) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(lostFilePath))) {
            for (LostItem item : items) {
                bw.write(serializeLostItem(item));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[Error] Failed to save lost items: " + e.getMessage());
        }
    }

    public synchronized List<LostItem> loadLostItems() {
        List<LostItem> items = new ArrayList<>();
        File file = new File(lostFilePath);
        if (!file.exists()) return items;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                LostItem item = deserializeLostItem(line);
                if (item != null) items.add(item);
            }
        } catch (IOException e) {
            System.err.println("[Error] Failed to load lost items: " + e.getMessage());
        }
        return items;
    }

    private String serializeLostItem(LostItem item) {
        User u = item.getReporter() != null ? item.getReporter() : new User("U0", "Unknown", "", "", "Student");
        return String.join("|",
                escape(item.getId()),
                escape(item.getName()),
                escape(item.getCategory().name()),
                escape(item.getColor()),
                escape(item.getBrand()),
                escape(item.getLocation() != null ? item.getLocation().getRawLocation() : ""),
                escape(DateTimeUtils.formatIso(item.getDateTime())),
                escape(item.getDescription()),
                escape(item.getDistinctiveFeatures()),
                escape(item.getStatus().name()),
                escape(u.getId()),
                escape(u.getName()),
                escape(u.getEmail()),
                escape(u.getPhone()),
                escape(u.getRole()),
                String.valueOf(item.getRewardAmount()),
                escape(item.getContactPreference())
        );
    }

    private LostItem deserializeLostItem(String line) {
        try {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 17) return null;

            String id = unescape(parts[0]);
            String name = unescape(parts[1]);
            ItemCategory category = ItemCategory.fromString(unescape(parts[2]));
            String color = unescape(parts[3]);
            String brand = unescape(parts[4]);
            Location location = new Location(unescape(parts[5]));
            LocalDateTime dt = DateTimeUtils.parse(unescape(parts[6]));
            String description = unescape(parts[7]);
            String distinctiveFeatures = unescape(parts[8]);
            ItemStatus status = ItemStatus.fromString(unescape(parts[9]));

            User user = new User(unescape(parts[10]), unescape(parts[11]), unescape(parts[12]), unescape(parts[13]), unescape(parts[14]));
            double reward = Double.parseDouble(parts[15]);
            String contactPref = unescape(parts[16]);

            LostItem item = new LostItem(id, name, category, color, brand, location, dt, description, distinctiveFeatures, user, reward, contactPref);
            item.setStatus(status);
            return item;
        } catch (Exception e) {
            return null;
        }
    }

    public synchronized void saveFoundItems(List<FoundItem> items) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(foundFilePath))) {
            for (FoundItem item : items) {
                bw.write(serializeFoundItem(item));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[Error] Failed to save found items: " + e.getMessage());
        }
    }

    public synchronized List<FoundItem> loadFoundItems() {
        List<FoundItem> items = new ArrayList<>();
        File file = new File(foundFilePath);
        if (!file.exists()) return items;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                FoundItem item = deserializeFoundItem(line);
                if (item != null) items.add(item);
            }
        } catch (IOException e) {
            System.err.println("[Error] Failed to load found items: " + e.getMessage());
        }
        return items;
    }

    private String serializeFoundItem(FoundItem item) {
        User u = item.getReporter() != null ? item.getReporter() : new User("U0", "Unknown", "", "", "Student");
        return String.join("|",
                escape(item.getId()),
                escape(item.getName()),
                escape(item.getCategory().name()),
                escape(item.getColor()),
                escape(item.getBrand()),
                escape(item.getLocation() != null ? item.getLocation().getRawLocation() : ""),
                escape(DateTimeUtils.formatIso(item.getDateTime())),
                escape(item.getDescription()),
                escape(item.getDistinctiveFeatures()),
                escape(item.getStatus().name()),
                escape(u.getId()),
                escape(u.getName()),
                escape(u.getEmail()),
                escape(u.getPhone()),
                escape(u.getRole()),
                escape(item.getStorageLocation()),
                escape(item.getVerificationQuestion())
        );
    }

    private FoundItem deserializeFoundItem(String line) {
        try {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 17) return null;

            String id = unescape(parts[0]);
            String name = unescape(parts[1]);
            ItemCategory category = ItemCategory.fromString(unescape(parts[2]));
            String color = unescape(parts[3]);
            String brand = unescape(parts[4]);
            Location location = new Location(unescape(parts[5]));
            LocalDateTime dt = DateTimeUtils.parse(unescape(parts[6]));
            String description = unescape(parts[7]);
            String distinctiveFeatures = unescape(parts[8]);
            ItemStatus status = ItemStatus.fromString(unescape(parts[9]));

            User user = new User(unescape(parts[10]), unescape(parts[11]), unescape(parts[12]), unescape(parts[13]), unescape(parts[14]));
            String storage = unescape(parts[15]);
            String vQuestion = unescape(parts[16]);

            FoundItem item = new FoundItem(id, name, category, color, brand, location, dt, description, distinctiveFeatures, user, storage, vQuestion);
            item.setStatus(status);
            return item;
        } catch (Exception e) {
            return null;
        }
    }

    public synchronized void saveRecoveryReports(List<RecoveryReport> reports) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(reportFilePath))) {
            for (RecoveryReport r : reports) {
                bw.write(serializeReport(r));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[Error] Failed to save recovery reports: " + e.getMessage());
        }
    }

    public synchronized List<RecoveryReport> loadRecoveryReports() {
        List<RecoveryReport> reports = new ArrayList<>();
        File file = new File(reportFilePath);
        if (!file.exists()) return reports;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                RecoveryReport r = deserializeReport(line);
                if (r != null) reports.add(r);
            }
        } catch (IOException e) {
            System.err.println("[Error] Failed to load recovery reports: " + e.getMessage());
        }
        return reports;
    }

    private String serializeReport(RecoveryReport r) {
        return String.join("|",
                escape(r.getReportId()),
                escape(r.getLostItemId()),
                escape(r.getLostItemName()),
                escape(r.getFoundItemId()),
                escape(r.getFoundItemName()),
                String.valueOf(r.getMatchScore()),
                escape(r.getMatchLevel().name()),
                escape(DateTimeUtils.formatIso(r.getRecoveryDate())),
                escape(r.getOwnerContact()),
                escape(r.getFinderContact()),
                escape(r.getResolutionNotes())
        );
    }

    private RecoveryReport deserializeReport(String line) {
        try {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 11) return null;

            String reportId = unescape(parts[0]);
            String lostId = unescape(parts[1]);
            String lostName = unescape(parts[2]);
            String foundId = unescape(parts[3]);
            String foundName = unescape(parts[4]);
            int score = Integer.parseInt(parts[5]);
            MatchLevel level = MatchLevel.valueOf(parts[6]);
            LocalDateTime date = DateTimeUtils.parse(unescape(parts[7]));
            String ownerContact = unescape(parts[8]);
            String finderContact = unescape(parts[9]);
            String notes = unescape(parts[10]);

            return new RecoveryReport(reportId, lostId, lostName, foundId, foundName, score, level, date, ownerContact, finderContact, notes);
        } catch (Exception e) {
            return null;
        }
    }

    private String escape(String input) {
        if (input == null) return "";
        return input.replace("\n", "\\n").replace("\r", "").replace("|", "\\p");
    }

    private String unescape(String input) {
        if (input == null) return "";
        return input.replace("\\n", "\n").replace("\\p", "|");
    }
}
