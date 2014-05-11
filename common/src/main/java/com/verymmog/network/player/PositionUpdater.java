package com.verymmog.network.player;

import com.verymmog.model.PlayerInterface;
import com.verymmog.nioengine.output.SerializableOutput;
import org.apache.log4j.Logger;

import java.util.Timer;

public class PositionUpdater {

    private Logger logger = Logger.getLogger(this.getClass());
    private UpdatePositionTask updater;
    private Timer t = new Timer("PositionUpdater");
    private long interval = 100;
    private SerializableOutput out;

    public PositionUpdater(SerializableOutput out) {
        this.out = out;
    }

    public void start(PlayerInterface player) {
        if (isRunning()) {
            throw new RuntimeException("Updater already running");
        }

        updater = new UpdatePositionTask(player, out);

        t.scheduleAtFixedRate(updater, 0, interval);

        logger.debug("PositionUpdater started for " + player.getName() + ", interval = " + interval);
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;

        if (isRunning()) {
            t.cancel();
            t.schedule(updater, 0, interval);
        }
    }

    public boolean isRunning() {
        return updater != null;
    }
}
