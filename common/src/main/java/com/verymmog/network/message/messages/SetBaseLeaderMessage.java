package com.verymmog.network.message.messages;

import java.net.InetSocketAddress;

public class SetBaseLeaderMessage implements MessageInterface {

    private InetSocketAddress leaderAddress;

    public SetBaseLeaderMessage(InetSocketAddress leaderAddress) {
        this.leaderAddress = leaderAddress;
    }

    public InetSocketAddress getLeaderAddress() {
        return leaderAddress;
    }
}
