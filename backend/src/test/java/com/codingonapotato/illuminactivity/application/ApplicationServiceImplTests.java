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
import com.codingonapotato.illuminactivity.category.CategoryRepository;

@DataJpaTest
@Import(ApplicationServiceImpl.class)
public class ApplicationServiceImplTests {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ApplicationHasCategoryRepository applicationCategoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private List<Application> seedApplications = List.of(
        new Application("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe", "Google Chrome", true),
        new Application("C:\\Example\\blitz.exe", "Blitz", true),
        new Application("C:\\Example\\notepad.exe", "Notepad", false)
    );

    private List<Category> seedCategories = List.of(
        new Category("productivity", "#eb4034"),
        new Category("entertainment", "#eb4034")
    );

    @BeforeEach
    void setup() {
        seedApplications.forEach(entityManager::persist);
        seedCategories.forEach(entityManager::persist);
    }

    @AfterEach
    void teardown() {
        applicationCategoryRepository.deleteAll();
        applicationRepository.deleteAll();
        categoryRepository.deleteAll();
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
    class GetCategoriesTests {

        @Test
        @DisplayName("should return active application-category pairs")
        void shouldReturnActiveApplicationCategoryPairs() {
            Application target = seedApplications.get(0);
            Category seedCategory1 = seedCategories.get(0);
            Category seedCategory2 = seedCategories.get(1);

            ApplicationHasCategory activeApplicationCategory =
                new ApplicationHasCategory(target, seedCategory1);
            ApplicationHasCategory inactiveApplicationCategory =
                new ApplicationHasCategory(target, seedCategory2);
            inactiveApplicationCategory.setDeletedAt(Instant.now());
            entityManager.persist(activeApplicationCategory);
            entityManager.persist(inactiveApplicationCategory);
            entityManager.flush();

            List<Category> categories = applicationService.getCategories(target);
            assertEquals(List.of(seedCategory1), categories);
        }

        @Test
        @DisplayName("should not return active application-category pairs for a soft deleted category")
        void shouldNotReturnActiveApplicationCategoryPairForInactiveCategories() {
            Application target = seedApplications.get(0);
            Category inactiveCategory = seedCategories.get(1);
            inactiveCategory.setDeletedAt(Instant.now());
            entityManager.persist(inactiveCategory);

            ApplicationHasCategory applicationCategory1 =
                new ApplicationHasCategory(target, seedCategories.get(0));
            ApplicationHasCategory applicationCategory2 =
                new ApplicationHasCategory(target, inactiveCategory);
            entityManager.persist(applicationCategory1);
            entityManager.persist(applicationCategory2);
            entityManager.flush();

            List<Category> categories = applicationService.getCategories(target);
            assertEquals(List.of(seedCategories.get(0)), categories);
        }
    }

    @Nested
    class AddCategoryTests {

        @Test
        @DisplayName("should associate category with application")
        void shouldAddCategoryToApplication() {
            Application target = seedApplications.get(0);
            Category seedCategory = seedCategories.get(0);
            applicationService.addCategory(target, seedCategory);

            ApplicationHasCategory.PK pk = new ApplicationHasCategory.PK(target.getPath(), seedCategory.getName());
            assertTrue(applicationCategoryRepository.existsById(pk));
            assertEquals(seedCategory,
                applicationCategoryRepository.findById(pk).get().getCategory());
        }

        @Test
        @DisplayName("should fail to add an already active application-category relationship")
        void shouldRejectAlreadyActiveCategoryRelationship() {
            Application target = seedApplications.get(0);
            Category seedCategory = seedCategories.get(0);
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

        @Test
        @DisplayName("should reactivate a soft-deleted application-category relationship")
        void shouldReactivateSoftDeletedCategoryRelationship() {
            Application target = seedApplications.get(0);
            Category seedCategory = seedCategories.get(0);
            ApplicationHasCategory applicationCategory =
                new ApplicationHasCategory(target, seedCategory);
            applicationCategory.setDeletedAt(Instant.now());
            entityManager.persist(applicationCategory);
            entityManager.flush();

            applicationService.addCategory(target, seedCategory);

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
            Category seedCategory = seedCategories.get(0);
            ApplicationHasCategory applicationCategory =
                new ApplicationHasCategory(target, seedCategory);
            entityManager.persist(applicationCategory);
            entityManager.flush();

            applicationService.removeCategory(target, seedCategory);

            assertTrue(applicationCategoryRepository.existsById(applicationCategory.getPk()));
            assertNotNull(applicationCategoryRepository.findById(applicationCategory.getPk()).get().getDeletedAt());
        }

        @Test
        @DisplayName("should fail to soft-delete an already soft-deleted application-category relationship")
        void shouldRejectAlreadySoftDeletedCategoryRelationship() {
            Application target = seedApplications.get(0);
            Category seedCategory = seedCategories.get(0);
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
