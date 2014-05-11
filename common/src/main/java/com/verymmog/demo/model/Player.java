/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.verymmog.demo.model;

import com.verymmog.model.PlayerInterface;
import com.verymmog.model.map.SquareObstacleInterface;

/**
 * @author marion : mariondalle@outlook.com
 */
public class Player implements PlayerInterface {

    private long x;
    private long y;
    private long dx;
    private long dy;
    private int remainingTimeStunned;
    private String name;

    public Player(long x, long y, String name) {
        this.x = x;
        this.y = y;
        this.dx = 0;
        this.dy = 0;
        this.name = name;
        this.remainingTimeStunned = 0;
    }

    @Deprecated
    public boolean inObstacle(SquareObstacleInterface obstacle) {
        return ((obstacle.getX() <= x) && ((obstacle.getX() + obstacle.getWidth()) >= x) && (obstacle.getY() <= y) && ((obstacle
                .getY() + obstacle.getHeight()) >= y));
    }

    public long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public long getY() {
        return y;
    }

    public void setY(long y) {
        this.y = y;
    }

    public long getDx() {
        return dx;
    }

    public void setDx(long dx) {
        this.dx = dx;
    }

    public long getDy() {
        return dy;
    }

    public void setDy(long dy) {
        this.dy = dy;
    }

    public String getName() {
        return name;
    }

    @Override
    public PlayerInterface updatePosition(long x, long y) {
        setX(x);
        setY(y);

        return this;
    }

    @Override
    public void incrementSpeedVector(long dx, long dy) {
        setSpeedVector(getDx() + dx, getDy() + dy);
    }

    @Override
    public void setSpeedVector(long dx, long dy) {
        setDx(dx);
        setDy(dy);
    }

    @Override
    @Deprecated
    public boolean isStunned() {
        return remainingTimeStunned == 0;
    }

    @Deprecated
    @Override
    public int getRemainingTimeStunned() {
        return remainingTimeStunned;
    }

    @Override
    @Deprecated
    public void setRemainingTimeStunned(int remainingTimeStunned) {
        this.remainingTimeStunned = remainingTimeStunned;
    }

}
