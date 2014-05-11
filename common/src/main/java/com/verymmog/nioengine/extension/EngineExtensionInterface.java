package com.verymmog.nioengine.extension;

import com.verymmog.nioengine.EngineInterface;

public interface EngineExtensionInterface extends EngineAwareInterface {
    public void boot();

    public void shutdown();

    public boolean isRunning();

    public EngineInterface getEngine();
}
