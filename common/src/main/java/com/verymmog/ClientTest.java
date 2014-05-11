package com.verymmog;

import com.verymmog.nioengine.AsyncEngineBuffer;
import com.verymmog.nioengine.NioEngine;
import com.verymmog.nioengine.event.MultithreadedEventDispatcher;
import com.verymmog.nioengine.extension.TryConnectionExtension;
import com.verymmog.nioengine.extension.TryConnectionListener;
import com.verymmog.util.Pair;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientTest extends Thread {

    public static void main(String[] args) throws IOException {
        final int numclients = 30;
        final int numMessages = 1000;
        final int total = numclients * numMessages;
        final long start = System.currentTimeMillis();
        final AtomicInteger idGenerator = new AtomicInteger();

        final Map<SelectableChannel, Pair<Integer, ArrayList<Integer>>> reception = new HashMap<>();
        final AtomicInteger finished = new AtomicInteger(0);

        final NioEngine engine = new NioEngine(SelectorProvider.provider().openSelector(),
                new MultithreadedEventDispatcher(numclients),
                new AsyncEngineBuffer()
        );

        engine.start();

        for (int j = 0; j < numclients; j++) {
            TryConnectionExtension tce = new TryConnectionExtension("localhost", 20202);
            tce.addListener(new TryConnectionListener() {
                @Override
                public void onError() {
                    System.out.println("Erreur connexion");
                }

                @Override
                public void onSuccess(SocketChannel channel) {
                    System.out.println("Success");
                    ArrayList<Integer> countList = new ArrayList<>();
                    for (int z = 0; z < numclients; z++) {
                        countList.add(0);
                    }
                    reception.put(channel, new Pair<>(0, countList));

                    engine.addExtension(new ClientTestExtension(idGenerator.getAndIncrement(),
                            channel,
                            finished,
                            numMessages,
                            total,
                            reception,
                            numclients,
                            start));
                }
            });

            engine.addExtension(tce);
        }

        Timer t = new Timer("Print");
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                StringBuilder builder = new StringBuilder();
                for (Pair<Integer, ArrayList<Integer>> v : reception.values()) {
                    builder.append('|').append(v.first);
                }

                System.out.println(builder.toString());
            }
        }, new Date(), 1000);

    }
}
