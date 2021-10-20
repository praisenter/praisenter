package org.praisenter;

@FunctionalInterface
public interface ThrowableRunnable {
    void run() throws Exception;
}