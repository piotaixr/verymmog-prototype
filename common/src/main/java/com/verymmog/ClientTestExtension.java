package com.verymmog;

import com.verymmog.nioengine.event.EventDispatcherInterface;
import com.verymmog.nioengine.event.EventListener;
import com.verymmog.nioengine.event.EventProvider;
import com.verymmog.nioengine.event.events.ReceiveEvent;
import com.verymmog.nioengine.event.events.data.ReceiveEventData;
import com.verymmog.nioengine.extension.BaseEngineExtension;
import com.verymmog.util.Pair;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientTestExtension extends BaseEngineExtension {

    private SocketChannel channel;
    private AtomicInteger finished;
    private int total;
    private Map<SelectableChannel, Pair<Integer, ArrayList<Integer>>> reception;
    private int numclients;
    private long start;
    private int num;
    private int id;
    private Thread thread;

    public ClientTestExtension(int id, SocketChannel channel, AtomicInteger finished, int num, int total, Map<SelectableChannel, Pair<Integer, ArrayList<Integer>>> reception, int numclients, long start) {
        this.channel = channel;
        this.finished = finished;
        this.total = total;
        this.reception = reception;
        this.numclients = numclients;
        this.start = start;
        this.num = num;
        this.id = id;
    }

    EventListener<ReceiveEventData> receiveListener = new EventListener<ReceiveEventData>() {
        @Override
        public void listen(ReceiveEventData event) {
            Pair<Integer, ArrayList<Integer>> res = reception.get(event.getChannel());
            res.first++;
            reception.put(event.getChannel(), res);

            // check message
            String message = new String(event.getData());
            String[] splitted = message.split(" ");
            int numsender = Integer.parseInt(splitted[1]);
            int nummessage = Integer.parseInt(splitted[2]);

            if (nummessage != res.second.get(numsender)) {
                System.err.println("Erreur avec " + numsender + ", attendu: " + res.second.get(numsender) + ", recu: " + nummessage);
                synchronized (this) {
                    try {
                        wait(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            res.second.set(numsender, res.second.get(numsender) + 1);

            if (res.first == total) {
                int f = finished.incrementAndGet();
                System.out.println(f + " / " + numclients);
                if (numclients == f) {
                    System.out.println("FINI");
                    System.out.println("Total = " + (System.currentTimeMillis() - start));
                    System.exit(0);
                }
            }
        }
    };

    @Override
    protected void doBoot() {
        super.doBoot();

//        getEngine().registerChannel(channel, SelectionKey.OP_READ);
        System.out.println("BOOT");
        thread = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < num; i++) {
                    String text = "TextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextTextText " + id;
                    getEngine().send(channel, (text + " " + i).getBytes());
                }
                System.out.println("Done ClientTest " + id);
            }
        };
        thread.start();
    }

    @Override
    public void register(EventDispatcherInterface dispatcher) {
        super.register(dispatcher);

        dispatcher
                .register(channel, EventProvider.provider().get(ReceiveEvent.class), receiveListener)
        ;
    }

    @Override
    public void unregister(EventDispatcherInterface dispatcher) {
        super.unregister(dispatcher);

        dispatcher.unregister(receiveListener);
    }
}
