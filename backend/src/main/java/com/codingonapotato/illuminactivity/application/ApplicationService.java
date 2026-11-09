package com.codingonapotato.illuminactivity.application;

import java.util.List;
import com.codingonapotato.illuminactivity.category.Category;

public interface ApplicationService {
    /**
     * Find and return a list of all tracked applications
     * @return List of tracked applications
     */
    List<Application> getTrackedApplications();

    /**
     * Return a list of all applications running on the host machiine
     * @return
     */
    List<Application> getAllApplications();

    /**
     * Return a list of active categories with an active application-category pair for the target application
     * @param target
     * @return List of active categories
     */
    List<Category> getCategories(Application target);

    /**
     * Create a new relationship between the target application and category
     * @param target
     * @param category
     */
    void addCategory(Application target, Category category);

    /**
     * Remove the relationship between the target application and category
     * @param target
     * @param category
     */
    void removeCategory(Application target, Category category);

    /**
     * Update the 'tracked' state for the target application
     * @param target
     * @param tracked
     */
    void setApplicationTracked(Application target, Boolean tracked);
}