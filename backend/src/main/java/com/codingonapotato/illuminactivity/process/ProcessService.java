package com.codingonapotato.illuminactivity.process;

import java.util.List;
import oshi.software.os.OSProcess;

public interface ProcessService {
    List<OSProcess> getUserProcesses();   
}
