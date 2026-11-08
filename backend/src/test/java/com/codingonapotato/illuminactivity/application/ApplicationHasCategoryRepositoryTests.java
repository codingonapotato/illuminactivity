package com.codingonapotato.illuminactivity.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.codingonapotato.illuminactivity.category.Category;

@DataJpaTest
public class ApplicationHasCategoryRepositoryTests {

    @Autowired
    private ApplicationHasCategoryRepository repository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Application firstApplication =
        new Application("C:\\Example\\blitz.exe", "Blitz", true);
    private Application secondApplication =
        new Application("C:\\Example\\chrome.exe", "Chrome", true);
    private Category activeCategory = new Category("productivity", "#eb4034");
    private Category deletedCategory = new Category("entertainment", "#eb4034");
    private Category inactiveRelationshipCategory = new Category("education", "#eb4034");

    @BeforeEach
    void setup() {
        deletedCategory.setDeletedAt(Instant.now());
        entityManager.persist(firstApplication);
        entityManager.persist(secondApplication);
        entityManager.persist(activeCategory);
        entityManager.persist(deletedCategory);
        entityManager.persist(inactiveRelationshipCategory);

        entityManager.persist(new ApplicationHasCategory(firstApplication, activeCategory));
        entityManager.persist(new ApplicationHasCategory(secondApplication, activeCategory));

        ApplicationHasCategory deletedRelationship =
            new ApplicationHasCategory(firstApplication, inactiveRelationshipCategory);
        deletedRelationship.setDeletedAt(Instant.now());
        entityManager.persist(deletedRelationship);

        entityManager.persist(new ApplicationHasCategory(firstApplication, deletedCategory));
        entityManager.flush();
    }

    @AfterEach
    void teardown() {
        repository.deleteAll();
        applicationRepository.deleteAll();
        entityManager.remove(activeCategory);
        entityManager.remove(deletedCategory);
        entityManager.remove(inactiveRelationshipCategory);
    }

    @Nested
    class GetActiveApplicationCategoriesTests {

        @Test
        @DisplayName("should return only active categories for the requested application")
        void shouldReturnOnlyActiveCategoriesForApplication() {
            List<Category> categories =
                repository.getActiveApplicationCategories(firstApplication.getPath());

            assertEquals(1, categories.size());
            assertTrue(categories.contains(activeCategory));
        }
    }
}
