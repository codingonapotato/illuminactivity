package com.codingonapotato.illuminactivity.usage;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.codingonapotato.illuminactivity.application.Application;
import com.codingonapotato.illuminactivity.category.Category;

public interface UsageService {
    List<Usage> getUsage(LocalDate date);
    List<Usage> getUsage(LocalDateTime start, LocalDateTime end);
    void addUsage(Application app, Category category, Instant start, Instant end);
    void updateUsage(Usage usage, Instant start, Instant end);
    void deleteUsage(Usage usage);
}