package com.verymmog.network.message.messages;

public class UpdatePositionMessage implements MessageInterface {

    private long x;
    private long y;
    private long dx;
    private long dy;

    public UpdatePositionMessage(long x, long y, long dx, long dy) {
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UpdatePositionMessage) {
            UpdatePositionMessage o = (UpdatePositionMessage) obj;

            return getX() == o.getX()
                    && getY() == o.getY()
                    && getDx() == o.getDx()
                    && getDy() == o.getDy();
        } else {
            return false;
        }
    }
}
