package com.codingonapotato.illuminactivity;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.codingonapotato.illuminactivity.application.Application;
import com.codingonapotato.illuminactivity.application.ApplicationService;

@RestController
public class GreetingController {
  private final ApplicationService applicationService;

  public GreetingController(ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  @GetMapping("/apps")
  public List<Application> greeting(@RequestParam(defaultValue = "World") String name) {
    return applicationService.getAllApplications();
  }
}