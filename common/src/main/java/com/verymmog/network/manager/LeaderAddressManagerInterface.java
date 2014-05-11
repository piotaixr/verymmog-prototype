package com.verymmog.network.manager;

import com.verymmog.model.map.ClusterInterface;
import com.verymmog.network.leader.LeaderAddressResolverInterface;

import java.net.InetSocketAddress;

public interface LeaderAddressManagerInterface extends LeaderAddressResolverInterface {

    public void register(ClusterInterface cluster, InetSocketAddress address);

    public void unregister(InetSocketAddress address);
}
