package com.verymmog;

import com.verymmog.demo.model.map.Map;
import com.verymmog.model.map.BasicCluster;
import com.verymmog.model.map.ClusterCollection;
import com.verymmog.model.map.MapInterface;
import com.verymmog.network.leader.LeaderNetwork;
import com.verymmog.network.manager.LeaderAddressManagerInterface;
import com.verymmog.network.manager.ManagerExtension;
import com.verymmog.network.manager.StaticLeaderAddressManager;
import com.verymmog.network.manager.clusterselection.ClusterSelectionStrategy;
import com.verymmog.network.manager.clusterselection.RandomSelectionStrategy;
import com.verymmog.network.manager.user.PlainTextAuthenticator;
import com.verymmog.network.manager.user.PlainTextTokenManager;
import com.verymmog.network.manager.user.TokenManager;
import com.verymmog.network.manager.user.UserAuthenticator;
import com.verymmog.network.manager.user.provider.BotUserProvider;
import com.verymmog.network.manager.user.provider.TextFileUserProvider;
import com.verymmog.network.manager.user.provider.UserProviderChain;
import com.verymmog.network.manager.user.provider.UserProviderInterface;
import com.verymmog.nioengine.NioEngine;
import com.verymmog.nioengine.event.MultithreadedEventDispatcher;
import com.verymmog.nioengine.extension.TryConnectionExtension;
import com.verymmog.nioengine.extension.TryConnectionListener;
import com.verymmog.util.Ports;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Scanner;

public class Manager {
    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.DEBUG);

        final NioEngine engine = new NioEngine(SelectorProvider.provider().openSelector());
        engine.setEventDispatcher(new MultithreadedEventDispatcher(10));

        engine.start();


        Scanner sc = new Scanner(System.in);
        String localAddress = "127.0.0.1";
        do {
            System.out.print("Quelle addresse utiliser pour le Manager? (127.0.0.1) \n --> ");
            String line = sc.nextLine();

            if (!line.trim().equals("")) {
                localAddress = line;
            }

        } while (!localAddress.matches("^\\d{1,3}(\\.\\d{1,3}){3}$"));

        ServerSocketChannel sscPlayers = ServerSocketChannel.open()
                                                            .bind(new InetSocketAddress(localAddress,
                                                                    Ports.MANAGER_CONNECT_PLAYERS));
        sscPlayers.configureBlocking(false);

        ServerSocketChannel sscLeaders = ServerSocketChannel.open()
                                                            .bind(new InetSocketAddress(localAddress,
                                                                    Ports.MANAGER_CONNECT_LEADERS));
        sscLeaders.configureBlocking(false);

        UserProviderInterface userProvider = new UserProviderChain(
                new TextFileUserProvider(new File("./users.txt")),
                new BotUserProvider()
        );

        final TokenManager tokenManager = new PlainTextTokenManager();
        UserAuthenticator userAuthenticator = new PlainTextAuthenticator();
        //TODO changer

        ClusterCollection cc = new ClusterCollection();
        for (int i = 0; i < 10; i++) {
            cc.add(1, new BasicCluster(i));
        }

        final MapInterface map = Map.generate(cc, 10000);

        LeaderAddressManagerInterface leaderAddressResolver = new StaticLeaderAddressManager(new InetSocketAddress(
                localAddress,
                Ports.LEADER_CONNECT_PLAYERS));
        ClusterSelectionStrategy clusterSelectionStrategy = new RandomSelectionStrategy();

        ManagerExtension mext = new ManagerExtension(sscPlayers,
                sscLeaders,
                userProvider,
                tokenManager,
                userAuthenticator,
                map,
                leaderAddressResolver,
                clusterSelectionStrategy);

        engine.addExtension(mext);

        TryConnectionExtension ext = new TryConnectionExtension(localAddress, Ports.MANAGER_CONNECT_LEADERS);

        ext.addListener(new TryConnectionListener() {
            @Override
            public void onSuccess(SocketChannel channel) {
                super.onSuccess(channel);
                Logger.getLogger(TryConnectionExtension.class).trace("Connexion success " + channel.toString());

                LeaderNetwork ln = new LeaderNetwork(channel,
                        "test",
                        "stubToken",
                        map,
                        map.getClusters().all().iterator().next(),
                        tokenManager);

                engine.addExtension(ln);
            }

            @Override
            public void onError() {
                super.onError();
                System.out.println("Erreur connexion au manager");
            }
        });

        engine.addExtension(ext);
    }
}
