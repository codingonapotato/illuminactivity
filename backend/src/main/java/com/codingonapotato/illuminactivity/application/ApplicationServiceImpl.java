package com.codingonapotato.illuminactivity.application;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.codingonapotato.illuminactivity.application.exception.DuplicateApplicationCategoryException;
import com.codingonapotato.illuminactivity.application.exception.InactiveApplicationCategoryException;
import com.codingonapotato.illuminactivity.category.Category;
import com.codingonapotato.illuminactivity.process.ProcessService;
import oshi.software.os.OSProcess;

@Service 
@Transactional (readOnly = true)
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final ApplicationHasCategoryRepository applicationCategoryRepository;
    private final ProcessService processService;
    
    public ApplicationServiceImpl(ApplicationRepository applicationRepository, 
                                ApplicationHasCategoryRepository applicationCategoryRepository,
                                ProcessService processService) {
        this.applicationRepository = applicationRepository;
        this.applicationCategoryRepository = applicationCategoryRepository;
        this.processService = processService;
    }
    
    @Override 
    public List<Application> getTrackedApplications() {
        return applicationRepository.findByTrackedIsTrue();
    }
    
    @Override
    public List<Application> getAllApplications() {
        CompletableFuture<List<OSProcess>> processesFuture = CompletableFuture.supplyAsync(processService::getUserProcesses);
        CompletableFuture<Iterable<Application>> applicationsFuture = CompletableFuture.supplyAsync(applicationRepository::findAll);
        
        List<OSProcess> processes = processesFuture.join();
        Iterable<Application> persistedApps = applicationsFuture.join();

        List<Application> applications = new ArrayList<>();
        Set<String> paths = new HashSet<>();
        persistedApps.forEach(app -> {
            paths.add(app.getPath());
            applications.add(app);
        });
        processes.forEach(process -> {
            if (paths.contains(process.getPath()))
                return;
            paths.add(process.getPath());
            boolean tracked = false; 
            applications.add(new Application(process.getPath(), process.getName(), tracked));
        });

        applications.sort(Comparator.comparing(Application::getProductName, String.CASE_INSENSITIVE_ORDER));

        return applications;
    }

    @Override
    public List<Category> getCategories(Application target) {
        return applicationCategoryRepository.getActiveApplicationCategories(target.getPath());
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
        applicationCategoryRepository.save(applicationCategory);
    }

    @Override
    @Transactional
    public void setApplicationTracked(Application target, Boolean tracked) {
        target.setTracked(tracked);
        applicationRepository.save(target);
    }
}
