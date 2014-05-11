package com.verymmog.nioengine.extension;

import java.nio.channels.SocketChannel;

public abstract class TryConnectionListener {
    public void onError() {

    }

    public void onSuccess(SocketChannel channel) {

    }
}
