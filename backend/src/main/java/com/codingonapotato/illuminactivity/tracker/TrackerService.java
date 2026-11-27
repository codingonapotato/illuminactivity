package com.codingonapotato.illuminactivity.tracker;

public interface TrackerService {
    /**
     * Track the usage for tracked apps running in the foreground
     */
    void trackUsage();
}