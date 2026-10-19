package com.codingonapotato.illuminactivity.category;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.codingonapotato.illuminactivity.application.ApplicationHasCategory;
import com.codingonapotato.illuminactivity.application.ApplicationHasCategoryRepository;
import com.codingonapotato.illuminactivity.category.exception.DuplicateCategoryException;

@Service 
public class CategoryServiceImpl implements CategoryService {
    private CategoryRepository categoryRepository;
    private ApplicationHasCategoryRepository applicationCategoryRepository;
    
    public CategoryServiceImpl(CategoryRepository categoryRepository, ApplicationHasCategoryRepository applicationCategoryRepository) {
        this.categoryRepository = categoryRepository;
        this.applicationCategoryRepository = applicationCategoryRepository;
    }

    @Override
    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        categoryRepository.findAll().forEach(categories::add);

        return categories;
    }

    @Override
    public void createCategory(String name, String color) {
        Category category;
        if (activeCategoryExists(name))
            throw new DuplicateCategoryException(name);
        else if (categoryRepository.existsById(name)) {
            category = categoryRepository.findById(name).get();
            category.setDeletedAt(null);
            updateApplicationCategories(category, null);
        } else {
            category = new Category(name, color);
        }
        categoryRepository.save(category);
    }

    @Override
    public void editCategory(Category target, String newName, String newColor) {
        // name change required + active category already exists with the desired name
        if (!target.getName().equals(newName) && activeCategoryExists(newName))
            throw new DuplicateCategoryException(newName);
        else if (!target.getName().equals(newName)) { // name change required + activeCategory DNE
            categoryRepository.delete(target);
            categoryRepository.flush();

            categoryRepository.save(new Category(newName, newColor));
        } else { // no name change required. activeCategoryExists must be true here to make sense since editing an inactive category makes no sense
            target.setColour(newColor);
            categoryRepository.save(target);
        }
    }

    @Override
    public void deleteCategory(Category target) {
        Instant deletionTime = Instant.now();
        target.setDeletedAt(deletionTime);
        categoryRepository.save(target);

        updateApplicationCategories(target, deletionTime);
    }

    private void updateApplicationCategories(Category target, Instant deletionTime) {
        List<ApplicationHasCategory> applicationCategories = applicationCategoryRepository.findAll();
        applicationCategories.forEach((applicationCategory) -> {
            if (applicationCategory.getCategory().equals(target)) {
                applicationCategory.setDeletedAt(deletionTime);
                applicationCategoryRepository.save(applicationCategory);
            }
        });
    }

    public boolean activeCategoryExists(String name) {
        return categoryRepository.existsById(name) && categoryRepository.findById(name).get().getDeletedAt() == null;
    }
}
