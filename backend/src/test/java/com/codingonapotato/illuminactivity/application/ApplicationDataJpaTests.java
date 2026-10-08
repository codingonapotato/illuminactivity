package com.codingonapotato.illuminactivity.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Transactional 
public class ApplicationDataJpaTests {
    static private List<Application> tracked = List.of(
        new Application("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe", "Google Chrome", true), 
        new Application("C:\\Example\\blitz.exe", "Blitz", true)
    );
    static private List<Application> untracked = List.of(new Application("C:\\Example\\notepad.exe", "Notepad", false));
    
	@Autowired
	private ApplicationRepository repository;

    @Autowired 
    private TestEntityManager entityManager;

    @BeforeEach 
    void setup() {
        ApplicationDataJpaTests.tracked.forEach(entityManager::persist);
        ApplicationDataJpaTests.untracked.forEach(entityManager::persist);
    }

    @AfterEach
    void teardown() {
        ApplicationDataJpaTests.tracked.forEach(entityManager::remove);
        ApplicationDataJpaTests.untracked.forEach(entityManager::remove);
    }

    @Test
	void testGetTrackedApplications() {
        int expectedSize = 2;
        List<Application> applications = repository.findByTrackedIsTrue();
        
        assertEquals(applications.size(), expectedSize);
        assertIterableEquals(tracked, applications);
	}
}
