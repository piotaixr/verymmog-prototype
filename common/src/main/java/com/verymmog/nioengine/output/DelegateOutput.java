package com.verymmog.nioengine.output;

public abstract class DelegateOutput<T, I> implements OutputInterface<T> {

    private OutputInterface<I> delegate;

    protected OutputInterface<I> getDelegate() {
        return delegate;
    }

    public DelegateOutput(OutputInterface<I> delegate) {
        this.delegate = delegate;
    }

}
