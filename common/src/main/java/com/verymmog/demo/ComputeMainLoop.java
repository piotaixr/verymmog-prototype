package com.verymmog.demo;

import com.verymmog.demo.model.map.Map;
import com.verymmog.demo.model.map.Obstacle;
import com.verymmog.model.PlayerInterface;

import java.util.TimerTask;

public class ComputeMainLoop extends TimerTask {

    private Data<Map> data;
    PlayerInterface player;

    public ComputeMainLoop(Data<Map> data) {
        this.data = data;
        this.player = data.getPlayer();
    }

    @Override
    public void run() {

        if (!player.isStunned()) {
            long oldx = player.getX();
            long oldy = player.getY();

            player.updatePosition(
                    player.getX() + player.getDx(),
                    player.getY() + player.getDy()
            );

            if (player.getY() < 0 || Math.pow(player.getX() - data.getMap().getRadius(), 2)
                    + Math.pow(player.getY(), 2) > Math.pow(data.getMap().getRadius(), 2)) {
                collision(oldx, oldy);
            }


            for (Obstacle o : data.getMap().getObstacles()) {
                if (this.player.inObstacle(o)) {
                    collision(oldx, oldy);
                    break;
                }
            }

            for (PlayerInterface p : data.getPlayers()) {
                p.updatePosition(
                        p.getX() + p.getDx(),
                        p.getY() + p.getDy()
                );
            }
        } else {
            player.setRemainingTimeStunned(player.getRemainingTimeStunned() - 1);
        }

    }

    private void collision(long oldx, long oldy) {
        this.player.setRemainingTimeStunned((int) (Math.abs(this.player.getDx()) + Math.abs(
                this.player.getDy())));
        this.player.setSpeedVector(0, 0);
        player.updatePosition(oldx, oldy);
    }
}
