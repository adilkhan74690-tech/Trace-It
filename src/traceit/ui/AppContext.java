package traceit.ui;

import traceit.model.FoundItem;
import traceit.model.LostItem;
import traceit.repository.FileRepository;
import traceit.service.FoundItemService;
import traceit.service.LostItemService;
import traceit.service.RecoveryService;
import traceit.service.SearchService;
import traceit.util.SampleDataGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AppContext {

    public interface DataChangeListener {
        void onDataChanged();
    }

    private final FileRepository repository;
    private final LostItemService lostItemService;
    private final FoundItemService foundItemService;
    private final RecoveryService recoveryService;
    private final SearchService searchService;

    private final List<DataChangeListener> dataChangeListeners = new ArrayList<>();
    private Consumer<String> screenNavigator;
    private Consumer<String> smartMatchTargetHandler;
    private BiConsumer<String, String> matchAnalysisTargetHandler;

    public AppContext() {
        this("data");
    }

    public AppContext(String dataDirPath) {
        this.repository = new FileRepository(dataDirPath);
        this.lostItemService = new LostItemService(repository);
        this.foundItemService = new FoundItemService(repository);
        this.recoveryService = new RecoveryService(lostItemService, foundItemService, repository);
        this.searchService = new SearchService(lostItemService, foundItemService);

        seedDataIfEmpty();
    }

    private void seedDataIfEmpty() {
        if (lostItemService.getAllLostItems().isEmpty() && foundItemService.getAllFoundItems().isEmpty()) {
            lostItemService.seedSampleData(SampleDataGenerator.generateSampleLostItems());
            foundItemService.seedSampleData(SampleDataGenerator.generateSampleFoundItems());
        }
    }

    public FileRepository getRepository() {
        return repository;
    }

    public LostItemService getLostItemService() {
        return lostItemService;
    }

    public FoundItemService getFoundItemService() {
        return foundItemService;
    }

    public RecoveryService getRecoveryService() {
        return recoveryService;
    }

    public SearchService getSearchService() {
        return searchService;
    }

    public void addDataChangeListener(DataChangeListener listener) {
        if (listener != null && !dataChangeListeners.contains(listener)) {
            dataChangeListeners.add(listener);
        }
    }

    public void removeDataChangeListener(DataChangeListener listener) {
        dataChangeListeners.remove(listener);
    }

    public void notifyDataChanged() {
        for (DataChangeListener listener : new ArrayList<>(dataChangeListeners)) {
            try {
                listener.onDataChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setScreenNavigator(Consumer<String> navigator) {
        this.screenNavigator = navigator;
    }

    public void navigateTo(String screenKey) {
        if (screenNavigator != null) {
            screenNavigator.accept(screenKey);
        }
    }

    public void setSmartMatchTargetHandler(Consumer<String> handler) {
        this.smartMatchTargetHandler = handler;
    }

    public void navigateToSmartMatching(String lostItemId) {
        if (smartMatchTargetHandler != null) {
            smartMatchTargetHandler.accept(lostItemId);
        }
        navigateTo("SMART_MATCHING");
    }

    public void setMatchAnalysisTargetHandler(BiConsumer<String, String> handler) {
        this.matchAnalysisTargetHandler = handler;
    }

    public void navigateToMatchAnalysis(String lostItemId, String foundItemId) {
        if (matchAnalysisTargetHandler != null) {
            matchAnalysisTargetHandler.accept(lostItemId, foundItemId);
        }
        navigateTo("MATCH_ANALYSIS");
    }
}

