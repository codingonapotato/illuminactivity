package com.codingonapotato.illuminactivity.usage;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.codingonapotato.illuminactivity.application.Application;
import com.codingonapotato.illuminactivity.category.Category;

public interface UsageService {
    List<Usage> getUsage(LocalDate date);
    List<Usage> getUsage(LocalDateTime start, LocalDateTime end);
    void addUsage(Application app, Category category, LocalDateTime start, LocalDateTime end);
    void updateUsage(Application app, Category category, LocalDateTime start, LocalDateTime end);
    void deleteUsage(Application app, Category category, LocalDateTime start);
}