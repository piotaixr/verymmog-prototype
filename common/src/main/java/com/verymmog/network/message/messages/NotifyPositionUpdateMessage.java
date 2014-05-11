package com.verymmog.network.message.messages;

import com.verymmog.model.PlayerInterface;

public class NotifyPositionUpdateMessage implements MessageInterface {

    private long x, y, dx, dy;

    public NotifyPositionUpdateMessage(PlayerInterface player) {
        this(player.getX(), player.getY(), player.getDx(), player.getDy());
    }

    public NotifyPositionUpdateMessage(long x, long y, long dx, long dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    public long getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    public long getDx() {
        return dx;
    }

    public long getDy() {
        return dy;
    }
}
