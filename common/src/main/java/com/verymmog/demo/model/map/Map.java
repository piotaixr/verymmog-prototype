/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.verymmog.demo.model.map;

import com.verymmog.model.map.BaseMap;
import com.verymmog.model.map.ClusterCollection;

import java.util.*;

/**
 * @author marion : mariondalle@outlook.com
 */
public class Map extends BaseMap<Obstacle> {

    //Choisis aléatoirment pour le moment 
    private long radius;
    @Deprecated
    private int nb_cluster;

    public Map(ClusterCollection clusters, List<Obstacle> obstacles, long radius) {
        super(clusters, obstacles, radius * 2, radius * 2);

        this.radius = radius;
        this.nb_cluster = clusters.size();
    }

    /**
     * Génère une map en positionnant aléatoirement les obstacles
     *
     * @param clusterCollection
     * @param radius
     * @return
     */
    public static Map generate(ClusterCollection clusterCollection, long radius) {
        int nb_cluster = clusterCollection.size();

        List<Obstacle> obstacles = new ArrayList<>();

        int area_obstacle = 0;
        while (area_obstacle <= 0.05 * Math.PI * Math.pow(radius, 2)) {
            int x;
            int y;
            do {
                x = (int) (Math.random() * 2 * radius);
                y = (int) (Math.random() * radius);
            } while ((Math.pow(x - radius, 2) + Math.pow(y, 2)) > Math.pow(radius, 2));
            int width = (int) (Math.random() * 500 + 100);
            int height = (int) (Math.random() * 500 + 100);
            area_obstacle = area_obstacle + width * height;
            Obstacle o = new Obstacle(x, y, width, height);
            obstacles.add(o);
        }

        return new Map(clusterCollection, obstacles, radius);
    }

}
