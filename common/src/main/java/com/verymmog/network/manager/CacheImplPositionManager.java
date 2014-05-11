package com.verymmog.network.manager;

import com.google.common.cache.*;
import com.verymmog.network.DataPlayer;
import org.apache.log4j.Logger;

import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;


public class CacheImplPositionManager implements PositionManagerInterface, RemovalListener<SocketChannel, DataPlayer> {

    private Logger logger = Logger.getLogger(getClass());
    private LoadingCache<SocketChannel, DataPlayer> data;
    private SortedSet<DataPlayer> sorted;

    public CacheImplPositionManager(CacheLoader<SocketChannel, DataPlayer> loader,
                                    DataPlayerComparatorInterface dataPlayerComparator) {
        sorted = Collections.synchronizedSortedSet(new TreeSet<>(dataPlayerComparator));
        data = CacheBuilder.newBuilder()
                           .expireAfterAccess(5, TimeUnit.SECONDS)
                           .removalListener(this)
                           .build(loader);
    }

    @Override
    public void onRemoval(RemovalNotification<SocketChannel, DataPlayer> socketChannelDataPlayerRemovalNotification) {
        sorted.remove(socketChannelDataPlayerRemovalNotification.getValue());
    }

    @Override
    public void updatePosition(SocketChannel channel, long x, long y, long dx, long dy) {
        DataPlayer pl = data.getUnchecked(channel);
        if (pl == null) {
            System.out.println("Erreur, pas prévu de passer la, le cache devrait pas retourner null");
        } else {
            logger.trace(String.format("Updating position of %s, (%d, %d, %d, %d)", pl.name, pl.x, pl.y, pl.dx, pl.dy));
            pl.update(x, y, dx, dy);
            synchronized (sorted) {
                sorted.remove(pl);
                sorted.add(pl);
            }
        }
    }

    @Override
    public void forget(SocketChannel channel) {
        DataPlayer pl = data.getUnchecked(channel);
        if (pl != null) {
            sorted.remove(pl);
            data.invalidate(channel);
            logger.debug("Oubli de la position de " + pl.name);
        }
    }

    @Override
    public SortedSet<DataPlayer> all() {
        return sorted;
    }
}
