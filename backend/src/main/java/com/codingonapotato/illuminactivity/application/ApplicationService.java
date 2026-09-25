package com.codingonapotato.illuminactivity.application;

import java.util.List;

public interface ApplicationService {
    List<Application> getTrackedApplications();
    List<Application> getAllApplications();
    void setApplicationTracked(Application target, Boolean tracked);
}