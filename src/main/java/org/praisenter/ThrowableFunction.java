package org.praisenter;

@FunctionalInterface
public interface ThrowableFunction<T, E> {
    E apply(T t) throws Exception;
}