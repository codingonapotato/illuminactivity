package com.codingonapotato.illuminactivity.category;

import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;

public interface CategoryRepository extends JpaRepository<Category, String> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @NativeQuery(value = "UPDATE Category SET name = ?2, colour = ?3, deleted_at = ?4 WHERE name = ?1")
    int updateCategory(String target, String name, String colour, Instant deletedAt);
}