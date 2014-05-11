package com.verymmog.network.manager.clusterselection;

import com.verymmog.model.map.ClusterInterface;

import java.util.List;

public class RandomSelectionStrategy implements ClusterSelectionStrategy {

    @Override
    public ClusterInterface select(List<ClusterInterface> clusters) {
        return clusters.get((int) (Math.random() * clusters.size()));
    }
}
