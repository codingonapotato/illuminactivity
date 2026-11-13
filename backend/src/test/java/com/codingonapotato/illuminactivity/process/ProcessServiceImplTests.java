package com.codingonapotato.illuminactivity.process;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import oshi.software.os.OSProcess;

public class ProcessServiceImplTests {

    private final ProcessService processService = new ProcessServiceImpl();

    @Nested
    class GetUserProcessesTests {

        @Test
        @DisplayName("should return only processes owned by the current user with non-empty paths")
        void shouldReturnCurrentUserProcessesWithNonEmptyPaths() {
            List<OSProcess> processes = processService.getUserProcesses();
            String currentUser = System.getProperty("user.name");

            processes.forEach(process -> {
                assertTrue(process.getUser().equals(currentUser));
                assertTrue(process.getPath().length() >= 1);
            });
        }
    }
}
