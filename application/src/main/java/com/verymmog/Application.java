package com.verymmog;

import com.verymmog.demo.Data;
import com.verymmog.demo.MainLoop;
import com.verymmog.demo.gui.Window;
import com.verymmog.demo.model.Player;
import com.verymmog.demo.model.map.Map;
import com.verymmog.gui.ConnectionFrame;
import com.verymmog.gui.IdentificationFrame;
import com.verymmog.model.PlayerInterface;
import com.verymmog.model.map.MapInterface;
import com.verymmog.network.player.PlayerNetwork;
import com.verymmog.network.player.identify.ManagerConnectionExtension;
import com.verymmog.nioengine.NioEngine;
import com.verymmog.nioengine.event.MultithreadedEventDispatcher;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Date;
import java.util.Timer;

public class Application implements Runnable {

    private NioEngine engine;
    private MapInterface map;

    @Override
    public void run() {
        try {
            engine = new NioEngine(SelectorProvider.provider().openSelector());
            engine.setEventDispatcher(new MultithreadedEventDispatcher(Runtime.getRuntime().availableProcessors()));

            engine.start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erreur d'initialisation", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                showConnectionFrame();
            }
        });
    }

    private void showConnectionFrame() {
        ConnectionFrame cf = new ConnectionFrame("Connexion", engine) {

            @Override
            protected void onConnectionSuccess(final SocketChannel channel) {
                final ConnectionFrame that = this;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        showIdentificationFrame(that, channel);
                    }
                });
            }
        };
    }

    private void showIdentificationFrame(ConnectionFrame cf, final SocketChannel managerChannel) {
        IdentificationFrame idframe = new IdentificationFrame("Identification") {

            @Override
            protected void onConnect(final String login, final String password) {
                final IdentificationFrame that = this;
                engine.addExtension(new ManagerConnectionExtension(managerChannel, login, password) {

                    @Override
                    protected void onError() {
                        System.out.println("Erreur connexion manager");
                    }

                    @Override
                    protected void onReceiveMap(MapInterface map) {
                        setMap(map);
                    }

                    @Override
                    protected void onInitialLeader(SocketChannel leaderChannel, String token) {
                        initializeGameGui(that, leaderChannel, login, token);
                    }
                });
            }
        };
        idframe.setLocationRelativeTo(cf);
    }

    private void initializeGameGui(final IdentificationFrame that,
                                   final SocketChannel leaderChannel,
                                   String login,
                                   final String token) {
        final PlayerInterface player = new Player(0, 0, login);
        Data data = new Data(map, player);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //TODO changer, le cast est pas bon
                Data<Map> data = new Data<>((Map) map, player);
                Window win = new Window(data);
                MainLoop ml = new MainLoop(data, win.getView(), win.getMiniMap(), win.getRanking());
                Timer timer = new Timer();

                timer.scheduleAtFixedRate(ml, new Date(), 10);

                PlayerNetwork pnet = new PlayerNetwork(leaderChannel, data, player, token);

                engine.addExtension(pnet);

                that.dispose();
            }
        });
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.DEBUG);

        Application app = new Application();
        app.run();
    }

    public void setMap(MapInterface map) {
        this.map = map;
    }
}
