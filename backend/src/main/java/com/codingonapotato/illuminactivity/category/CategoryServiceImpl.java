package com.codingonapotato.illuminactivity.category;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.codingonapotato.illuminactivity.category.exception.DuplicateCategoryException;

@Service 
public class CategoryServiceImpl implements CategoryService {
    private CategoryRepository repository;
    
    public CategoryServiceImpl(CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        repository.findAll().forEach(categories::add);

        return categories;
    }

    @Override
    public void createCategory(String name, String color) {
        if (repository.existsById(name))
            throw new DuplicateCategoryException(name);
        Category category = new Category(name, color);
        repository.save(category);
    }

    @Override
    public void editCategory(Category target, String newName, String newColor) {
        if (!target.getName().equals(newName) && repository.existsById(newName))
            throw new DuplicateCategoryException(newName);
        
        target.setColour(newColor);
        if (target.getName().equals(newName)) {
            repository.save(target);
            return;
        }

        repository.delete(target);
        repository.flush();

        repository.save(new Category(newName, newColor));
    }

    @Override
    public void deleteCategory(Category target) {
        repository.delete(target);
    }
}
