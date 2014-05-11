package com.verymmog;

import com.verymmog.demo.ComputeMainLoop;
import com.verymmog.demo.Data;
import com.verymmog.demo.model.map.Map;
import com.verymmog.demo.model.Player;
import com.verymmog.model.PlayerInterface;
import com.verymmog.model.map.MapInterface;
import com.verymmog.network.player.PlayerNetwork;
import com.verymmog.network.player.identify.ManagerConnectionExtension;
import com.verymmog.nioengine.EngineInterface;
import com.verymmog.nioengine.extension.TryConnectionExtension;
import com.verymmog.nioengine.extension.TryConnectionListener;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Bot implements Runnable {

    private EngineInterface engine;
    private InetSocketAddress managerAddress;
    private String login;
    private Map map;
    private Timer moveTimer = new Timer();

    public Bot(int id, EngineInterface engine, InetSocketAddress managerAddress) {
        this.engine = engine;
        this.managerAddress = managerAddress;
        this.login = "Bot" + id;
    }

    @Override
    public void run() {
        TryConnectionExtension ext = new TryConnectionExtension(managerAddress);
        ext.addListener(new TryConnectionListener() {
            @Override
            public void onError() {
                System.out.println("Erreur connexion manager");
            }

            @Override
            public void onSuccess(SocketChannel channel) {
                engine.addExtension(new ManagerConnectionExtension(channel, login, "password") {
                    @Override
                    protected void onError() {
                        System.out.println("Erreur identification auprès du manager");
                    }

                    @Override
                    protected void onReceiveMap(MapInterface map) {
                        Bot.this.map = (Map) map;
                    }

                    @Override
                    protected void onInitialLeader(SocketChannel leaderChannel, String token) {
                        launchPlayerExtension(leaderChannel, token);

                    }
                });
            }
        });

        engine.addExtension(ext);
    }

    private void launchPlayerExtension(SocketChannel leaderChannel, String token) {

        final PlayerInterface player = new Player(0, 0, login);
        //TODO changer, le cast est pas bon
        Data<Map> data = new Data<>((Map) map, player);

        ComputeMainLoop ml = new ComputeMainLoop(data);
        java.util.Timer timer = new java.util.Timer();

        timer.scheduleAtFixedRate(ml, new Date(), 10);

        PlayerNetwork pnet = new PlayerNetwork(leaderChannel, data, player, token);

        engine.addExtension(pnet);

        timer.scheduleAtFixedRate(new TimerTask() {
            private BotDirection direction = BotDirection.BOT;

            @Override
            public void run() {
                switch (direction) {
                    case TOP:
                        player.setSpeedVector(0, -1);
                        break;
                    case RIGHT:
                        player.setSpeedVector(1, 0);
                        break;
                    case BOT:
                        player.setSpeedVector(0, 1);
                        break;
                    case LEFT:
                        player.setSpeedVector(-1, 0);
                        break;
                }
                direction = BotDirection.values()[(int) (Math.random() * BotDirection.values().length)];

            }
        }, new Date(), 3000);
    }

    enum BotDirection {
        TOP, RIGHT, BOT, LEFT
    }
}
