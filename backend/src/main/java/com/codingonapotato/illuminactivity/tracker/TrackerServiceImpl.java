package com.codingonapotato.illuminactivity.tracker;

import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.codingonapotato.illuminactivity.application.Application;
import com.codingonapotato.illuminactivity.application.ApplicationService;
import com.codingonapotato.illuminactivity.usage.UsageService;

@Service 
public class TrackerServiceImpl implements TrackerService {
    private static final int POLLING_INTERVAL_SECONDS = 1;
    private final UsageService usageService;
    private final ApplicationService applicationService;
    private Application activeApplication;
    
    public TrackerServiceImpl(UsageService usageService, ApplicationService applicationService) {
        this.usageService = usageService;
        this.applicationService = applicationService;
        this.activeApplication = null;
    };
    
    @Scheduled(fixedDelay = POLLING_INTERVAL_SECONDS, timeUnit = TimeUnit.SECONDS)
    public void trackUsage() {
        System.out.println("Yeehaw");
    }
}
