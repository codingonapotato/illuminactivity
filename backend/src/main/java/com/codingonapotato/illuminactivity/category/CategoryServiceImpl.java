package com.codingonapotato.illuminactivity.category;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public void createCategory(String name, String color) {
        Category category;
        if (activeCategoryExists(name))
            throw new DuplicateCategoryException(name);
        else if (categoryRepository.existsById(name)) {
            category = categoryRepository.findById(name).get();
            category.setDeletedAt(null);
            updateApplicationCategories(category, null, category);
        } else {
            category = new Category(name, color);
        }
        categoryRepository.save(category);
    }

    @Override
    @Transactional 
    public void editCategory(Category target, String newName, String newColor) {
        boolean nameChangeRequired = !target.getName().equals(newName);
        if (nameChangeRequired && activeCategoryExists(newName))
            throw new DuplicateCategoryException(newName);
        else if (nameChangeRequired && inactiveCategoryExists(newName)) {
            Category category = categoryRepository.findById(newName).get();
            categoryRepository.updateCategory(newName, newName, newColor, null);
            // migrate app-category pairs from old category to point to new category
            updateApplicationCategories(target, null, category);
            categoryRepository.deleteById(target.getName());
        }
        else
            categoryRepository.updateCategory(target.getName(), newName, newColor, null);
    }

    @Transactional 
    @Override
    public void deleteCategory(Category target) {
        Instant deletionTime = Instant.now();
        target.setDeletedAt(deletionTime);
        categoryRepository.save(target);

        updateApplicationCategories(target, deletionTime, target);
    }

    private void updateApplicationCategories(Category target, Instant deletionTime, Category category) {
        applicationCategoryRepository.updateApplicationCategoriesForCategory(target.getName(), deletionTime, category.getName());
    }

    private boolean activeCategoryExists(String name) {
        return categoryRepository.existsById(name) && categoryRepository.findById(name).get().getDeletedAt() == null;
    }

    private boolean inactiveCategoryExists(String name) {
        return categoryRepository.existsById(name) && categoryRepository.findById(name).get().getDeletedAt() != null;
    }
}
