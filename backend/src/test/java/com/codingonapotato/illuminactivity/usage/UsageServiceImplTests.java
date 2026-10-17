package com.codingonapotato.illuminactivity.usage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDate;
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
import org.springframework.context.annotation.Import;
import com.codingonapotato.illuminactivity.application.Application;
import com.codingonapotato.illuminactivity.application.ApplicationHasCategory;
import com.codingonapotato.illuminactivity.application.ApplicationHasCategoryRepository;
import com.codingonapotato.illuminactivity.category.Category;
import com.codingonapotato.illuminactivity.usage.exception.UsageDateTimeException;

@DataJpaTest
@Import(UsageServiceImpl.class)
public class UsageServiceImplTests {

    @Autowired
    private UsageService usageService;

    @Autowired
    private UsageRepository usageRepository;

    @Autowired
    private ApplicationHasCategoryRepository applicationCategoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Application seedApplication =
        new Application("C:\\Example\\example.exe", "Example", true);

    private Category seedCategory = new Category("Example", "#FFFFFF");

    private ApplicationHasCategory seedApplicationCategory =
        new ApplicationHasCategory(seedApplication, seedCategory);

    private LocalDateTime firstStart = LocalDateTime.of(2026, 1, 1, 10, 50);

    private List<Usage> seedUsages = List.of(
        new Usage(seedApplicationCategory, firstStart, firstStart.plusMinutes(5)),
        new Usage(seedApplicationCategory, firstStart.plusMinutes(5), firstStart.plusMinutes(6)),
        new Usage(seedApplicationCategory, firstStart.plusDays(1), firstStart.plusDays(1).plusMinutes(5))
    );

    @BeforeEach
    void setup() {
        entityManager.persist(seedApplication);
        entityManager.persist(seedCategory);
        entityManager.persist(seedApplicationCategory);
        seedUsages.forEach(entityManager::persist);
    }

    @AfterEach
    void teardown() {
        usageRepository.deleteAll();
        applicationCategoryRepository.deleteAll();
        entityManager.remove(seedApplication);
        entityManager.remove(seedCategory);
    }

    @Nested
    class GetUsageByDateTests {

        @Test
        @DisplayName("should return usage for the specified date")
        void shouldReturnUsageForDate() {
            int expectedSize = 2;
            LocalDate targetDate = firstStart.toLocalDate();
            List<Usage> usages = usageService.getUsage(targetDate);

            assertEquals(expectedSize, usages.size());
            usages.forEach((usage) -> {
                assertTrue(usage.getPK().getStartTime().toLocalDate().isEqual(targetDate));
            });
        }
    }

    @Nested
    class GetUsageBetweenDateTimesTests {

        @Test
        @DisplayName("should return usage within the specified date-time range")
        void shouldReturnUsageBetweenDateTimes() {
            LocalDateTime end = firstStart.plusDays(1).plusHours(1);

            List<Usage> usages = usageService.getUsage(firstStart, end);

            assertEquals(seedUsages.size(), usages.size());
            seedUsages.forEach((seedUsage) -> assertTrue(usages.contains(seedUsage)));
        }

        @Test
        @DisplayName("should reject a range whose end is before its start")
        void shouldRejectEndBeforeStart() {
            LocalDateTime end = firstStart.minusMinutes(1);

            assertThrows(UsageDateTimeException.class,
                () -> usageService.getUsage(firstStart, end));
        }
    }

    @Nested
    class AddUsageTests {

        @Test
        @DisplayName("should add usage for an application category")
        void shouldAddUsage() {
            LocalDateTime start = firstStart.plusDays(2);
            LocalDateTime end = start.plusMinutes(10);

            usageService.addUsage(seedApplication, seedCategory, start, end);

            Usage.PK pk = new Usage.PK(seedApplicationCategory.getPk(), start);
            assertTrue(usageRepository.existsById(pk));
        }
    }

    @Nested
    class UpdateUsageTests {

        @Test
        @DisplayName("update usage with new end time and unchanged start time")
        void shouldUpdateWithNewEndTime() {
            Usage target = seedUsages.get(0);
            LocalDateTime end = firstStart.plusMinutes(15);

            usageService.updateUsage(target, firstStart, end);

            Usage updated = usageRepository.findById(target.getPK()).orElseThrow();
            assertEquals(end, updated.getEndTime());
        }

        @Test
        @DisplayName("update usage with new start and end time")
        void shouldUpdateWithNewStartAndEndTime() {
            Usage target = seedUsages.get(0);
            LocalDateTime start = firstStart.plusHours(1);
            LocalDateTime end = start.plusMinutes(10);
            Usage.PK oldPK = target.getPK();
            Usage.PK newPK = new Usage.PK(seedApplicationCategory.getPk(), start);

            usageService.updateUsage(target, start, end);

            assertFalse(usageRepository.existsById(oldPK));
            assertTrue(usageRepository.existsById(newPK));
            assertEquals(start, usageRepository.findById(newPK).orElseThrow().getPK().getStartTime());
            assertEquals(end, usageRepository.findById(newPK).orElseThrow().getEndTime());
        }
    }

    @Nested
    class DeleteUsageTests {

        @Test
        @DisplayName("should delete usage")
        void shouldDeleteUsage() {
            Usage target = seedUsages.get(0);

            usageService.deleteUsage(target);

            assertFalse(usageRepository.existsById(target.getPK()));
        }
    }
}
