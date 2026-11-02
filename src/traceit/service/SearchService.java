package traceit.service;

import traceit.model.Item;
import traceit.model.ItemCategory;
import traceit.model.ItemStatus;

import java.util.ArrayList;
import java.util.List;

public class SearchService {

    private final LostItemService lostItemService;
    private final FoundItemService foundItemService;

    public SearchService(LostItemService lostItemService, FoundItemService foundItemService) {
        this.lostItemService = lostItemService;
        this.foundItemService = foundItemService;
    }

    public List<Item> searchAll(String keyword, ItemCategory category, String color, String brand, String location, ItemStatus status) {
        List<Item> results = new ArrayList<>();
        List<Item> allItems = new ArrayList<>();
        allItems.addAll(lostItemService.getAllLostItems());
        allItems.addAll(foundItemService.getAllFoundItems());

        for (Item item : allItems) {
            if (matchesCriteria(item, keyword, category, color, brand, location, status)) {
                results.add(item);
            }
        }
        return results;
    }

    private boolean matchesCriteria(Item item, String keyword, ItemCategory category, String color, String brand, String location, ItemStatus status) {
        if (category != null && item.getCategory() != category) {
            return false;
        }

        if (status != null && item.getStatus() != status) {
            return false;
        }

        if (color != null && !color.trim().isEmpty()) {
            if (item.getColor() == null || !item.getColor().toLowerCase().contains(color.toLowerCase().trim())) {
                return false;
            }
        }

        if (brand != null && !brand.trim().isEmpty()) {
            if (item.getBrand() == null || !item.getBrand().toLowerCase().contains(brand.toLowerCase().trim())) {
                return false;
            }
        }

        if (location != null && !location.trim().isEmpty()) {
            if (item.getLocation() == null || !item.getLocation().getRawLocation().toLowerCase().contains(location.toLowerCase().trim())) {
                return false;
            }
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.toLowerCase().trim();
            boolean matchName = item.getName() != null && item.getName().toLowerCase().contains(kw);
            boolean matchDesc = item.getDescription() != null && item.getDescription().toLowerCase().contains(kw);
            boolean matchFeat = item.getDistinctiveFeatures() != null && item.getDistinctiveFeatures().toLowerCase().contains(kw);
            boolean matchId = item.getId() != null && item.getId().toLowerCase().contains(kw);
            if (!matchName && !matchDesc && !matchFeat && !matchId) {
                return false;
            }
        }

        return true;
    }
}
