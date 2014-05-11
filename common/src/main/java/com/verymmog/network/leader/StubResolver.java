package com.verymmog.network.leader;

import com.verymmog.model.map.ClusterInterface;
import com.verymmog.util.Callback;
import com.verymmog.util.Ports;

import java.net.InetSocketAddress;

public class StubResolver implements LeaderAddressResolverInterface {

    InetSocketAddress leaderAddress;

    public StubResolver(InetSocketAddress leaderAddress) {
        this.leaderAddress = leaderAddress;
    }

    @Override
    public void resolve(ClusterInterface cluster, Callback<InetSocketAddress> callback) {
        callback.call(leaderAddress);
    }
}
