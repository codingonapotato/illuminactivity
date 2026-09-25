package com.codingonapotato.illuminactivity.category;

import java.util.List;

public interface CategoryService {
    List<Category> getCategories();
    void createCategory(String name, String color);
    void editCategory(Category target, String newName, String newColor);
    void deleteCategory(Category target);
}