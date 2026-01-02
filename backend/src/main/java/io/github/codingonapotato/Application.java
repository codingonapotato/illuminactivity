package io.github.codingonapotato;

import java.time.Instant;

public interface Application {
    String getCategory();

    void setCategory(String category);

    String getName();

    String getProductName();

    short getElapsedTime(); // only for the current day

    void setElapsedTime(short iMinutes);

    boolean getTracked();

    void setTracked(boolean tracked);

    boolean isAlive();

    Instant getTimeOfDeath(); // only considered if isAlive() is true

    void setProcessHandle(ProcessHandle processHandle);
}
