/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.verymmog.demo.model.map;

import com.verymmog.model.map.ClusterInterface;

/**
 * @author marion : mariondalle@outlook.com
 */
public class Cluster implements ClusterInterface {

    private int id;
    private long centerX;
    private long centerY;
    private long radius;

    public Cluster(int id, long centerX, long centerY, long radius) {
        this.id = id;
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

    @Override
    public int getId() {
        return 0;
    }
}
