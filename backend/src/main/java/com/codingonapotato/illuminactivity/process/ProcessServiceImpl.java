package com.codingonapotato.illuminactivity.process;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.stereotype.Service;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.spi.SystemInfoFactory;

@Service 
public class ProcessServiceImpl implements ProcessService {
    public ProcessServiceImpl() {};
    
    @Override
    public List<OSProcess> getUserProcesses() {
        OperatingSystem os = SystemInfoFactory.create().getOperatingSystem();
        int limit = 0;
        Predicate<OSProcess> filter = getUserProcessPredicate();
        Comparator<OSProcess> comparator = null;

        return os.getProcesses(filter, comparator, limit);
    }

    private Predicate<OSProcess> getUserProcessPredicate() {
        String currentUser = System.getProperty("user.name");
        Predicate<OSProcess> filter = p -> p.getPath().length() >= 1 && p.getUser().equals(currentUser);

        return filter;
    }
}