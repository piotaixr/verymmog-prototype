package com.verymmog.network;

import com.verymmog.network.message.messages.MessageInterface;
import com.verymmog.nioengine.EngineInterface;

public interface ClientNetworkInterface {

    public void sendLeader(MessageInterface message);

    public EngineInterface getEngine();
}