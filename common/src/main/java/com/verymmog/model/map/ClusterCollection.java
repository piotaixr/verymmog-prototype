package com.verymmog.model.map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClusterCollection implements Serializable {
    Multimap<Integer, ClusterInterface> clusters = ArrayListMultimap.create();
    Map<Integer, ClusterInterface> clustersById = new HashMap<>();

    @Override
    public String toString() {
        return "ClusterCollection{" + "clusters=" + clusters + '}';
    }

    
    public ImmutableList<ClusterInterface> getClustersAtLevel(int level) {
        return ImmutableList.copyOf(clusters.get(level));
    }

    public int getMaxClusterLevel() {
        return Ordering.natural().max(clusters.keySet());
    }

    public int size(){
        return clusters.size();
    }
    
    public void add(int level, ClusterInterface cluster) {
        if (!clustersById.containsKey(cluster.getId())) {
            clustersById.put(cluster.getId(), cluster);
            clusters.put(level, cluster);
        }
    }

    public Collection<ClusterInterface> all() {
        return clustersById.values();
    }

    public ClusterInterface getCluster(int clusterId) {
        return clustersById.get(clusterId);
    }
}
