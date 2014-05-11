package com.verymmog.network.leader;

import com.verymmog.model.map.ClusterInterface;
import com.verymmog.util.Callback;

import java.net.InetSocketAddress;

public interface LeaderAddressResolverInterface {

    public void resolve(ClusterInterface cluster, Callback<InetSocketAddress> callback);

}
