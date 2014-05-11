package com.verymmog.util;

/**
 * Represents a simple function with a generic parameter
 *
 * @param <T> The type of the callback's parameter
 */
public interface Callback<T> {
    public void call(T input);
}
