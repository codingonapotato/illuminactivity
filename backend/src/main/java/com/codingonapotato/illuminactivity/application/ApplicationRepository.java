package com.codingonapotato.illuminactivity.application;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ApplicationRepository extends CrudRepository<Application, String> {\
    List<Application> findByTrackedIsTrue();
}