package com.codingonapotato.illuminactivity.usage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.codingonapotato.illuminactivity.application.Application;
import com.codingonapotato.illuminactivity.application.ApplicationHasCategory;
import com.codingonapotato.illuminactivity.application.ApplicationHasCategoryRepository;
import com.codingonapotato.illuminactivity.category.Category;

@Service 
public class UsageServiceImpl implements UsageService {
    private UsageRepository usageRepository;
    private ApplicationHasCategoryRepository applicationCategoryRepository;

    public UsageServiceImpl(UsageRepository usageRepository, ApplicationHasCategoryRepository applicationCategoryRepository) {
        this.usageRepository = usageRepository;
        this.applicationCategoryRepository = applicationCategoryRepository;
    }

    @Override 
    public List<Usage> getUsage(LocalDate date) {
        List<Usage> usageList = new ArrayList<>();
        LocalDateTime startTime = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
        usageRepository.findAllByStartAndEndTimeBetween(startTime, endTime).forEach(usageList::add);
        return usageList;
    }

    @Override
    public List<Usage> getUsage(LocalDateTime start, LocalDateTime end) {
        List<Usage> usageList = new ArrayList<>();
        usageRepository.findAllByStartAndEndTimeBetween(start, end).forEach(usageList::add);
        return usageList;
    }

    @Override
    public void addUsage(Application app, Category category, LocalDateTime start, LocalDateTime end) {
        ApplicationHasCategory.PK applicationCategoryPK = new ApplicationHasCategory.PK(app.getPath(), category.getName());
        ApplicationHasCategory applicationHasCategory = applicationCategoryRepository.getReferenceById(applicationCategoryPK);
        Usage usage = new Usage(applicationHasCategory, start, end);

        usageRepository.save(usage);
    }

    @Override 
    public void updateUsage(Usage usage, LocalDateTime start, LocalDateTime end) {
        usage.setEndTime(end);
        
        if (usage.getPK().getStartTime().equals(start)) {
            usageRepository.save(usage);
            return;
        }

        usageRepository.delete(usage);
        usageRepository.flush();

        usageRepository.save(new Usage(usage.getApplicationCategory(), start, end));
    }

    @Override 
    public void deleteUsage(Usage usage) {
        usageRepository.delete(usage);
    }
}