package com.codingonapotato.illuminactivity.category;

import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.codingonapotato.illuminactivity.category.exception.DuplicateCategoryException;

@Service
@Transactional (readOnly = true) 
public class CategoryServiceImpl implements CategoryService {
    private CategoryRepository categoryRepository;
    
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
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
            category.setDeletedAt(null);
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
        Instant deletionTime = Instant.now();
        target.setDeletedAt(deletionTime);
        categoryRepository.save(target);
    }

    private boolean activeCategoryExists(String name) {
        return categoryRepository.existsById(name) && categoryRepository.findById(name).get().getDeletedAt() == null;
    }
}