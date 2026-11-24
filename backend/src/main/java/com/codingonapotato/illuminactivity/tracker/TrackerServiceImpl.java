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
    private Set<String> categoryNames;
    private int elapsedSeconds;
    private Instant startTime;
    
    public TrackerServiceImpl(UsageService usageService, ApplicationService applicationService) {
        this.usageService = usageService;
        this.applicationService = applicationService;
        
        startTime = null;
        lastSeenApp = null;
        categoryNames = new HashSet<>();
        elapsedSeconds = 0;
    };
    
    /**TODO: KAN-25 Flush data in memory to DB every minute (probably as a diff scheduled function actually) */
    @Scheduled(fixedDelay = POLLING_INTERVAL_SECONDS, timeUnit = TimeUnit.SECONDS)
    public void trackUsage() {
        if (lastSeenApp != null) 
            elapsedSeconds += POLLING_INTERVAL_SECONDS;
        
        Application foregroundApp = applicationService.getForegroundApplication();
        
        if (foregroundApp.getTracked() && foregroundApp.equals(lastSeenApp)) {
            List<Category> categories = applicationService.getAllCategories(lastSeenApp);
            Set<String> latestCategoryNames = new HashSet<>(
                categories.stream().map(category -> category.getName()).toList()
            );

            if (latestCategoryNames.equals(categoryNames)) 
                return;

            handleUpdatedApplicationCategories(latestCategoryNames, categories);
            return;
        }
        
        if (lastSeenApp != null) {
            List<Category> activeCategories = applicationService.getActiveCategories(lastSeenApp);
            activeCategories.forEach(category -> {
                usageService.addUsage(lastSeenApp, category, startTime, startTime.plusSeconds(elapsedSeconds));
            });
        }
        
        resetStateVariables(foregroundApp);
    }

    /**
     * Handles updates that are required in response to a change in the list of categories associated with the currently
     * tracked application such as writing buffered usage data for deleted categories and updating the TrackerService.categoryNames
     * set instance
     * @param latestCategoryNames
     * @param categories
     */
    private void handleUpdatedApplicationCategories(Set<String> latestCategoryNames, List<Category> categories) {
        List<String> newCategoryNames = latestCategoryNames.stream().filter(name -> !categoryNames.contains(name)).toList();
        List<String> removedCategoryNames = categoryNames.stream().filter(name -> !latestCategoryNames.contains(name)).toList();
        
        Map<String, Category> categoryMap = new HashMap<>();
        categories.forEach(category -> categoryMap.put(category.getName(), category));

        removedCategoryNames.forEach(removedCategoryName -> {
            usageService.addUsage(lastSeenApp, categoryMap.get(removedCategoryName), startTime, startTime.plusSeconds(elapsedSeconds));
        });

        categoryNames.removeAll(removedCategoryNames);
        newCategoryNames.forEach(categoryNames::add);
    }

    /**
     * Reset state variables when a new tracked foregroundApp is detected
     * @param app
     */
    private void resetStateVariables(Application app) {
        elapsedSeconds = 0;
        lastSeenApp = app.getTracked() ? app : null;
        startTime = app.getTracked() ? Instant.now() : null;
        categoryNames.clear();
        
        if (app.getTracked()) {
            List<Category> activeCategories = applicationService.getActiveCategories(app);
            activeCategories.forEach(category -> categoryNames.add(category.getName()));
        }
    }
}