/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.verymmog.demo.model.map;

import com.verymmog.model.map.SquareObstacleInterface;

/**
 *
 * @author marion : mariondalle@outlook.com
 */
public class Obstacle implements SquareObstacleInterface {

    private long x;
    private long y;
    private long width;
    private long height;

    public Obstacle(long x, long y, long width, long height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Fuction to verify if an obstacle is in a area rectangular
     * @param a absissa minimum of the area
     * @param b ordered minimum of the area 
     * @param c absissa maximum of the area
     * @param d ordered maximum of the area
     * @return boolean
     */
    public boolean isIn(int a, int b, int c, int d) {
        boolean ba = (x > a) && (x < c) && (y > b) && (y < d);
        boolean bb = (x + width > a) && (x + width < c) && (y > b) && (y < d);
        boolean bc = (x > a) && (x < c) && (y + height > b) && (y + height > d);
        boolean bd = (x + width > a) && (x + width < c) && (y + height > b) && (y + height > d);
        return ba || bb || bc || bd;
    }

    @Override
    public long getX() {
        return x;
    }

    @Override
    public long getY() {
        return y;
    }

    @Override
    public long getWidth() {
        return width;
    }

    @Override
    public long getHeight() {
        return height;
    }
    
    
}
