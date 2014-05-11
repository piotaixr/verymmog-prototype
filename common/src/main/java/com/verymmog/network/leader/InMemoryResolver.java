package com.verymmog.network.leader;

import com.verymmog.model.map.ClusterInterface;
import com.verymmog.util.Callback;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Future;

public class InMemoryResolver implements LeaderAddressResolverInterface {

    private Map<ClusterInterface, InetSocketAddress> addresses;

    public InMemoryResolver(Map<ClusterInterface, InetSocketAddress> addresses) {
        this.addresses = addresses;
    }

    @Override
    public void resolve(ClusterInterface cluster, Callback<InetSocketAddress> callback) {
        callback.call(addresses.get(cluster));
    }
}
