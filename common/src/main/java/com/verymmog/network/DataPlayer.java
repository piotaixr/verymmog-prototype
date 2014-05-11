package com.verymmog.network;

import java.io.Serializable;

public class DataPlayer implements Serializable {
    public final String name;
    public long x, y, dx, dy;

    public DataPlayer(String name, long x, long y, long dx, long dy) {
        this.name = name;
        update(x, y, dx, dy);
    }

    public void update(long x, long y, long dx, long dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }


}
