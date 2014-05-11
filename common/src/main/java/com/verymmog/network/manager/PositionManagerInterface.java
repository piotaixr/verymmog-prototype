package com.verymmog.network.manager;

import com.verymmog.network.DataPlayer;

import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;

public interface PositionManagerInterface {
    public void updatePosition(SocketChannel channel, long x, long y, long dx, long dy);

    public void forget(SocketChannel channel);

    public SortedSet<DataPlayer> all();
}
