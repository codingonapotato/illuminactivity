package com.codingonapotato.illuminactivity.application;

import java.util.List;
import com.codingonapotato.illuminactivity.category.Category;

public interface ApplicationService {
    List<Application> getTrackedApplications();
    List<Application> getAllApplications();
    void addCategory(Application target, Category category);
    void removeCategory(Application target, Category category);
    void setApplicationTracked(Application target, Boolean tracked);
}