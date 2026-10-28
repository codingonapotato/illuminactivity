package com.codingonapotato.illuminactivity.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.Instant;
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
import com.codingonapotato.illuminactivity.application.exception.DuplicateApplicationCategoryException;
import com.codingonapotato.illuminactivity.application.exception.InactiveApplicationCategoryException;
import com.codingonapotato.illuminactivity.category.Category;

@DataJpaTest
@Import(ApplicationServiceImpl.class)
public class ApplicationServiceImplTests {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationHasCategoryRepository applicationCategoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private List<Application> seedApplications = List.of(
        new Application("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe", "Google Chrome", true),
        new Application("C:\\Example\\blitz.exe", "Blitz", true),
        new Application("C:\\Example\\notepad.exe", "Notepad", false)
    );

    private Category seedCategory = new Category("productivity", "#eb4034");

    @BeforeEach
    void setup() {
        seedApplications.forEach(entityManager::persist);
        entityManager.persist(seedCategory);
    }

    @AfterEach
    void teardown() {
        applicationCategoryRepository.deleteAll();
        applicationRepository.deleteAll();
        entityManager.remove(seedCategory);
    }

    @Nested
    class GetTrackedApplicationsTests {

        @Test
        @DisplayName("should return only tracked applications")
        void shouldReturnOnlyTrackedApplications() {
            int expectedSize = 2;
            List<Application> applications = applicationService.getTrackedApplications();

            assertEquals(expectedSize, applications.size());
            seedApplications.forEach((seedApplication) -> {
                if (seedApplication.getTracked())
                    assertTrue(applications.contains(seedApplication));
                else
                    assertFalse(applications.contains(seedApplication));
            });
        }
    }

    // TODO: Postponed to KAN-17
    // @Nested
    // class GetAllApplicationsTests {

    //     @Test
    //     @DisplayName("should return no applications")
    //     void shouldReturnNoApplications() {
    //         assertTrue(applicationService.getAllApplications().isEmpty());
    //     }
    // }

    @Nested
    class AddCategoryTests {

        @Test
        @DisplayName("should associate category with application")
        void shouldAddCategoryToApplication() {
            Application target = seedApplications.get(0);
            int expectedSize = target.getApplicationCategories().size() + 1;
            applicationService.addCategory(target, seedCategory);

            ApplicationHasCategory.PK pk = new ApplicationHasCategory.PK(target.getPath(), seedCategory.getName());
            assertTrue(applicationCategoryRepository.existsById(pk));
            assertEquals(expectedSize, target.getApplicationCategories().size());
        }

        @Test
        @DisplayName("should fail to add an already active application-category relationship")
        void shouldRejectAlreadyActiveCategoryRelationship() {
            Application target = seedApplications.get(0);
            ApplicationHasCategory applicationCategory =
                new ApplicationHasCategory(target, seedCategory);
            entityManager.persist(applicationCategory);
            entityManager.flush();

            assertThrows(DuplicateApplicationCategoryException.class,
                () -> applicationService.addCategory(target, seedCategory));

            assertTrue(applicationCategoryRepository.existsById(applicationCategory.getPk()));
            assertNull(
                applicationCategoryRepository.findById(applicationCategory.getPk()).get().getDeletedAt());
        }
    }

    @Nested
    class RemoveCategoryTests {

        @Test
        @DisplayName("should remove category from application")
        void shouldRemoveCategoryFromApplication() {
            Application target = seedApplications.get(0);
            ApplicationHasCategory applicationCategory =
                new ApplicationHasCategory(target, seedCategory);
            entityManager.persist(applicationCategory);
            entityManager.flush();

            entityManager.refresh(target);
            int expectedSize = target.getApplicationCategories().size() - 1;

            applicationService.removeCategory(target, seedCategory);

            assertTrue(applicationCategoryRepository.existsById(applicationCategory.getPk()));
            assertEquals(expectedSize, target.getApplicationCategories().size());
            assertNotNull(applicationCategoryRepository.findById(applicationCategory.getPk()).get().getDeletedAt());
        }

        @Test
        @DisplayName("should fail to soft-delete an already soft-deleted application-category relationship")
        void shouldRejectAlreadySoftDeletedCategoryRelationship() {
            Application target = seedApplications.get(0);
            ApplicationHasCategory applicationCategory =
                new ApplicationHasCategory(target, seedCategory);
            applicationCategory.setDeletedAt(Instant.now());
            entityManager.persist(applicationCategory);
            entityManager.flush();

            Instant deletedAt = applicationCategory.getDeletedAt();

            assertThrows(InactiveApplicationCategoryException.class,
                () -> applicationService.removeCategory(target, seedCategory));

            assertEquals(deletedAt,
                applicationCategoryRepository.findById(applicationCategory.getPk()).get().getDeletedAt());
        }
    }

    @Nested
    class SetApplicationTrackedTests {

        @Test
        @DisplayName("should update application tracked state")
        void shouldUpdateApplicationTrackedState() {
            Application target = seedApplications.get(2);

            applicationService.setApplicationTracked(target, true);

            assertTrue(applicationRepository.findById(target.getPath()).get().getTracked());
        }
    }
}
