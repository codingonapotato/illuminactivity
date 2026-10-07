package com.codingonapotato.illuminactivity.application;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service 
public class ApplicationServiceImpl implements ApplicationService {
    private ApplicationRepository repository;
    
    public ApplicationServiceImpl(ApplicationRepository repository) {
        this.repository = repository;
    }
    
    @Override 
    public List<Application> getTrackedApplications() {
        return repository.findByTrackedIsTrue();
    }

    // TODO: To be tackled at later stage of KAN-15
    @Override
    public List<Application> getAllApplications() {
        return new ArrayList<>();
    }

    @Override
    public void setApplicationTracked(Application target, Boolean tracked) {
        target.setTracked(tracked);
        repository.save(target);
    }
}
