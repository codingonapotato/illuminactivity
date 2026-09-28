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
        return new ArrayList<>();
    }

    @Override
    public void createCategory(String name, String color) {
        return;
    }

    @Override
    public void editCategory(Category target, String newName, String newColor) {
        return;
    }

    @Override
    public void deleteCategory(Category target) {
        return;
    }
}
