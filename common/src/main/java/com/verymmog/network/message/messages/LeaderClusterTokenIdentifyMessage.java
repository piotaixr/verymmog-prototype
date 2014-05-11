package com.verymmog.network.message.messages;

public class LeaderClusterTokenIdentifyMessage extends TokenIdentifyMessage {
    private int clusterId;
    private int port;

    public LeaderClusterTokenIdentifyMessage(String id, String token, int clusterId, int port) {
        super(id, token);
        this.clusterId = clusterId;
        this.port = port;
    }

    public int getClusterId() {
        return clusterId;
    }

    public int getPort() {
        return port;
    }
}
