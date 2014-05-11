package com.verymmog.network.message.messages;

public class SetBasePositionMessage implements MessageInterface {
    private long x, y;

    public SetBasePositionMessage(long x, long y) {
        this.x = x;
        this.y = y;
    }

    public long getX() {
        return x;
    }

    public long getY() {
        return y;
    }
}
