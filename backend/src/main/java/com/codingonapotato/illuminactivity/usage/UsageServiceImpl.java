package com.codingonapotato.illuminactivity.usage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.codingonapotato.illuminactivity.application.Application;
import com.codingonapotato.illuminactivity.category.Category;

@Service 
public class UsageServiceImpl implements UsageService {
    
    @Override 
    public List<Usage> getUsage(LocalDate date) {
        return new ArrayList<>();
    }

    @Override
    public List<Usage> getUsage(LocalDateTime start, LocalDateTime end) {
        return new ArrayList<>();
    }

    @Override
    public void addUsage(Application app, Category category, LocalDateTime start, LocalDateTime end) {
        return;
    }

    @Override 
    public void updateUsage(Application app, Category category, LocalDateTime start, LocalDateTime end) {
        return;
    }

    @Override 
    public void deleteUsage(Application app, Category category, LocalDateTime start) {
        return;
    }
}