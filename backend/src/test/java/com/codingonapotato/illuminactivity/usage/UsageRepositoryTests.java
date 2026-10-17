package com.codingonapotato.illuminactivity.usage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import com.codingonapotato.illuminactivity.application.Application;
import com.codingonapotato.illuminactivity.application.ApplicationHasCategory;
import com.codingonapotato.illuminactivity.category.Category;

@DataJpaTest 
public class UsageRepositoryTests {
    @Autowired
	private UsageRepository repository;

    @Autowired 
    private TestEntityManager entityManager;

    private LocalDateTime startTime = LocalDateTime.of(2026, 1, 1, 10, 50);

    private List<Application> applications = List.of(
        new Application("C:\\Example\\blitz.exe", "Blitz", true),
        new Application("C:\\Example\\chrome.exe", "Chrome", true)
    );

    private List<Category> categories = List.of(
        new Category("productivity", "#eb4034"),
        new Category("entertainment", "#eb4034")
    );

    private List<ApplicationHasCategory> applicationHasCategories = List.of(
        new ApplicationHasCategory(applications.get(0), categories.get(0)),
        new ApplicationHasCategory(applications.get(0), categories.get(1)),
        new ApplicationHasCategory(applications.get(1), categories.get(1))
    );

    private List<Usage> seed = List.of(
        new Usage(applicationHasCategories.get(0), startTime, startTime),
        new Usage(applicationHasCategories.get(1), startTime.plusMinutes(1), startTime.plusMinutes(1)),
        new Usage(applicationHasCategories.get(2), startTime.plusMinutes(2), startTime.plusMinutes(2))
    );

    @BeforeEach 
    void setup() {
        applications.forEach(entityManager::persist);
        categories.forEach(entityManager::persist);
        applicationHasCategories.forEach(entityManager::persist);
        seed.forEach(entityManager::persist);
    }

    @AfterEach
    void teardown() {
        seed.forEach(entityManager::remove);
        applicationHasCategories.forEach(entityManager::remove);
        applications.forEach(entityManager::remove);
        categories.forEach(entityManager::remove);
    }

    @Nested 
    class FindAllByStartAndEndTimeBetweenTests {
        @Test
        @DisplayName("returns all Usage entities with a startTime between the queried startTime and endTime")
        void shouldFindAllUsageBetweenStartAndEndTime() {
            int expectedSize = 1;
            List<Usage> usages = repository.findAllByStartAndEndTimeBetween(startTime, startTime);

            assertEquals(usages.size(), expectedSize);
            usages.forEach((usage) -> {
                LocalDateTime usageStartTime = usage.getPK().getStartTime();
                assertTrue(usageStartTime.isEqual(startTime) || usageStartTime.isAfter(startTime));
            });

            LocalDateTime past = startTime.minusDays(1);
            Usage pastUsage = new Usage(applicationHasCategories.get(0), past, past);
            entityManager.persist(pastUsage);
            List<Usage> nextUsages = repository.findAllByStartAndEndTimeBetween(startTime, startTime);

            assertEquals(nextUsages.size(), expectedSize);
            assertFalse(nextUsages.contains(pastUsage));
        }
    }
}
