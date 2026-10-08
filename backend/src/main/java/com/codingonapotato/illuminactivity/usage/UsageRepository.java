package com.codingonapotato.illuminactivity.usage;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsageRepository extends JpaRepository<Usage, Usage.PK> {
    @Query("select u from Usage u where u.pk.startTime >= ?1")
    List<Usage> findAllByStartTime(LocalDateTime start);
    
    @Query("select u from Usage u where u.pk.startTime >= ?1 AND u.endTime <= ?2")
    List<Usage> findAllByStartAndEndTimeBetween(LocalDateTime start, LocalDateTime end);
}