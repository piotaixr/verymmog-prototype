package com.verymmog.nioengine;

import com.verymmog.nioengine.event.EventDispatcherInterface;
import com.verymmog.nioengine.exception.ChannelNotRegisteredException;
import com.verymmog.nioengine.extension.EngineExtensionInterface;
import com.verymmog.nioengine.output.ByteArrayOutput;
import com.verymmog.nioengine.output.OutputInterface;
import com.verymmog.nioengine.processor.KeyProcessor;
import com.verymmog.nioengine.processor.KeyProcessorInterface;
import com.verymmog.nioengine.register.BasicRegister;
import com.verymmog.nioengine.register.Register;
import com.verymmog.nioengine.register.RegisterConnect;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NioEngine extends BaseAsyncEngine {

    private KeyProcessorInterface keyProcessor;
    private Set<SelectableChannel> channels = Collections.synchronizedSet(new HashSet<SelectableChannel>());
    private final Set<EngineExtensionInterface> extensions = Collections.synchronizedSet(new HashSet<EngineExtensionInterface>());
    private Queue<Register> registers = new ConcurrentLinkedQueue<>();

    public NioEngine(Selector selector) {
        super(selector);
    }

    public NioEngine(Selector selector,
                     EventDispatcherInterface eventDispatcher,
                     DataProviderInterface dataProvider) {
        super(selector, eventDispatcher, dataProvider);
    }

    @Override
    public void addExtension(EngineExtensionInterface extension) {
        if (extensions.contains(extension)) {
            return;
        }

        extensions.add(extension);

        extension.setEngine(this);

        if (isRunning()) {
            extension.boot();
        }
    }

    @Override
    public void removeExtension(EngineExtensionInterface extension) {
        extensions.remove(extension);

        if (extension.isRunning()) {
            extension.shutdown();
        }

        extension.setEngine(null);
    }

    @Override
    public void registerChannel(SelectableChannel channel, int operations) {
        registers.offer(new BasicRegister(channel, operations));

        getSelector().wakeup();
    }

    @Override
    public SocketChannel connect(InetSocketAddress address) {
        SocketChannel channel = null;
        try {
            channel = SocketChannel.open();
            channel.configureBlocking(false);

            registers.offer(new RegisterConnect(channel, address));

            getSelector().wakeup();

            return channel;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void send(SelectableChannel channel, byte[] data) {
        if (!channels.contains(channel)) {
            throw new ChannelNotRegisteredException(channel);
        } else {
            super.send(channel, data);

            //TODO optimiser, inutile de le faire a chaque fois!
            setInterest(channel, SelectionKey.OP_WRITE, true);

            getSelector().wakeup();
        }
    }

    @Override
    public void setInterest(SelectableChannel channel, int operations, boolean value) {
        if (!channels.contains(channel)) {
            throw new ChannelNotRegisteredException(channel);
        } else {
            SelectionKey key = channel.keyFor(getSelector());

            if (value) {
                key.interestOps(key.interestOps() | operations);
            } else {
                key.interestOps(key.interestOps() & ~operations);
            }
        }
    }


    @Override
    public Set<SelectableChannel> getChannels() {
        return channels;
    }

    @Override
    public void start() {
        if (keyProcessor == null) {
            setKeyProcessor(new KeyProcessor());
        }

        super.start();
    }

    public void terminate() {
        super.terminate();

        synchronized (extensions) {
            for (EngineExtensionInterface e : extensions) {
                e.shutdown();
            }
        }
    }

    protected void boot() {
        super.boot();

        synchronized (extensions) {
            for (EngineExtensionInterface e : extensions) {
                e.boot();
            }
        }
    }

    protected void mainLoop() {
        try {
            Selector selector = getSelector();

            selector.select();

            while (!registers.isEmpty()) {
                Register r = registers.poll();

                if (r != null && !channels.contains(r.getChannel())) {
                    try {
                        channels.add(r.execute(selector));
                    } catch (ClosedChannelException e) {
                        e.printStackTrace();
                    }
                }
            }

            keyProcessor.processKeys(selector.selectedKeys());

        } catch (IOException ex) {
            System.err.println("Exception, engine terminating");
            terminate();
        }
    }


    @Override
    public OutputInterface<byte[]> createOutput(SocketChannel channel) {
        if (channels.contains(channel)) {
            return new ByteArrayOutput(this, channel);
        } else {
            return null;
        }
    }


    public void setKeyProcessor(KeyProcessorInterface keyProcessor) {
        this.keyProcessor = keyProcessor;
        keyProcessor.setEngine(this);
    }

    public KeyProcessorInterface getKeyProcessor() {
        return keyProcessor;
    }

}
