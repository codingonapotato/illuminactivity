package com.codingonapotato.illuminactivity.category;

import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.codingonapotato.illuminactivity.application.ApplicationHasCategoryRepository;
import com.codingonapotato.illuminactivity.category.exception.DuplicateCategoryException;

@Service
@Transactional (readOnly = true) 
public class CategoryServiceImpl implements CategoryService {
    private CategoryRepository categoryRepository;
    private ApplicationHasCategoryRepository applicationCategoryRepository;
    
    public CategoryServiceImpl(CategoryRepository categoryRepository, ApplicationHasCategoryRepository applicationCategoryRepository) {
        this.categoryRepository = categoryRepository;
        this.applicationCategoryRepository = applicationCategoryRepository;
    }

    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll().stream().filter(category -> category.getDeletedAt() == null).toList();
    }

    @Override
    @Transactional
    public void createCategory(String name, String color) {
        Category category;
        if (activeCategoryExists(name))
            throw new DuplicateCategoryException(name);
        else if (categoryRepository.existsById(name)) {
            category = categoryRepository.findById(name).get();
            activateCategory(category);
            category.setColour(color);
        } else {
            category = new Category(name, color);
        }
        categoryRepository.save(category);
    }

    @Override
    @Transactional 
    public void editCategory(Category target, String newName, String newColor) {
        Category.validateInput(newName, newColor);
        boolean nameChangeRequired = !target.getName().equals(newName);
        if (nameChangeRequired && activeCategoryExists(newName))
            throw new DuplicateCategoryException(newName);
        else if (nameChangeRequired && categoryRepository.existsById(newName)) {
            deleteCategory(target);
            createCategory(newName, newColor);
        } else
            categoryRepository.updateCategory(target.getName(), newName, newColor, null);
    }

    @Override
    @Transactional 
    public void deleteCategory(Category target) {
        deactivateCategory(target);
        categoryRepository.save(target);
    }

    private void activateCategory(Category category) {
        category.setDeletedAt(null);
        updateApplicationCategories(category, null);
    }

    private void deactivateCategory(Category category) {
        Instant deletionTime = Instant.now();
        category.setDeletedAt(deletionTime);
        updateApplicationCategories(category, deletionTime);
    }

    private void updateApplicationCategories(Category target, Instant deletionTime) {
        applicationCategoryRepository.updateDeletedAtForCategory(target.getName(), deletionTime);
    }

    private boolean activeCategoryExists(String name) {
        return categoryRepository.existsById(name) && categoryRepository.findById(name).get().getDeletedAt() == null;
    }
}
