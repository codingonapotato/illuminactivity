package com.codingonapotato.illuminactivity.tracker;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.codingonapotato.illuminactivity.application.Application;
import com.codingonapotato.illuminactivity.application.ApplicationService;
import com.codingonapotato.illuminactivity.category.Category;
import com.codingonapotato.illuminactivity.usage.UsageService;

@Service 
public class TrackerServiceImpl implements TrackerService {
    private static final int POLLING_INTERVAL_SECONDS = 1;
    private final UsageService usageService;
    private final ApplicationService applicationService;
    private Application lastSeenApp;
    // (k, v) = (categoryName, seconds elapsed before the category was added)
    private Map<String, Integer> categoryMap;
    
    private int elapsedSeconds;
    private Instant startTime;
    
    public TrackerServiceImpl(UsageService usageService, ApplicationService applicationService) {
        this.usageService = usageService;
        this.applicationService = applicationService;
        
        startTime = null;
        lastSeenApp = null;
        categoryMap = new HashMap<>();
        elapsedSeconds = 0;
    };
    
    /**TODO: KAN-25 Flush data in memory to DB every minute (probably as a diff scheduled function actually) */
    @Scheduled(fixedDelay = POLLING_INTERVAL_SECONDS, timeUnit = TimeUnit.SECONDS)
    public void trackUsage() {
        if (lastSeenApp != null) 
            elapsedSeconds += POLLING_INTERVAL_SECONDS;
        
        Application foregroundApp = applicationService.getForegroundApplication();

        if (foregroundApp.getTracked() && foregroundApp.equals(lastSeenApp)) {
            List<Category> activeCategories = applicationService.getActiveCategories(lastSeenApp);
            if (hasApplicationCategoriesChanged(activeCategories))
                handleUpdatedApplicationCategories(activeCategories);
            return;
        }
        
        if (lastSeenApp != null) {
            List<Category> activeCategories = applicationService.getActiveCategories(lastSeenApp);
            if (hasApplicationCategoriesChanged(activeCategories))
                handleUpdatedApplicationCategories(activeCategories);
            
            activeCategories.forEach(category -> writeUsage(category));
        }
        
        updateStateVariables(foregroundApp);
    }

    /**
     * Determine if the list of application categories for the tracked foreground app has changed
     * @return true if list of application categories for the tracked foreground app has changed. Otherwise, false
     */
    private boolean hasApplicationCategoriesChanged(List<Category> activeCategories) {
        Set<String> latestCategoryNames = new HashSet<>(
            activeCategories.stream().map(category -> category.getName()).toList()
        );
        Set<String> lastSeenCategories = categoryMap.keySet();
        return !latestCategoryNames.equals(lastSeenCategories);
    }

    /**
     * Handles updates that are required in response to a change in the list of categories associated with the currently
     * tracked application such as writing buffered usage data for deleted categories and updating the TrackerService.categoryNames
     * set instance
     * @param latestCategoryNames
     */
    private void handleUpdatedApplicationCategories(List<Category> latestActiveCategories) {
        Set<String> latestCategoryNames = new HashSet<>(
            latestActiveCategories.stream().map(category -> category.getName()).toList()
        );
        Set<String> lastSeenCategoryNames = categoryMap.keySet();
        List<Category> categories = applicationService.getAllCategories(lastSeenApp);
        List<String> newCategoryNames = latestCategoryNames.stream().filter(name -> !lastSeenCategoryNames.contains(name)).toList();
        List<String> removedCategoryNames = lastSeenCategoryNames.stream().filter(name -> !latestCategoryNames.contains(name)).toList();
        
        Map<String, Category> allCategoryMap = new HashMap<>();
        categories.forEach(category -> allCategoryMap.put(category.getName(), category));

        removedCategoryNames.forEach(removedCategoryName -> {
            writeUsage(allCategoryMap.get(removedCategoryName));
            categoryMap.remove(removedCategoryName);
        });

        newCategoryNames.forEach(newCategoryName -> categoryMap.put(newCategoryName, elapsedSeconds));
    }

    /**
     * Write usage data for the this.lastSeenApp and the supplied category
     * @param category
     */
    private void writeUsage(Category category) {
        int offset = categoryMap.get(category.getName());
        Instant correctedStartTime = startTime.plusSeconds(offset);
        int correctedElapsedSeconds = elapsedSeconds - offset;
        usageService.addUsage(lastSeenApp, category, correctedStartTime, correctedStartTime.plusSeconds(correctedElapsedSeconds));
    }

    /**
     * Update state variables based on the properties of the new detected foregroundApp
     * @param app
     */
    private void updateStateVariables(Application app) {
        elapsedSeconds = 0;
        lastSeenApp = app.getTracked() ? app : null;
        startTime = app.getTracked() ? Instant.now() : null;
        categoryMap.clear();
        
        if (app.getTracked()) {
            List<Category> activeCategories = applicationService.getActiveCategories(app);
            activeCategories.forEach(category -> categoryMap.put(category.getName(), elapsedSeconds));
        }
    }
}