package com.codingonapotato.illuminactivity.usage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.codingonapotato.illuminactivity.application.Application;
import com.codingonapotato.illuminactivity.application.ApplicationHasCategory;
import com.codingonapotato.illuminactivity.category.Category;

@Service 
public class UsageServiceImpl implements UsageService {
    private UsageRepository repository;

    public UsageServiceImpl(UsageRepository repository) {
        this.repository = repository;
    }

    @Override 
    public List<Usage> getUsage(LocalDate date) {
        List<Usage> usageList = new ArrayList<>();
        LocalDateTime startTime = LocalDateTime.from(date);
        repository.findAllByStartTime(startTime).forEach(usageList::add);
        return usageList;
    }

    @Override
    public List<Usage> getUsage(LocalDateTime start, LocalDateTime end) {
        List<Usage> usageList = new ArrayList<>();
        repository.findAllByStartAndEndTimeBetween(start, end).forEach(usageList::add);
        return usageList;
    }

    @Override
    public void addUsage(Application app, Category category, LocalDateTime start, LocalDateTime end) {
        ApplicationHasCategory applicationHasCategory = new ApplicationHasCategory(app, category);
        Usage.PK usagePK = new Usage.PK(applicationHasCategory.getPk(), start);
        Usage usage = new Usage(usagePK, end);

        repository.save(usage);
    }

    @Override 
    public void updateUsage(Usage usage, LocalDateTime start, LocalDateTime end) {
        usage.setEndTime(end);
        
        if (usage.getPK().getStartTime().equals(start)) {
            repository.save(usage);
            return;
        }

        repository.delete(usage);
        repository.flush();

        Usage.PK usagePK = new Usage.PK(usage.getPK().getApplicationHasCategoryPK(), start);
        repository.save(new Usage(usagePK, end));
    }

    @Override 
    public void deleteUsage(Usage usage) {
        repository.delete(usage);
    }
}