package com.verymmog.network.leader.clientrepresentation;

import java.nio.channels.SocketChannel;

public class LeaderClient {
    private String id;
    private SocketChannel channel;

    public LeaderClient(SocketChannel channel) {
        this.channel = channel;
        id = null;
    }

    public String getId() {

        return id == null ? "Inconnu" : id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isIdentified() {
        return id != null;
    }

    public SocketChannel getChannel() {
        return channel;
    }
}
