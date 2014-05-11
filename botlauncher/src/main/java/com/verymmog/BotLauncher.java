package com.verymmog;

import com.verymmog.gui.ConnectionFrame;
import com.verymmog.nioengine.EngineInterface;
import com.verymmog.nioengine.NioEngine;
import com.verymmog.nioengine.event.MultithreadedEventDispatcher;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.List;

public class BotLauncher {

    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.DEBUG);

        final NioEngine engine = new NioEngine(SelectorProvider.provider().openSelector());
        engine.setEventDispatcher(new MultithreadedEventDispatcher(8));

        engine.start();

        ConnectionFrame cf = new ConnectionFrame("Connexion au manager", engine) {
            @Override
            protected void onConnectionSuccess(SocketChannel channel) {

            }

            @Override
            protected void tryConnection(String text) {
                createBots(engine, toAddress(text));
            }
        };

    }

    public static void createBots(EngineInterface engine, InetSocketAddress managerAddress) {
        int nbBots = 100;
        System.out.println("Creating bots");
        List<Bot> bots = new ArrayList<>();
        for (int i = 0; i < nbBots; i++) {
            Bot b = new Bot(i, engine, managerAddress);
            b.run();
        }


    }

}
