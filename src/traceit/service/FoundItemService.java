package traceit.service;

import traceit.exception.InvalidItemException;
import traceit.exception.ItemNotFoundException;
import traceit.model.FoundItem;
import traceit.model.ItemStatus;
import traceit.repository.FileRepository;
import traceit.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;

public class FoundItemService {

    private final List<FoundItem> foundItems;
    private final FileRepository repository;

    public FoundItemService(FileRepository repository) {
        this.repository = repository;
        this.foundItems = new ArrayList<>(repository.loadFoundItems());
        updateIdGeneratorCounter();
    }

    private void updateIdGeneratorCounter() {
        int maxId = 1000;
        for (FoundItem item : foundItems) {
            try {
                if (item.getId() != null && item.getId().startsWith("FND-")) {
                    int num = Integer.parseInt(item.getId().substring(4));
                    if (num > maxId) maxId = num;
                }
            } catch (Exception ignored) {}
        }
        IdGenerator.initializeCounters(1000, maxId, 1000);
    }

    public synchronized FoundItem addFoundItem(FoundItem item) throws InvalidItemException {
        validateItem(item);
        if (item.getId() == null || item.getId().isEmpty()) {
            item.setId(IdGenerator.generateFoundItemId());
        }
        foundItems.add(item);
        repository.saveFoundItems(foundItems);
        return item;
    }

    public synchronized List<FoundItem> getAllFoundItems() {
        return new ArrayList<>(foundItems);
    }

    public synchronized FoundItem getById(String id) throws ItemNotFoundException {
        for (FoundItem item : foundItems) {
            if (item.getId().equalsIgnoreCase(id)) {
                return item;
            }
        }
        throw new ItemNotFoundException("Found item with ID '" + id + "' not found.");
    }

    public synchronized boolean updateStatus(String id, ItemStatus status) throws ItemNotFoundException {
        FoundItem item = getById(id);
        item.setStatus(status);
        repository.saveFoundItems(foundItems);
        return true;
    }

    public synchronized void saveAll() {
        repository.saveFoundItems(foundItems);
    }

    public synchronized void seedSampleData(List<FoundItem> sampleList) {
        if (foundItems.isEmpty()) {
            foundItems.addAll(sampleList);
            repository.saveFoundItems(foundItems);
            updateIdGeneratorCounter();
        }
    }

    private void validateItem(FoundItem item) throws InvalidItemException {
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
