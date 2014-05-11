package com.verymmog.nioengine.output;

public interface OutputInterface<T> {
    public OutputInterface<T> send(T data);
}
