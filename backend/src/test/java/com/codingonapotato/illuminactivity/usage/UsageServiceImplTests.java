package com.codingonapotato.illuminactivity.usage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
import com.codingonapotato.illuminactivity.application.exception.InactiveApplicationCategoryException;
import com.codingonapotato.illuminactivity.category.Category;
import com.codingonapotato.illuminactivity.usage.exception.DuplicateUsageException;
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

    private Instant firstStart = Instant.parse("2026-01-01T10:50:00Z");

    private Instant crossDayStart = firstStart.atZone(ZoneId.systemDefault()).toLocalDate()
        .atTime(LocalTime.of(23, 55)).atZone(ZoneId.systemDefault()).toInstant();

    private List<Usage> seedUsages = List.of(
        new Usage(seedApplicationCategory, firstStart, firstStart.plusSeconds(5)),
        new Usage(seedApplicationCategory, firstStart.plusSeconds(5), firstStart.plusSeconds(6)),
        new Usage(seedApplicationCategory, firstStart.plus(1, ChronoUnit.DAYS), firstStart.plus(1, ChronoUnit.DAYS).plus(5, ChronoUnit.MINUTES)),
        new Usage(seedApplicationCategory, crossDayStart, crossDayStart.plus(10, ChronoUnit.MINUTES))
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
        @DisplayName("should return usage that starts on the specified date")
        void shouldReturnUsageForDate() {
            int expectedSize = 3;
            LocalDate targetDate = firstStart.atZone(ZoneId.systemDefault()).toLocalDate();
            List<Usage> usages = usageService.getUsage(targetDate);

            assertEquals(expectedSize, usages.size());
            assertTrue(usages.stream()
                .anyMatch((usage) -> usage.getPK().getStartTime().equals(crossDayStart)));
            usages.forEach((usage) -> {
                assertTrue(usage.getPK().getStartTime().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(targetDate));
            });
        }
    }

    @Nested
    class GetUsageBetweenDateTimesTests {

        @Test
        @DisplayName("should return usage that starts within the specified date-time range")
        void shouldReturnUsageBetweenDateTimes() {
            LocalDateTime start = firstStart.atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime end = crossDayStart.atZone(ZoneId.systemDefault()).toLocalDateTime();

            List<Usage> usages = usageService.getUsage(start, end);

            assertEquals(3, usages.size());
            assertTrue(usages.contains(seedUsages.get(0)));
            assertTrue(usages.contains(seedUsages.get(1)));
            assertTrue(usages.contains(seedUsages.get(3)));
            assertFalse(usages.contains(seedUsages.get(2)));
        }

        @Test
        @DisplayName("should exclude usage outside the specified date-time range")
        void shouldExcludeUsageOutsideDateTimeRange() {
            LocalDateTime start = firstStart.atZone(ZoneId.systemDefault()).toLocalDateTime()
                .plusSeconds(6);
            LocalDateTime end = start.plusSeconds(1);

            List<Usage> usages = usageService.getUsage(start, end);

            assertTrue(usages.isEmpty());
        }
    }

    @Nested
    class AddUsageTests {

        @Test
        @DisplayName("should add usage for an application category")
        void shouldAddUsage() {
            Instant start = firstStart.plus(2, ChronoUnit.DAYS);
            Instant end = start.plus(10, ChronoUnit.MINUTES);

            usageService.addUsage(seedApplication, seedCategory, start, end);

            Usage.PK pk = new Usage.PK(seedApplicationCategory.getPk(), start);
            assertTrue(usageRepository.existsById(pk));
        }

        @Test
        @DisplayName("should reject a usage with an existing application category and start time")
        void shouldRejectDuplicateUsage() {
            Instant start = seedUsages.get(0).getPK().getStartTime();
            Instant end = start.plus(10, ChronoUnit.MINUTES);

            assertThrows(DuplicateUsageException.class,
                () -> usageService.addUsage(seedApplication, seedCategory, start, end));

            Usage.PK pk = new Usage.PK(seedApplicationCategory.getPk(), start);
            assertEquals(seedUsages.get(0), usageRepository.findById(pk).orElseThrow());
        }

        @Test
        @DisplayName("should reject usage for an inactive application category")
        void shouldRejectInactiveApplicationCategory() {
            seedApplicationCategory.setDeletedAt(Instant.now());
            Instant start = firstStart.plus(2, ChronoUnit.DAYS);
            Instant end = start.plus(10, ChronoUnit.MINUTES);

            assertThrows(InactiveApplicationCategoryException.class,
                () -> usageService.addUsage(seedApplication, seedCategory, start, end));

            Usage.PK pk = new Usage.PK(seedApplicationCategory.getPk(), start);
            assertFalse(usageRepository.existsById(pk));
        }

        @Test
        @DisplayName("should reject usage whose end time is before its start time")
        void shouldRejectEndBeforeStart() {
            Instant start = firstStart.plus(2, ChronoUnit.DAYS);
            Instant end = start.minus(1, ChronoUnit.MINUTES);

            assertThrows(UsageDateTimeException.class,
                () -> usageService.addUsage(seedApplication, seedCategory, start, end));

            Usage.PK pk = new Usage.PK(seedApplicationCategory.getPk(), start);
            assertFalse(usageRepository.existsById(pk));
        }
    }

    @Nested
    class UpdateUsageTests {

        @Test
        @DisplayName("update usage with new end time and unchanged start time")
        void shouldUpdateWithNewEndTime() {
            Usage target = seedUsages.get(0);
            Instant end = firstStart.plus(15, ChronoUnit.MINUTES);

            usageService.updateUsage(target, firstStart, end);

            Usage updated = usageRepository.findById(target.getPK()).orElseThrow();
            assertEquals(end, updated.getEndTime());
        }

        @Test
        @DisplayName("update usage with new start and end time")
        void shouldUpdateWithNewStartAndEndTime() {
            Usage target = seedUsages.get(0);
            Instant start = firstStart.plus(1, ChronoUnit.HOURS);
            Instant end = start.plus(10, ChronoUnit.MINUTES);
            Usage.PK oldPK = target.getPK();
            Usage.PK newPK = new Usage.PK(seedApplicationCategory.getPk(), start);

            usageService.updateUsage(target, start, end);

            assertFalse(usageRepository.existsById(oldPK));
            assertTrue(usageRepository.existsById(newPK));
            assertEquals(start, usageRepository.findById(newPK).orElseThrow().getPK().getStartTime());
            assertEquals(end, usageRepository.findById(newPK).orElseThrow().getEndTime());
        }

        @Test
        @DisplayName("should update usage with a different application category")
        void shouldUpdateApplicationCategory() {
            Category replacementCategory = new Category("Replacement", "#000000");
            ApplicationHasCategory replacementApplicationCategory =
                new ApplicationHasCategory(seedApplication, replacementCategory);
            entityManager.persist(replacementCategory);
            entityManager.persist(replacementApplicationCategory);

            Usage target = seedUsages.get(0);
            Instant start = firstStart.plus(1, ChronoUnit.HOURS);
            Instant end = start.plus(10, ChronoUnit.MINUTES);
            Usage.PK oldPK = target.getPK();
            Usage.PK newPK = new Usage.PK(replacementApplicationCategory.getPk(), start);

            usageService.updateUsage(target, start, end, replacementApplicationCategory);

            assertFalse(usageRepository.existsById(oldPK));
            Usage updated = usageRepository.findById(newPK).orElseThrow();
            assertEquals(replacementApplicationCategory, updated.getApplicationCategory());
            assertEquals(end, updated.getEndTime());
        }

        @Test
        @DisplayName("should reject an end time before the new start time")
        void shouldRejectEndBeforeStartWhenUpdatingApplicationCategory() {
            Usage target = seedUsages.get(0);
            Instant start = firstStart.plus(1, ChronoUnit.HOURS);
            Instant end = start.minus(1, ChronoUnit.MINUTES);

            assertThrows(UsageDateTimeException.class,
                () -> usageService.updateUsage(target, start, end, seedApplicationCategory));

            assertTrue(usageRepository.existsById(target.getPK()));
        }

        @Test
        @DisplayName("should reject an inactive replacement application category")
        void shouldRejectInactiveApplicationCategoryWhenUpdating() {
            seedApplicationCategory.setDeletedAt(Instant.now());
            Usage target = seedUsages.get(0);
            Instant start = firstStart.plus(1, ChronoUnit.HOURS);
            Instant end = start.plus(10, ChronoUnit.MINUTES);

            assertThrows(InactiveApplicationCategoryException.class,
                () -> usageService.updateUsage(target, start, end, seedApplicationCategory));

            assertTrue(usageRepository.existsById(target.getPK()));
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
