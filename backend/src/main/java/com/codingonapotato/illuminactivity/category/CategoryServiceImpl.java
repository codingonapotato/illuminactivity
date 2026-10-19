package com.codingonapotato.illuminactivity.category;

import java.time.Instant;
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
        Category category;
        if (activeCategoryExists(name))
            throw new DuplicateCategoryException(name);
        else if (repository.existsById(name)) {
            category = repository.findById(name).get();
            category.setDeletedAt(null);
        } else {
            category = new Category(name, color);
        }
        repository.save(category);
    }

    @Override
    public void editCategory(Category target, String newName, String newColor) {
        // name change required + active category already exists with the desired name
        if (!target.getName().equals(newName) && activeCategoryExists(newName))
            throw new DuplicateCategoryException(newName);
        else if (!target.getName().equals(newName)) { // name change required + activeCategory DNE
            repository.delete(target);
            repository.flush();

            repository.save(new Category(newName, newColor));
        } else { // no name change required. activeCategoryExists must be true here to make sense since editing an inactive category makes no sense
            target.setColour(newColor);
            repository.save(target);
        }

        

    }

    @Override
    public void deleteCategory(Category target) {
        target.setDeletedAt(Instant.now());
        repository.save(target);
    }

    public boolean activeCategoryExists(String name) {
        return repository.existsById(name) && repository.findById(name).get().getDeletedAt() == null;
    }
}
