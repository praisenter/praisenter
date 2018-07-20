package org.praisenter;

@FunctionalInterface
public interface ThrowableSupplier<T> {
    T get() throws Exception;
}