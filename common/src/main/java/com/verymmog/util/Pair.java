package com.verymmog.util;

/**
 * A simple tuple with generic class types
 *
 * @param <V1>
 * @param <V2>
 */
public class Pair<V1, V2> {
    public V1 first;
    public V2 second;

    public Pair(V1 first, V2 second) {
        this.first = first;
        this.second = second;
    }

}
