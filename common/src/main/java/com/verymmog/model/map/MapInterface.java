package com.verymmog.model.map;

import java.io.Serializable;
import java.util.List;

public interface MapInterface<ObstacleClass extends ObstacleInterface> extends Serializable {
    public ClusterCollection getClusters();

    public long getWidth();

    public long getHeight();

    @Deprecated
    public long getRadius();

    public List<ObstacleClass> getObstacles();
}
