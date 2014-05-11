package com.verymmog.nioengine.processor;

import com.verymmog.nioengine.BufferStatus;
import com.verymmog.nioengine.NioEngine;
import com.verymmog.nioengine.RecvStatus;
import com.verymmog.nioengine.event.EventProvider;
import com.verymmog.nioengine.event.events.*;
import com.verymmog.nioengine.event.events.data.*;
import com.verymmog.nioengine.exception.SelectableChannelException;
import com.verymmog.util.ByteBufferUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Basic implementation of KeyProcessorInterface
 */
public class KeyProcessor implements KeyProcessorInterface {
    /**
     * The engine
     */
    private NioEngine engine;

    private Logger logger;
    /**
     * Set of buffers for the input data
     * There is an input buffer for a channel only if there is a data tranfert.
     */
    private Map<SelectableChannel, BufferStatus> inBuffers = new HashMap<>();
    /**
     * Set of buffers for sending data.
     * There is a buffer in this collection only if there is data to send.
     */
    private Map<SelectableChannel, ByteBuffer> outBuffers = new HashMap<>();

    public KeyProcessor() {
        logger = Logger.getLogger(this.getClass());
    }

    protected Logger getLogger() {
        return logger;
    }

    public void setEngine(NioEngine engine) {
        this.engine = engine;
    }

    @Override
    public void processKeys(Set<SelectionKey> selectionKeys) {
        try {
            Iterator<SelectionKey> it = selectionKeys.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();

                processKey(key);
            }
        } catch (SelectableChannelException scex) {
            try (SelectableChannel channel = scex.getChannel()) {
                System.err.println("Erreur avec un channel, on le ferme");

                fireDisconnect(channel);
                System.out.println("Deconnexion");

                engine.getChannels().remove(channel);

                channel.close();
            } catch (IOException ex1) {
                ex1.printStackTrace();
            }
        }
    }

    /**
     * Process a SelectionKey
     *
     * @param key The key to process
     */
    protected void processKey(SelectionKey key) {
        if (key.isValid()) {
            if (key.isAcceptable()) {
                getLogger().trace("Accept");
                handleAccept(key);
            }
            if (key.isConnectable()) {
                handleConnect(key);
                getLogger().trace("Connect");
            }
            if (key.isReadable()) {
                handleDataIn(key);
                getLogger().trace("Read");
            }
            if (key.isWritable()) {
                handleDataOut(key);
                getLogger().trace("Write");
            }

        }
    }

    /**
     * Called if there is data to be read for the given selection key.
     * Creates an input buffer if none associated to the key's channel and read the available data.
     *
     * @param key The key for which there is data to be read
     */
    protected void handleDataIn(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        BufferStatus bs = inBuffers.get(channel);
        if (bs == null) {
            bs = new BufferStatus();
            inBuffers.put(channel, bs);
        }

        handleRead(channel, bs);
    }

    /**
     * Reads data from the given channel to the given buffer
     *
     * @param channel The channel to read from
     * @param bs The buffer to write into
     * @return The number of bytes read
     */
    protected int handleRead(SocketChannel channel, BufferStatus bs) {
        int read;
//        System.out.println("HandleIn");
        try {
            read = channel.read(bs.buffer);
        } catch (IOException e) {
            throw new SelectableChannelException(channel);
        }

        if (read == -1) {
            throw new SelectableChannelException(channel);
        }

        if (bs.buffer.remaining() == 0) {
            if (bs.status == RecvStatus.SIZE) {
                int size = bs.buffer.getInt(0);
                if (size <= 0) {
                    inBuffers.remove(channel);
                } else {
                    try {
                        bs.buffer = ByteBuffer.allocate(size);
                    } catch (Exception e) {
                        System.out.println(size);
                    }
                    bs.status = RecvStatus.DATA;
                }
            } else {
                fireReceive(channel, bs.buffer.array());

                inBuffers.remove(channel);
            }
        }

        return read;
    }

    /**
     * Called if there is data to be written for the given SelectionKey
     *
     * Gets (create and fill if necessary) the buffer
     *
     * @param key
     */
    protected void handleDataOut(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer outBuffer = getOutBuffer(channel);

        if (outBuffer == null || channel.isConnectionPending()) {
            engine.setInterest(channel, SelectionKey.OP_WRITE, false);
        } else {
            handleWrite(channel, outBuffer);
        }
    }

    protected int handleWrite(SocketChannel channel, ByteBuffer outBuffer) {
        int written = 0;
        try {
            written = channel.write(outBuffer);
        } catch (IOException e) {
            throw new SelectableChannelException(channel);
        }

        if (outBuffer.remaining() == 0) {
            outBuffers.remove(channel);
        }

        return written;
    }

    protected void handleConnect(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();

        try {
            channel.finishConnect();
        } catch (IOException e) {
            System.out.println("Erreur FINISHCONNECT");
            throw new SelectableChannelException(channel);
        }

        fireConnected(channel);
        System.out.println("HandleConnect");
        engine.setInterest(channel, SelectionKey.OP_CONNECT, false);
        engine.setInterest(channel, SelectionKey.OP_READ, true);

        if (outBuffers.get(channel) != null) {
            engine.setInterest(channel, SelectionKey.OP_WRITE, true);
        }
    }

    protected void handleAccept(SelectionKey key) {
        System.out.println("Accept");
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        SocketChannel newChannel;

        try {
            newChannel = channel.accept();
            if (newChannel == null) {
                return;
            }
        } catch (IOException e) {
            throw new SelectableChannelException(channel);
        }

        try {
            newChannel.configureBlocking(false);
        } catch (IOException e) {
            throw new SelectableChannelException(newChannel);
        }

        engine.registerChannel(newChannel, SelectionKey.OP_READ);

        fireNewConnection(channel, newChannel);
    }

    protected ByteBuffer getOutBuffer(SocketChannel channel) {
        ByteBuffer outBuffer = outBuffers.get(channel);

        if (outBuffer == null) {
            byte[] data = engine.getDataProvider().nextData(channel);

            if (data != null) {
                outBuffer = ByteBufferUtil.toByteBuffer(data);
            }
        }

        return outBuffer;
    }

    protected void fireDisconnect(SelectableChannel channel) {
        engine.getEventDispatcher().dispatch(
                EventProvider.provider().get(DisconnectEvent.class),
                new DisconnectEventData(engine, channel)
        );
    }

    protected void fireReceive(SocketChannel channel, byte[] data) {
        Socket s = channel.socket();
        logger.trace("Message recu de " + s.getInetAddress() + ":" + s.getPort() + " --> " + s.getLocalAddress() + ":" + s
                .getLocalPort());

        engine.getEventDispatcher().dispatch(
                EventProvider.provider().get(ReceiveEvent.class),
                new ReceiveEventData(engine, channel, data)
        );
    }

    protected void fireWriteReady(SocketChannel channel) {
        engine.getEventDispatcher().dispatch(
                EventProvider.provider().get(WriteReadyEvent.class),
                new WriteReadyEventData(engine, channel)
        );
    }

    protected void fireNewConnection(ServerSocketChannel channel, SocketChannel newChannel) {
        engine.getEventDispatcher().dispatch(
                EventProvider.provider().get(NewConnectionEvent.class),
                new NewConnectionEventData(engine, channel, newChannel)
        );
    }

    protected void fireConnected(SocketChannel channel) {
        engine.getEventDispatcher().dispatch(
                EventProvider.provider().get(ConnectionAccepted.class),
                new ConnectionAcceptedEventData(engine, channel)
        );
    }

}
