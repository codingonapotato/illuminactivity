package com.codingonapotato.illuminactivity.application;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.codingonapotato.illuminactivity.category.Category;

@Service 
public class ApplicationServiceImpl implements ApplicationService {
    private ApplicationRepository applicationRepository;
    private ApplicationHasCategoryRepository applicationCategoryRepository;
    
    public ApplicationServiceImpl(ApplicationRepository applicationRepository, 
                                ApplicationHasCategoryRepository applicationCategoryRepository) {
        this.applicationRepository = applicationRepository;
        this.applicationCategoryRepository = applicationCategoryRepository;
    }
    
    @Override 
    public List<Application> getTrackedApplications() {
        return applicationRepository.findByTrackedIsTrue();
    }

    // TODO: Postponed to KAN-17
    @Override
    public List<Application> getAllApplications() {
        return new ArrayList<>();
    }

    @Override
    public void addCategory(Application target, Category category) {
        ApplicationHasCategory applicationCategory = new ApplicationHasCategory(target, category);
        applicationCategoryRepository.save(applicationCategory);
        target.getApplicationCategories().add(applicationCategory);
    }

    @Override
    public void removeCategory(Application target, Category category) {
        ApplicationHasCategory.PK pk = new ApplicationHasCategory.PK(target.getPath(), category.getName());
        ApplicationHasCategory applicationCategory = applicationCategoryRepository.findById(pk).get();
        applicationCategoryRepository.delete(applicationCategory);
        target.getApplicationCategories().remove(applicationCategory);
    }

    @Override
    public void setApplicationTracked(Application target, Boolean tracked) {
        target.setTracked(tracked);
        applicationRepository.save(target);
    }
}
