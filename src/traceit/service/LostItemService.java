package traceit.service;

import traceit.exception.InvalidItemException;
import traceit.exception.ItemNotFoundException;
import traceit.model.ItemStatus;
import traceit.model.LostItem;
import traceit.repository.FileRepository;
import traceit.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;

public class LostItemService {

    private final List<LostItem> lostItems;
    private final FileRepository repository;

    public LostItemService(FileRepository repository) {
        this.repository = repository;
        this.lostItems = new ArrayList<>(repository.loadLostItems());
        updateIdGeneratorCounter();
    }

    private void updateIdGeneratorCounter() {
        int maxId = 1000;
        for (LostItem item : lostItems) {
            try {
                if (item.getId() != null && item.getId().startsWith("LST-")) {
                    int num = Integer.parseInt(item.getId().substring(4));
                    if (num > maxId) maxId = num;
                }
            } catch (Exception ignored) {}
        }
        IdGenerator.initializeCounters(maxId, 1000, 1000);
    }

    public synchronized LostItem addLostItem(LostItem item) throws InvalidItemException {
        validateItem(item);
        if (item.getId() == null || item.getId().isEmpty()) {
            item.setId(IdGenerator.generateLostItemId());
        }
        lostItems.add(item);
        repository.saveLostItems(lostItems);
        return item;
    }

    public synchronized List<LostItem> getAllLostItems() {
        return new ArrayList<>(lostItems);
    }

    public synchronized LostItem getById(String id) throws ItemNotFoundException {
        for (LostItem item : lostItems) {
            if (item.getId().equalsIgnoreCase(id)) {
                return item;
            }
        }
        throw new ItemNotFoundException("Lost item with ID '" + id + "' not found.");
    }

    public synchronized boolean updateStatus(String id, ItemStatus status) throws ItemNotFoundException {
        LostItem item = getById(id);
        item.setStatus(status);
        repository.saveLostItems(lostItems);
        return true;
    }

    public synchronized void saveAll() {
        repository.saveLostItems(lostItems);
    }

    public synchronized void seedSampleData(List<LostItem> sampleList) {
        if (lostItems.isEmpty()) {
            lostItems.addAll(sampleList);
            repository.saveLostItems(lostItems);
            updateIdGeneratorCounter();
        }
    }

    private void validateItem(LostItem item) throws InvalidItemException {
        if (item == null) {
            throw new InvalidItemException("Item object cannot be null.");
        }
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            throw new InvalidItemException("Item name is required.");
        }
        if (item.getCategory() == null) {
            throw new InvalidItemException("Item category is required.");
        }
        if (item.getLocation() == null || item.getLocation().getRawLocation().isEmpty()) {
            throw new InvalidItemException("Item location is required.");
        }
    }
}
