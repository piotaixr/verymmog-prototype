package com.verymmog.model.map;

import java.util.List;

public class BaseMap<ObstacleClass extends ObstacleInterface> implements MapInterface<ObstacleClass> {

    private ClusterCollection clusters = new ClusterCollection();
    private List<ObstacleClass> obstacles;
    private long width, height;

    public BaseMap(ClusterCollection clusters, List<ObstacleClass> obstacles, long width, long height) {
        this.clusters = clusters;
        this.obstacles = obstacles;
        this.width = width;
        this.height = height;
    }

    @Override
    public List<ObstacleClass> getObstacles() {
        return obstacles;
    }

    @Override
    public long getWidth() {
        return width;
    }

    @Override
    public long getHeight() {
        return height;
    }

    @Override
    public long getRadius() {
        return getWidth() / 2;
    }

    @Override
    public ClusterCollection getClusters() {
        return clusters;
    }
}
