package com.codingonapotato.illuminactivity.category;

import java.util.List;

public interface CategoryService {
    List<Category> getCategories();
    void createCategory(String name, String colour);
    void editCategory(Category target, String newName, String newColour);
    void deleteCategory(Category target);
}