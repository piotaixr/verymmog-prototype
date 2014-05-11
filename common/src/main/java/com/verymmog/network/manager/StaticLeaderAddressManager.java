package com.verymmog.network.manager;

import com.verymmog.model.map.ClusterInterface;
import com.verymmog.network.leader.StubResolver;

import java.net.InetSocketAddress;

public class StaticLeaderAddressManager extends StubResolver implements LeaderAddressManagerInterface {

    public StaticLeaderAddressManager(InetSocketAddress leaderAddress) {
        super(leaderAddress);
    }

    @Override
    public void register(ClusterInterface cluster, InetSocketAddress address) {

    }

    @Override
    public void unregister(InetSocketAddress address) {

    }
}
