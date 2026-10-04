package com.codingonapotato.illuminactivity.usage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsageRepository extends JpaRepository<Usage, Usage.PK> {
    List<Usage> findByDate(LocalDate date);
    List<Usage> findByDateBetween(LocalDateTime start, LocalDateTime end);
}