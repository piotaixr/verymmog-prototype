/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.verymmog.demo.gui;

import com.verymmog.demo.Data;
import com.verymmog.demo.model.map.Map;
import com.verymmog.model.map.SquareObstacleInterface;

import javax.swing.*;
import java.awt.*;

/**
 * @author marion : mariondalle@outlook.com
 */
public class MiniMap extends JPanel {

    private Data<Map> data;
    private View view;


    public MiniMap(Data<Map> data, View view) {
        this.data = data;
        this.view = view;
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.blue);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.pink);
        g.fillOval(
                0,
                scaleY(-1 * data.getMap().getRadius()),
                scaleX(2 * data.getMap().getRadius()),
                scaleY(2 * data.getMap().getRadius()));
        g.setColor(Color.blue);
        for (SquareObstacleInterface o : data.getMap().getObstacles()) {
            g.fillRect(scaleX(o.getX()), scaleY(o.getY()), scaleX(o.getWidth()), scaleY(o.getHeight()));
        }
        g.setColor(Color.red);
        g.fillOval(
                scaleX(data.getMap().getRadius() - 0.05 * data.getMap().getRadius()),
                scaleY(-0.05 * data.getMap().getRadius()),
                scaleX(0.1 * data.getMap().getRadius()),
                scaleY(0.1 * data.getMap().getRadius()));
        g.setColor(Color.black);
        g.drawRect(
                scaleX(data.getPlayer().getX() - view.getWidth() / 2),
                scaleY(data.getPlayer().getY() - view.getHeight() / 2),
                scaleX(view.getWidth()),
                scaleY(view.getHeight()));
    }

    private int scaleX(double x) {
        return (int) (x * this.getWidth() / (2 * data.getMap().getRadius()));
    }

    private int scaleY(double y) {
        return (int) (y * this.getHeight() / data.getMap().getRadius());
    }
}
