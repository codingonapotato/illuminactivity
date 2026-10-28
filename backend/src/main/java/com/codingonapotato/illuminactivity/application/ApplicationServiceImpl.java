package com.codingonapotato.illuminactivity.application;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.codingonapotato.illuminactivity.application.exception.DuplicateApplicationCategoryException;
import com.codingonapotato.illuminactivity.application.exception.InactiveApplicationCategoryException;
import com.codingonapotato.illuminactivity.category.Category;

@Service 
@Transactional (readOnly = true)
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
    @Transactional 
    public void addCategory(Application target, Category category) {
        ApplicationHasCategory applicationCategory = new ApplicationHasCategory(target, category);
        ApplicationHasCategory.PK pk = applicationCategory.getPk();
        Optional<ApplicationHasCategory> optional = applicationCategoryRepository.findById(pk);
        if (optional.isPresent()) {
            applicationCategory = optional.get();
            if (applicationCategory.getDeletedAt() == null) {
                throw new DuplicateApplicationCategoryException(applicationCategory);
            }
            applicationCategory.setDeletedAt(null);
        }
        
        target.getApplicationCategories().add(applicationCategory);
        category.getCategoryApplications().add(applicationCategory);
        applicationCategoryRepository.save(applicationCategory);
    }

    @Override
    @Transactional
    public void removeCategory(Application target, Category category) {
        ApplicationHasCategory.PK pk = new ApplicationHasCategory.PK(target.getPath(), category.getName());
        ApplicationHasCategory applicationCategory = applicationCategoryRepository.findById(pk).get();
        
        if (applicationCategory.getDeletedAt() != null)
            throw new InactiveApplicationCategoryException(applicationCategory);
        
        applicationCategory.setDeletedAt(Instant.now());

        target.getApplicationCategories().remove(applicationCategory);
        category.getCategoryApplications().remove(applicationCategory);
        applicationCategoryRepository.save(applicationCategory);
    }

    @Override
    @Transactional
    public void setApplicationTracked(Application target, Boolean tracked)
        target.setTracked(tracked);
        applicationRepository.save(target);
    }
}
