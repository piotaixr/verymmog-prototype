package com.verymmog.network.manager.clusterselection;

import com.verymmog.model.map.ClusterInterface;

import java.util.List;

public interface ClusterSelectionStrategy {

    public ClusterInterface select(List<ClusterInterface> clusters);
}
