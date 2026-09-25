package com.codingonapotato.illuminactivity.application;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service 
public class ApplicationServiceImpl implements ApplicationService {
    @Override 
    public List<Application> getTrackedApplications() {
        return new ArrayList<>();
    }

    @Override
    public List<Application> getAllApplications() {
        return new ArrayList<>();
    }

    @Override
    public void setApplicationTracked(Application target, Boolean tracked) {
        return;
    }
}
