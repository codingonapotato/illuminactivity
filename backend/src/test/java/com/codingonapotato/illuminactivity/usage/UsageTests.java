package com.codingonapotato.illuminactivity.usage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import com.codingonapotato.illuminactivity.application.Application;
import com.codingonapotato.illuminactivity.application.ApplicationHasCategory;
import com.codingonapotato.illuminactivity.category.Category;
import com.codingonapotato.illuminactivity.usage.exception.UsageDateTimeException;

public class UsageTests {
    private static final ApplicationHasCategory APPLICATION_CATEGORY =
        new ApplicationHasCategory(
            new Application("C:\\Example\\example.exe", "Example", true),
            new Category("Example", "#FFFFFF"));
    private static final LocalDateTime START_TIME = LocalDateTime.of(2026, 1, 1, 10, 50);

    @Test
    @DisplayName("Should create instance with specified fields")
    void testConstructor() {
        LocalDateTime endTime = START_TIME.plusMinutes(5);
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
            LocalDateTime endTime = START_TIME.minusMinutes(1);

            assertThrows(UsageDateTimeException.class,
                () -> new Usage(APPLICATION_CATEGORY, START_TIME, endTime));
        }
    }

    @Nested
    class SetEndTimeTests {
        @Test
        @DisplayName("should update end time when it is after start time")
        void shouldSetValidEndTime() {
            Usage usage = new Usage(APPLICATION_CATEGORY, START_TIME, START_TIME);
            LocalDateTime endTime = START_TIME.plusMinutes(5);

            usage.setEndTime(endTime);

            assertEquals(endTime, usage.getEndTime());
        }

        @Test
        @DisplayName("should fail when end time is before start time")
        void shouldFailWithEndTimeBeforeStartTime() {
            Usage usage = new Usage(APPLICATION_CATEGORY, START_TIME, START_TIME);
            LocalDateTime originalEndTime = usage.getEndTime();
            LocalDateTime endTime = START_TIME.minusMinutes(1);

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
