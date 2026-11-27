package com.codingonapotato.illuminactivity.process;

import java.util.List;
import java.util.Optional;
import oshi.software.os.OSProcess;

public interface ProcessService {
    /**
     * Return a list of currently running user processes
     * @return List of user processes
     */
    List<OSProcess> getUserProcesses();

    /**
     * Find and return the process running in the foreground
     * @return foregroundProcess
     */
    Optional<OSProcess> getForegroundProcess();   
}
