package com.codingonapotato.illuminactivity.category;

import java.util.List;

public interface CategoryService {
    /**
     * Finds and returns a list of all active categories
     * @return List of active categories
     */
    List<Category> getCategories();

    /**
     * Create a category
     * @param name
     * @param colour
     */
    void createCategory(String name, String colour);

    /**
     * Update a category
     * @param target
     * @param newName
     * @param newColour
     */
    void editCategory(Category target, String newName, String newColour);

    /**
     * Delete a category
     * @param target
     */
    void deleteCategory(Category target);
}