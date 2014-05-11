package com.verymmog.model;

import com.verymmog.model.map.SquareObstacleInterface;

public interface PlayerInterface {
    public String getName();

    public void incrementSpeedVector(long x, long y);

    public void setSpeedVector(long x, long y);

    public long getX();

    public long getY();

    public long getDx();

    public long getDy();

    public PlayerInterface updatePosition(long x, long y);

    @Deprecated
    public int getRemainingTimeStunned();

    @Deprecated
    public void setRemainingTimeStunned(int remainingTimeStunned);

    @Deprecated
    public boolean isStunned();

    @Deprecated
    public boolean inObstacle(SquareObstacleInterface obstacle);
}
