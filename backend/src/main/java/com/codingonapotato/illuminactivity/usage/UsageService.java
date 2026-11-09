package com.codingonapotato.illuminactivity.usage;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.codingonapotato.illuminactivity.application.Application;
import com.codingonapotato.illuminactivity.application.ApplicationHasCategory;
import com.codingonapotato.illuminactivity.category.Category;

public interface UsageService {
    /**
     * Find and return a list of all Usage records who's startTime is on the supplied date
     * @param date
     * @return List of Usage records that start on the supplied date
     */
    List<Usage> getUsage(LocalDate date);

    /**
     * Find and return a list of all Usage records who's startTime is between the specified time range
     * @param start
     * @param end
     * @return List of Usage records that start within the specified time range
     */
    List<Usage> getUsage(LocalDateTime start, LocalDateTime end);

    /**
     * Create a new Usage record
     * @param app
     * @param category
     * @param start
     * @param end
     */
    void addUsage(Application app, Category category, Instant start, Instant end);

    /**
     * Update the target Usage record
     * @param usage
     * @param start
     * @param end
     * @param applicationCategory
     */
    void updateUsage(Usage usage, Instant start, Instant end, ApplicationHasCategory applicationCategory);

    /**
     * Update the target Usage record
     * @param usage
     * @param start
     * @param end
     */
    void updateUsage(Usage usage, Instant start, Instant end);

    /**
     * Delete the target Usage record
     * @param usage
     */
    void deleteUsage(Usage usage);
}