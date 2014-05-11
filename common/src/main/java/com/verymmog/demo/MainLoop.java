/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.verymmog.demo;

import com.verymmog.demo.gui.MiniMap;
import com.verymmog.demo.gui.Ranking;
import com.verymmog.demo.gui.View;
import com.verymmog.demo.model.map.Map;

import javax.swing.*;

/**
 * @author marion : mariondalle@outlook.com
 */
public class MainLoop extends ComputeMainLoop {

    private View view;
    private MiniMap miniMap;
    private Ranking ranking;


    public MainLoop(Data<Map> data, View view, MiniMap miniMap, Ranking ranking) {
        super(data);
        this.view = view;
        this.miniMap = miniMap;
        this.ranking = ranking;
    }

    @Override
    public void run() {
        super.run();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                view.repaint();
                miniMap.repaint();
            }
        });
    }

}
