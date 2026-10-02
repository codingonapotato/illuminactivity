package com.codingonapotato.illuminactivity.category;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

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
        Category category = new Category(name, color);
        repository.save(category);
    }

    @Override
    public void editCategory(Category target, String newName, String newColor) {
        target.setName(newName);
        target.setColour(newColor);
        repository.save(target);
    }

    @Override
    public void deleteCategory(Category target) {
        repository.delete(target);
    }
}
