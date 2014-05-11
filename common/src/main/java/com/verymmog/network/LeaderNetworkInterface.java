package com.verymmog.network;

import com.verymmog.network.message.messages.MessageInterface;
import com.verymmog.nioengine.EngineInterface;

public interface LeaderNetworkInterface {

    public void sendCluster(MessageInterface message);

    public EngineInterface getEngine();
}
