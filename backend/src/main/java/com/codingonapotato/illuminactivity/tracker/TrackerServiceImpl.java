package com.codingonapotato.illuminactivity.tracker;

import java.time.Instant;
import java.util.List;
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
    private int elapsedSeconds;
    
    public TrackerServiceImpl(UsageService usageService, ApplicationService applicationService) {
        this.usageService = usageService;
        this.applicationService = applicationService;
        
        lastSeenApp = null;
        elapsedSeconds = 0;
    };
    
    /**TODO: KAN-25 Flush data in memory to DB every minute (probably as a diff scheduled function actually) */
    @Scheduled(fixedDelay = POLLING_INTERVAL_SECONDS, timeUnit = TimeUnit.SECONDS)
    public void trackUsage() {
        Instant now = Instant.now();
        if (lastSeenApp != null) 
            elapsedSeconds += POLLING_INTERVAL_SECONDS;
        
        Application foregroundApp = applicationService.getForegroundApplication();
        
        // TODO: I need to make sure to detect and handle changes in the list of categories for an application!
        if (foregroundApp.getTracked() && foregroundApp.equals(lastSeenApp)) return;
        
        // TODO: Need to check that existing usage record doesn't exist
        if (lastSeenApp != null) {
            List<Category> categories = applicationService.getCategories(lastSeenApp);
            categories.forEach(category -> {
                usageService.addUsage(lastSeenApp, category, now.minusSeconds(elapsedSeconds), now);
            });
        }
        
        elapsedSeconds = 0;
        lastSeenApp = foregroundApp.getTracked() ? foregroundApp : null;
    }
}