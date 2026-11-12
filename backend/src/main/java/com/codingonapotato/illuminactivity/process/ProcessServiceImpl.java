package com.codingonapotato.illuminactivity.process;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.springframework.stereotype.Service;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.spi.SystemInfoFactory;

@Service 
public class ProcessServiceImpl implements ProcessService {
    public ProcessServiceImpl() {};
    
    /**
     * TODO: Incomplete
     */
    @Override
    public List<OSProcess> getUserProcesses() {
        OperatingSystem os = SystemInfoFactory.create().getOperatingSystem();
        int limit = 0;
        Predicate<OSProcess> filter = p -> p.getPath().length() > 1; // TODO: I think there's a way to filter for only user processes through OSSession and usernames
        Comparator<OSProcess> comparator = null;
        List<OSProcess> processes = os.getProcesses(filter, comparator, limit);
        Map<String, String> paths = new HashMap<>();
        processes.forEach(process -> {
            if (paths.get(process.getPath()) == null) 
                paths.put(process.getPath(), process.getName());
        });
        
        return processes;
    }
}
