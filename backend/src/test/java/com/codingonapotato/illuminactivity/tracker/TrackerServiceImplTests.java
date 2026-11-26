package com.codingonapotato.illuminactivity.tracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import com.codingonapotato.illuminactivity.application.Application;
import com.codingonapotato.illuminactivity.application.ApplicationService;
import com.codingonapotato.illuminactivity.category.Category;
import com.codingonapotato.illuminactivity.usage.UsageService;

public class TrackerServiceImplTests {

    private UsageService usageService;
    private ApplicationService applicationService;
    private TrackerServiceImpl trackerService;

    @BeforeEach
    void setup() {
        usageService = mock(UsageService.class);
        applicationService = mock(ApplicationService.class);
        trackerService = new TrackerServiceImpl(usageService, applicationService);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Integer> getCategoryMap() {
        return (Map<String, Integer>) ReflectionTestUtils.getField(trackerService, "categoryMap");
    }

    private Application getLastSeenApp() {
        return (Application) ReflectionTestUtils.getField(trackerService, "lastSeenApp");
    }

    private Instant getStartTime() {
        return (Instant) ReflectionTestUtils.getField(trackerService, "startTime");
    }

    private int getElapsedSeconds() {
        return (int) ReflectionTestUtils.getField(trackerService, "elapsedSeconds");
    }

    @Nested
    class TrackUsageTests {

        @Test
        @DisplayName("should do nothing on the first tick for an untracked app")
        void shouldDoNothingOnFirstTickForUntrackedApp() {
            Application untrackedApp = new Application("C:\\untracked.exe", "Untracked", false);
            when(applicationService.getForegroundApplication()).thenReturn(untrackedApp);

            trackerService.trackUsage();

            assertNull(getLastSeenApp());
            assertNull(getStartTime());
            assertEquals(0, getElapsedSeconds());
            assertTrue(getCategoryMap().isEmpty());
            verifyNoInteractions(usageService);
        }

        @Test
        @DisplayName("should update instance variables when switching from untracked to tracked")
        void shouldUpdateInstanceVariablesWithTrackedApp() {
            Application trackedApp = new Application("C:\\tracked.exe", "Tracked", true);
            Category productivity = new Category("productivity", "#eb4034");
            when(applicationService.getForegroundApplication()).thenReturn(trackedApp);
            when(applicationService.getActiveCategories(trackedApp)).thenReturn(List.of(productivity));

            trackerService.trackUsage();

            assertEquals(trackedApp, getLastSeenApp());
            assertNotNull(getStartTime());
            assertEquals(0, getElapsedSeconds());
            assertEquals(Map.of(productivity.getName(), 0), getCategoryMap());
            verifyNoInteractions(usageService);
        }

        @Test
        @DisplayName("should increment elapsed time with same tracked app and unchanged categories")
        void shouldIncrementElapsedTimeOnNoChangesDetected() {
            Application trackedApp = new Application("C:\\tracked.exe", "Tracked", true);
            Category productivity = new Category("productivity", "#eb4034");
            when(applicationService.getForegroundApplication()).thenReturn(trackedApp);
            when(applicationService.getActiveCategories(trackedApp)).thenReturn(List.of(productivity));
            trackerService.trackUsage();
            trackerService.trackUsage();

            assertEquals(trackedApp, getLastSeenApp());
            assertEquals(1, getElapsedSeconds());
            assertEquals(Map.of(productivity.getName(), 0), getCategoryMap());
            verify(usageService, never()).addUsage(
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        }

        @Test
        @DisplayName("should start a newly added category from the tick it was detected")
        @SuppressWarnings("unchecked")
        void shouldTrackAddedCategoryFromItsAdditionTime() {
            Application trackedApp = new Application("C:\\tracked.exe", "Tracked", true);
            Category productivity = new Category("productivity", "#eb4034");
            Category entertainment = new Category("entertainment", "#eb4034");
            Application nextApp = new Application("C:\\next.exe", "Next", false);
            when(applicationService.getForegroundApplication())
                .thenReturn(trackedApp, trackedApp, trackedApp, nextApp);
            when(applicationService.getActiveCategories(trackedApp))
                .thenReturn(List.of(productivity), List.of(productivity, entertainment),
                    List.of(productivity, entertainment), List.of(productivity, entertainment));
            when(applicationService.getAllCategories(trackedApp))
                .thenReturn(List.of(productivity, entertainment));

            trackerService.trackUsage();
            trackerService.trackUsage();

            Instant appStartTime = getStartTime();
            int expectedOffset = 1;
            assertEquals(1, getElapsedSeconds());
            assertEquals(Map.of("productivity", 0, "entertainment", expectedOffset), getCategoryMap());
            verifyNoInteractions(usageService);

            trackerService.trackUsage();
            trackerService.trackUsage();
            
            int trackedElapsedSeconds = 3;
            Instant correctedStartTime = appStartTime.plusSeconds(expectedOffset);
            int correctedElapsedSeconds = trackedElapsedSeconds - expectedOffset;
            verify(usageService).addUsage(
                trackedApp, productivity, appStartTime, appStartTime.plusSeconds(trackedElapsedSeconds));
            verify(usageService).addUsage(
                trackedApp, entertainment, correctedStartTime , correctedStartTime.plusSeconds(correctedElapsedSeconds));
            assertNull(getLastSeenApp());
            assertNull(getStartTime());
            assertEquals(0, getElapsedSeconds());
            assertTrue(getCategoryMap().isEmpty());
        }
        
        @Test
        @DisplayName("should flush a removed category and keep remaining categories")
        @SuppressWarnings("unchecked")
        void shouldRemoveCategoryMidSession() {
            Application trackedApp = new Application("C:\\tracked.exe", "Tracked", true);
            Category productivity = new Category("productivity", "#eb4034");
            Category entertainment = new Category("entertainment", "#eb4034");
            when(applicationService.getForegroundApplication()).thenReturn(trackedApp);
            when(applicationService.getActiveCategories(trackedApp))
                .thenReturn(List.of(productivity, entertainment), List.of(productivity));
            when(applicationService.getAllCategories(trackedApp)).thenReturn(List.of(productivity, entertainment));

            trackerService.trackUsage();
            trackerService.trackUsage();

            Instant startTime = getStartTime();
            assertEquals(Map.of("productivity", 0), getCategoryMap());
            verify(usageService).addUsage(
                trackedApp, entertainment, startTime, startTime.plusSeconds(1));
        }

        @Test
        @DisplayName("should flush a removed category before switching to another tracked app")
        @SuppressWarnings("unchecked")
        void shouldFlushRemovedCategoryWhenSwitchingTrackedApps() {
            Application appA = new Application("C:\\app-a.exe", "App A", true);
            Application appB = new Application("C:\\app-b.exe", "App B", true);
            Category productivity = new Category("productivity", "#eb4034");
            Category entertainment = new Category("entertainment", "#eb4034");
            Category focus = new Category("focus", "#eb4034");
            when(applicationService.getForegroundApplication()).thenReturn(appA, appA, appB);
            when(applicationService.getActiveCategories(appA))
                .thenReturn(List.of(productivity, entertainment), List.of(productivity), List.of(productivity));
            when(applicationService.getActiveCategories(appB)).thenReturn(List.of(focus));
            when(applicationService.getAllCategories(appA)).thenReturn(List.of(productivity, entertainment));

            trackerService.trackUsage();
            Instant appAStartTime = getStartTime();
            trackerService.trackUsage();
            trackerService.trackUsage();

            verify(usageService).addUsage(
                appA, entertainment, appAStartTime, appAStartTime.plusSeconds(1));
            verify(usageService).addUsage(
                appA, productivity, appAStartTime, appAStartTime.plusSeconds(2));
            assertEquals(appB, getLastSeenApp());
            assertEquals(0, getElapsedSeconds());
            assertNotNull(getStartTime());
            assertEquals(Map.of(focus.getName(), 0), getCategoryMap());
        }

        @Test
        @DisplayName("should flush tracked app A and reset state for tracked app B")
        void shouldSwitchFromTrackedAppAToTrackedAppB() {
            Application appA = new Application("C:\\app-a.exe", "App A", true);
            Application appB = new Application("C:\\app-b.exe", "App B", true);
            Category productivity = new Category("productivity", "#eb4034");
            Category entertainment = new Category("entertainment", "#eb4034");
            when(applicationService.getForegroundApplication()).thenReturn(appA, appB);
            when(applicationService.getActiveCategories(appA)).thenReturn(List.of(productivity));
            when(applicationService.getActiveCategories(appB)).thenReturn(List.of(entertainment));
            when(applicationService.getAllCategories(appA)).thenReturn(List.of(productivity));

            trackerService.trackUsage();
            Instant appAStartTime = getStartTime();
            trackerService.trackUsage();

            verify(usageService).addUsage(
                appA, productivity, appAStartTime, appAStartTime.plusSeconds(1));
            assertEquals(appB, getLastSeenApp());
            assertEquals(0, getElapsedSeconds());
            assertNotNull(getStartTime());
            assertEquals(Map.of("entertainment", 0), getCategoryMap());
        }

        @Test
        @DisplayName("should flush app A and reset state for an untracked app")
        void shouldSwitchFromTrackedAppAToUntrackedApp() {
            Application appA = new Application("C:\\app-a.exe", "App A", true);
            Application untrackedApp = new Application("C:\\untracked.exe", "Untracked", false);
            Category productivity = new Category("productivity", "#eb4034");
            when(applicationService.getForegroundApplication()).thenReturn(appA, untrackedApp);
            when(applicationService.getActiveCategories(appA)).thenReturn(List.of(productivity));
            when(applicationService.getAllCategories(appA)).thenReturn(List.of(productivity));

            trackerService.trackUsage();
            Instant appAStartTime = getStartTime();
            trackerService.trackUsage();

            verify(usageService).addUsage(
                appA, productivity, appAStartTime, appAStartTime.plusSeconds(1));
            assertNull(getLastSeenApp());
            assertNull(getStartTime());
            assertEquals(0, getElapsedSeconds());
            assertTrue(getCategoryMap().isEmpty());
        }
    }
}
