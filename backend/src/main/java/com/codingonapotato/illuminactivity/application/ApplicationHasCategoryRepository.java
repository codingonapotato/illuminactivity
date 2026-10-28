package com.codingonapotato.illuminactivity.application;

import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;

public interface ApplicationHasCategoryRepository extends JpaRepository<ApplicationHasCategory, ApplicationHasCategory.PK> {
    @Modifying 
    @NativeQuery(value = "UPDATE ApplicationHasCategory SET deleted_at = ?2, category = ?3 WHERE category = ?1")
    int updateApplicationCategoriesForCategory(String target, Instant deletedAt, String category);
}