/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.verymmog.demo;

import com.verymmog.demo.gui.Window;
import com.verymmog.demo.model.map.Map;
import com.verymmog.demo.model.Player;
import com.verymmog.model.PlayerInterface;
import com.verymmog.model.map.BasicCluster;
import com.verymmog.model.map.ClusterCollection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

/**
 * @author marion
 */
public class Demonstrateur {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        long radius = 10000;
        int nb_cluster = 10;
//        EngineInterface engine = new BufferedEngine(SelectorProvider.provider().openSelector());
//        engine.start();

        PlayerInterface player = new Player(0, 0, "Marion");
        //Window win = new Window(me, null, radius);
        ClusterCollection clusterCollection = new ClusterCollection();
        for (int i = 0; i < nb_cluster; i++) {
            clusterCollection.add(1, new BasicCluster(i));
        }
        
        System.out.println(clusterCollection);
        System.out.println(clusterCollection.size());
        // On génère la map (j'ai bougé la generation de la carte qui etait dans le constructeur vers une methode statique)
        Map map = Map.generate(clusterCollection, radius);

        //Pour tester
        List<PlayerInterface> neighbours = new ArrayList<>();
        List<PlayerInterface> podium = new ArrayList<>();

        Data<Map> data = new Data<>(map, player, neighbours, podium);
        Window win = new Window(data);
        Timer timer = new Timer("main");
        timer.scheduleAtFixedRate(new MainLoop(data, win.getView(), win.getMiniMap(), win.getRanking()),
                new Date(),
                10);

    }

}
