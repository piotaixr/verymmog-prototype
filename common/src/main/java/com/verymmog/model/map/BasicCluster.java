package com.verymmog.model.map;

public class BasicCluster implements ClusterInterface {
    private int id;

    public BasicCluster(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
