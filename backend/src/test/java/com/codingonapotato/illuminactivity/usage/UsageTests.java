package com.codingonapotato.illuminactivity.usage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import com.codingonapotato.illuminactivity.application.Application;
import com.codingonapotato.illuminactivity.application.ApplicationHasCategory;
import com.codingonapotato.illuminactivity.application.exception.InactiveApplicationCategoryException;
import com.codingonapotato.illuminactivity.category.Category;
import com.codingonapotato.illuminactivity.usage.exception.UsageDateTimeException;

public class UsageTests {
    private static final ApplicationHasCategory APPLICATION_CATEGORY =
        new ApplicationHasCategory(
            new Application("C:\\Example\\example.exe", "Example", true),
            new Category("Example", "#FFFFFF"));
    private static final Instant START_TIME = Instant.parse("2026-01-01T10:50:00Z");

    @Test
    @DisplayName("Should create instance with specified fields")
    void testConstructor() {
        Instant endTime = START_TIME.plusSeconds(5 * 60);
        Usage usage = new Usage(APPLICATION_CATEGORY, START_TIME, endTime);

        assertEquals(APPLICATION_CATEGORY, usage.getApplicationCategory());
        assertEquals(START_TIME, usage.getPK().getStartTime());
        assertEquals(endTime, usage.getEndTime());
    }

    @Nested
    class ConstructorInputValidationTests {
        @Test
        @DisplayName("should fail when end time is before start time")
        void shouldFailWithEndTimeBeforeStartTime() {
            Instant endTime = START_TIME.minusSeconds(60);

            assertThrows(UsageDateTimeException.class,
                () -> new Usage(APPLICATION_CATEGORY, START_TIME, endTime));
        }
    }

    @Nested
    class ValidateInputTests {
        @Test
        @DisplayName("should accept an active application category and valid times")
        void shouldAcceptValidInput() {
            Instant endTime = START_TIME.plusSeconds(5 * 60);

            Usage.validateInput(APPLICATION_CATEGORY, START_TIME, endTime);
        }

        @Test
        @DisplayName("should reject an inactive application category")
        void shouldRejectInactiveApplicationCategory() {
            ApplicationHasCategory inactiveApplicationCategory =
                new ApplicationHasCategory(
                    new Application("C:\\Example\\inactive.exe", "Inactive", true),
                    new Category("Inactive", "#000000"));
            inactiveApplicationCategory.setDeletedAt(Instant.now());

            assertThrows(InactiveApplicationCategoryException.class,
                () -> Usage.validateInput(inactiveApplicationCategory, START_TIME, START_TIME));
        }

        @Test
        @DisplayName("should reject an end time before the start time")
        void shouldRejectEndTimeBeforeStartTime() {
            Instant endTime = START_TIME.minusSeconds(60);

            assertThrows(UsageDateTimeException.class,
                () -> Usage.validateInput(APPLICATION_CATEGORY, START_TIME, endTime));
        }
    }

    @Nested
    class SetEndTimeTests {
        @Test
        @DisplayName("should update end time when it is after start time")
        void shouldSetValidEndTime() {
            Usage usage = new Usage(APPLICATION_CATEGORY, START_TIME, START_TIME);
            Instant endTime = START_TIME.plusSeconds(5 * 60);

            usage.setEndTime(endTime);

            assertEquals(endTime, usage.getEndTime());
        }

        @Test
        @DisplayName("should fail when end time is before start time")
        void shouldFailWithEndTimeBeforeStartTime() {
            Usage usage = new Usage(APPLICATION_CATEGORY, START_TIME, START_TIME);
            Instant originalEndTime = usage.getEndTime();
            Instant endTime = START_TIME.minusSeconds(60);

            assertThrows(UsageDateTimeException.class, () -> usage.setEndTime(endTime));
            assertEquals(originalEndTime, usage.getEndTime());
        }
    }

    @Nested
    class PKTests {
        @Test
        @DisplayName("Should create PK with specified fields")
        void testConstructor() {
            ApplicationHasCategory.PK applicationCategoryPK =
                new ApplicationHasCategory.PK("C:\\Example\\example.exe", "Example");
            Usage.PK pk = new Usage.PK(applicationCategoryPK, START_TIME);

            assertEquals(applicationCategoryPK, pk.getApplicationHasCategoryPK());
            assertEquals(START_TIME, pk.getStartTime());
        }
    }
}
