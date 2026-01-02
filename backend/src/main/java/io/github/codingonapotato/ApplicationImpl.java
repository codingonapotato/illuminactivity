package io.github.codingonapotato;

import java.lang.ProcessHandle;
import java.time.Instant;
import java.util.Optional;
import com.sun.jna.platform.win32.Version;
import com.sun.jna.Memory;

public class ApplicationImpl implements Application {
    private String category;
    private String name;
    private String productName;
    private short elapsedTime = 0; // in minutes
    private boolean tracked;
    private boolean alive = false;
    private Instant timeOfDeath;
    private ProcessHandle processHandle = null;

    /**
     * @param category The category for the application (e.g. "Game", "Productivity", etc.)
     * @param processHandle The ProcessHandle for the application
     */
    public ApplicationImpl(String category, String executableName, boolean tracked,
            Optional<ProcessHandle> processHandle, Optional<Short> elapsedTime,
            Optional<String> productName) {
        this.category = category;
        this.name = executableName;
        this.tracked = tracked;
        elapsedTime.ifPresentOrElse(elapsedTimeValue -> this.elapsedTime = elapsedTimeValue,
                () -> this.elapsedTime = 0);
        if (!processHandle.isPresent()) {
            this.timeOfDeath = Instant.now();
        } else {
            this.alive = true;
            this.processHandle = processHandle.get();
        }

        if (!productName.isPresent()) {
            try {
                this.productName = getProductNameFromProcessHandle(this.processHandle);
            } catch (Exception e) {
                this.productName = this.name;
            }
        } else {
            this.productName = productName.get();
        }
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return this.name;
    }

    public String getProductName() {
        return this.productName;
    }

    public short getElapsedTime() {
        return this.elapsedTime;
    }

    public void setElapsedTime(short iMinutes) {
        this.elapsedTime = iMinutes;
    }

    public boolean getTracked() {
        return this.tracked;
    }

    public void setTracked(boolean tracked) {
        this.tracked = tracked;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public Instant getTimeOfDeath() {
        return this.timeOfDeath;
    }

    public void setProcessHandle(ProcessHandle processHandle) {
        this.processHandle = processHandle;
    }

    /**
     * @param processHandle The ProcessHandle for the application
     * @return The product name for the application if available. Otherwise, returns the name of the
     *         executable.
     */
    private static String getProductNameFromProcessHandle(ProcessHandle processHandle)
            throws Exception {

        if (!processHandle.info().command().isPresent()) {
            throw new Exception("Could not get command from ProcessHandle"); // TODO stub
        }

        String commandPath = processHandle.info().command().get();
        int versionInfoSize = Version.INSTANCE.GetFileVersionInfoSize(commandPath, null);
        if (versionInfoSize == 0) {
            throw new Exception("Could not get version info size"); // TODO stub
        }
        Memory pointer;
        return ""; // TODO stub
    }
}
