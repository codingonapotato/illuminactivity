package com.codingonapotato.illuminactivity.process;

import java.util.List;
import java.util.Optional;
import oshi.software.os.OSProcess;

public interface ProcessService {
    List<OSProcess> getUserProcesses();
    Optional<OSProcess> getForegroundProcess();   
}
