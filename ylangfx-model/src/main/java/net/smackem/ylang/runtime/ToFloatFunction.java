package net.smackem.ylang.runtime;

@FunctionalInterface
public interface ToFloatFunction<T> {
    float apply(T value);
}
