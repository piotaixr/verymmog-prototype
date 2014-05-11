package com.verymmog.nioengine;

import com.verymmog.nioengine.event.EventDispatcherInterface;
import com.verymmog.nioengine.extension.EngineExtensionInterface;
import com.verymmog.nioengine.output.OutputInterface;

import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

public interface EngineInterface {
    /**
     * Register the channel 'channel' for the operations 'operations'.
     *
     * @param channel    The channel to register
     * @param operations The interested operations
     * @see java.nio.channels.SelectionKey for the available operations.
     */
    void registerChannel(SelectableChannel channel, int operations);

    /**
     * Asynchronously connect to the address 'address' and returns the created Socket.
     * There is no guarantee that the returned socket is already connected.
     *
     * @param address The address to connect to
     * @return The created SocketChannel
     */
    SocketChannel connect(InetSocketAddress address);

    /**
     * Asynchronously send the data 'data' to the channel 'channel'.
     *
     * @param channel The destination channel
     * @param data    The data to send
     */
    void send(SelectableChannel channel, byte[] data);

    /**
     * Changes the interest for the given channel and operations.
     *
     * @param channel    The channel for which the interests will be changed
     * @param operations The operations that will be changed
     * @param value      A boolean value representing wether the interest must be set or unset
     * @throws com.verymmog.nioengine.exception.ChannelNotRegisteredException If the channel is not registered in the engine.
     * @see java.nio.channels.SelectionKey for the available operations.
     */
    void setInterest(SelectableChannel channel, int operations, boolean value);

    /**
     * @return The EventDispatcher associated with this engine.
     */
    EventDispatcherInterface getEventDispatcher();

    /**
     * @return All the channels managed by the engine.
     */
    Set<SelectableChannel> getChannels();

    /**
     * Starts (bootstrap) the Engine in a new Thread.
     * This method is supposed to be non-blocking
     */
    void start();

    /**
     * Marks the engine for termination.
     */
    void terminate();

    /**
     * @return The engine's Thread
     */
    Thread getThread();

    /**
     * @return true If the engine is started
     */
    boolean isRunning();

    /**
     * Add an extension to the engine.
     * If the engine is already running, also starts the extension.
     *
     * @param extension The extension to add
     */
    public void addExtension(EngineExtensionInterface extension);

    /**
     * Removes an extension currently in the engine.
     * If the engine is running, the extension is first shut-down.
     *
     * @param extension The extension to remove
     */
    public void removeExtension(EngineExtensionInterface extension);

    /**
     * Returns an ouput for the given channel using the engine.
     *
     * @param channel The channel wich will be used to send the data that will be send to the created output
     * @return The created output
     */
    public OutputInterface<byte[]> createOutput(SocketChannel channel);
}
