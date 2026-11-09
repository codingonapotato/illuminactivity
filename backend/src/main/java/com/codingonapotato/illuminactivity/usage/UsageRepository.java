package com.codingonapotato.illuminactivity.usage;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsageRepository extends JpaRepository<Usage, Usage.PK> {
    @Query("select u from Usage u where u.pk.startTime >= ?1 AND u.pk.startTime <= ?2")
    List<Usage> findAllByStartTimeBetween(Instant start, Instant end);
}