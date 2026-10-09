package com.codingonapotato.illuminactivity.application;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationHasCategoryRepository extends JpaRepository<ApplicationHasCategory, ApplicationHasCategory.PK> {}