/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.verymmog.demo.gui;

import com.verymmog.demo.Data;
import com.verymmog.demo.model.map.Map;
import com.verymmog.model.PlayerInterface;
import com.verymmog.model.map.SquareObstacleInterface;

import javax.swing.*;
import java.awt.*;

/**
 * @author marion : mariondalle@outlook.com
 */
public class View extends JPanel {

    private Data<Map> data;


    public View(Data<Map> data) {
        this.data = data;
    }


    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.gray);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.pink);
        g.fillOval(translateX(0),
                translateY(-1 * data.getMap().getRadius()),
                (int) (2 * data.getMap().getRadius()),
                (int) (2 * data.getMap().getRadius()));
        g.setColor(Color.gray);
        for (SquareObstacleInterface o : data.getMap().getObstacles()) {
            g.fillRect(translateX(o.getX()), translateY(o.getY()), (int) (o.getWidth()), (int) (o.getHeight()));
        }

        g.setColor(Color.red);
        for (PlayerInterface p : data.getPlayers()) {
//            System.out.println(p);
            g.fillRect(translateX(p.getX()), translateY(p.getY()), 5, 5);
        }

        g.fillOval(translateX(data.getMap().getRadius() - (0.1 * data.getMap().getRadius())),
                translateY(-0.1 * data.getMap().getRadius()),
                (int) (0.2 * data.getMap().getRadius()),
                (int) (0.2 * data.getMap().getRadius()));

        //le joueur
        g.setColor(Color.black);
        g.fillRect(translateX(data.getPlayer().getX()), translateY(data.getPlayer().getY()), 5, 5);

        g.setColor(Color.gray);
        g.fillRect(translateX(0),
                translateY(-1 * data.getMap().getRadius()),
                (int) (2 * data.getMap().getRadius()),
                (int) (data.getMap().getRadius()));
    }

    private int translateX(double x) {
        return (int) (x + this.getWidth() / 2 - data.getPlayer().getX());
    }

    private int translateY(double y) {
        return (int) (y + this.getHeight() / 2 - data.getPlayer().getY());
    }
}
