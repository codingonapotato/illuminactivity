package com.codingonapotato.illuminactivity.process;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.stereotype.Service;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.IntByReference;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.spi.SystemInfoFactory;

@Service 
public class ProcessServiceImpl implements ProcessService {
    private final OperatingSystem os;

    public ProcessServiceImpl() {
        os = SystemInfoFactory.create().getOperatingSystem();
    };
    
    @Override
    public List<OSProcess> getUserProcesses() {
        int limit = 0;
        Predicate<OSProcess> filter = getUserProcessPredicate();
        Comparator<OSProcess> comparator = null;

        return os.getProcesses(filter, comparator, limit);
    }

    @Override 
    public Optional<OSProcess> getForegroundProcess() {
        HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        if (hwnd == null || Pointer.nativeValue(hwnd.getPointer()) == 0) {
            return Optional.empty();
        }

        IntByReference pidRef = new IntByReference();
        int threadId = User32.INSTANCE.GetWindowThreadProcessId(hwnd, pidRef);
        int pid = pidRef.getValue();

        if (threadId == 0 || pid <= 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(os.getProcess(pid));
    }

    private Predicate<OSProcess> getUserProcessPredicate() {
        String currentUser = System.getProperty("user.name");
        Predicate<OSProcess> filter = p -> p.getPath().length() >= 1 && p.getUser().equals(currentUser);

        return filter;
    }
}