package com.codingonapotato.illuminactivity.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
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
import com.codingonapotato.illuminactivity.application.Application;
import com.codingonapotato.illuminactivity.application.ApplicationHasCategory;
import com.codingonapotato.illuminactivity.application.ApplicationHasCategoryRepository;
import com.codingonapotato.illuminactivity.application.ApplicationRepository;
import com.codingonapotato.illuminactivity.category.exception.DuplicateCategoryException;

@DataJpaTest 
@Import(CategoryServiceImpl.class)
public class CategoryServiceImplTests {

    @Autowired 
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ApplicationHasCategoryRepository applicationCategoryRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    private List<Category> seedCategories = List.of(
        new Category("productivity", "#eb4034"),
        new Category("entertainment", "#eb4034"),
        new Category("wedding planning", "#eb4034") 
    );

    @BeforeEach 
    void setup() {
        seedCategories.forEach(entityManager::persist);
    }

    @AfterEach 
    void teardown() {
        applicationCategoryRepository.deleteAll();
        repository.deleteAll();
        applicationRepository.deleteAll();
    }

    @Nested
    class GetCategoriesTests {
        
        @Test 
        @DisplayName("should return all categories")
        void shouldReturnAllCategories() {
            int expectedSize = seedCategories.size();
            List<Category> categories = categoryService.getCategories();
            
            assertEquals(categories.size(), expectedSize);
            seedCategories.forEach((seedCategory) -> {
                assertTrue(categories.contains(seedCategory));
            });
        }
    }

    @Nested
    class CreateCategoryTests {
        
        @Test 
        @DisplayName("should create new category")
        void shouldCreateNewCategory() {
            String name = "Bing Chilling";
            String colour =  "#FFFFFF";
            
            try {
                categoryService.createCategory(name, colour);
                assertTrue(repository.existsById(name));
            } catch (Exception e) {
                fail("Should not have thrown exception");
            }
        }

        @Test 
        @DisplayName("should reject existing category")
        void shouldRejectExistingCategory() {
            String name = "Bing Chilling";
            String colour =  "#FFFFFF";

            try {
                categoryService.createCategory(name, colour);
                categoryService.createCategory(name, colour);
                fail("Should have thrown exception");
            } catch (Exception e) {
                assertTrue(e instanceof DuplicateCategoryException);
            }
        }

        @Test
        @DisplayName("should have category with same name as soft-deleted one with updated colour")
        void shouldReactivateCategoryWithUpdatedColour() {
            Category target = seedCategories.get(0);
            String name = target.getName();
            String newColour = "#111111";

            categoryService.deleteCategory(target);
            categoryService.createCategory(name, newColour);

            Category reactivatedCategory = repository.findById(name).get();
            assertNull(reactivatedCategory.getDeletedAt());
            assertEquals(newColour, reactivatedCategory.getColour());
        }
    }

    @Nested
    class EditCategoryTests {
        
        @Test 
        @DisplayName("should update category name and colour")
        void shouldUpdateCategory() {
            String newName = "Bing Chilling";
            String newColour =  "#FFFFFF";
            Category target = seedCategories.get(0);
            String targetName = target.getName();

            try {
                categoryService.editCategory(target, newName, newColour);
                assertTrue(repository.existsById(newName));
                assertFalse(repository.existsById(targetName));
            } catch (Exception e) {
                fail("Should not have thrown exception");
            }
        }

        @Test 
        @DisplayName("should reject update category name to one that already exists")
        void shouldRejectUpdateNameToExistingCategory() {
            Category target = seedCategories.get(0);
            String newName = seedCategories.get(1).getName();
            String newColour =  "#FFFFFF";

            try {
                categoryService.editCategory(target, newName, newColour);
                fail("Should have thrown exception");
            } catch (Exception e) {
                assertTrue(e instanceof DuplicateCategoryException);
            }
        }

        @Test
        @DisplayName("should reactivate soft-deleted category and deactivate the old one")
        void shouldReactivateSoftDeletedCategoryWhenRenaming() {
            Category category1 = seedCategories.get(0);
            Category category2 = seedCategories.get(1);
            List<Application> applications = List.of(
                new Application("C:\\Example\\category-test-1.exe", "Category Test 1", true),
                new Application("C:\\Example\\category-test-2.exe", "Category Test 2", true)
            );

            applications.forEach(application -> {
                entityManager.persist(application);
                entityManager.persist(new ApplicationHasCategory(application, category1));
                entityManager.persist(new ApplicationHasCategory(application, category2));
            });
            entityManager.flush();

            String newColour = "#FFFFFF";
            categoryService.deleteCategory(category1);
            categoryService.editCategory(category2, category1.getName(), newColour);

            entityManager.flush();
            entityManager.clear();

            assertNull(repository.findById(category1.getName()).get().getDeletedAt());
            assertEquals(repository.findById(category1.getName()).get().getColour(), newColour);
            assertNotNull(repository.findById(category2.getName()).get().getDeletedAt());

            applications.forEach(application -> {
                ApplicationHasCategory category1Pair = applicationCategoryRepository.findById(
                    new ApplicationHasCategory.PK(application.getPath(), category1.getName())).get();
                ApplicationHasCategory category2Pair = applicationCategoryRepository.findById(
                    new ApplicationHasCategory.PK(application.getPath(), category2.getName())).get();

                assertNull(category1Pair.getDeletedAt());
                assertNull(category2Pair.getDeletedAt());
            });
        }
    }

    @Nested
    class DeleteCategoryTests {
        
        @Test 
        @DisplayName("should delete category")
        void shouldDeleteCategory() {
            Category target = seedCategories.get(0);
            String targetName = target.getName();

            categoryService.deleteCategory(target);
            assertTrue(repository.existsById(targetName));
            assertTrue(repository.findById(targetName).get().getDeletedAt() instanceof Instant);
        }
    }
}
