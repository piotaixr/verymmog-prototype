/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.verymmog.demo.gui;

import com.verymmog.demo.Data;
import com.verymmog.demo.MainLoop;
import com.verymmog.demo.model.map.Map;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author marion
 */
public class Window extends JFrame{
    
    private View view; 
    private MiniMap miniMap; 
    private Ranking ranking;
    private JPanel side;
    private Data<Map> data;
    
    private MainLoop game;
    private KeyboardListener keyboardListener;
   
   
    public Window(Data<Map> data) {
        this.data = data;
        this.setTitle("Demonstration");
        this.setExtendedState(Window.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        
        side = new JPanel();
        side.setLayout(new BorderLayout());
        ranking = new Ranking(data);
        side.add(ranking, BorderLayout.CENTER);       
        view = new View(data);        
        miniMap = new MiniMap(data, view);
        side.add(miniMap, BorderLayout.SOUTH); 
        miniMap.setPreferredSize(new Dimension(side.getWidth(), 200));
        side.setPreferredSize(new Dimension(250, getHeight()));

        this.add(view, BorderLayout.CENTER);
        this.add(side, BorderLayout.EAST);
        this.setVisible(true);
        pack();
        
        
        this.keyboardListener = new KeyboardListener(data.getPlayer());
        this.addKeyListener(keyboardListener);
    }



    public View getView() {
        return view;
    }

    public MiniMap getMiniMap() {
        return miniMap;
    }

    public Ranking getRanking() {
        return ranking;
    }
    
    
    
}
