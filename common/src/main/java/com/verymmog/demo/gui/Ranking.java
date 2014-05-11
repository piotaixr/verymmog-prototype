/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.verymmog.demo.gui;

import com.verymmog.demo.Data;
import com.verymmog.demo.model.map.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author marion
 */
public class Ranking extends JPanel {

    private Data<Map> data;
    private JLabel podiumTitle;
    private JLabel podiumFirstTitle;
    private JLabel podiumFirstPlayer;
    private JLabel podiumSecondTitle;
    private JLabel podiumSecondPlayer;
    private JLabel podiumThirdTitle;
    private JLabel podiumThirdPlayer;
    private JLabel positionTitle1;
    private JLabel positionTitle2;
    private JLabel positionEstimate1;
    private JLabel positionEstimate2;

    public Ranking(Data<Map> data) {
        this.data = data;
        podiumTitle = new JLabel("PODIUM");
        this.add(podiumTitle);
        podiumFirstTitle = new JLabel("First :");
        this.add(podiumFirstTitle);
        podiumFirstPlayer = new JLabel();
        this.add(podiumFirstPlayer);
        podiumSecondTitle = new JLabel("Second :");
        this.add(podiumSecondTitle);
        podiumSecondPlayer = new JLabel();
        this.add(podiumSecondPlayer);
        podiumThirdTitle = new JLabel("Third :");
        this.add(podiumThirdTitle);
        podiumThirdPlayer = new JLabel();
        this.add(podiumThirdPlayer);
        positionTitle1 = new JLabel("My ranking is between ");
        this.add(positionTitle1);
        positionEstimate1 = new JLabel();
        this.add(positionEstimate1);
        positionTitle2 = new JLabel(" and ");
        this.add(positionTitle2);
        positionEstimate2 = new JLabel();
        this.add(positionEstimate2);
    }

    public void changeValue() {
        podiumFirstPlayer.setText(data.getPodium().get(0).getName());
        podiumSecondPlayer.setText(data.getPodium().get(1).getName());
        podiumThirdPlayer.setText(data.getPodium().get(2).getName());
        positionEstimate1.setText(String.valueOf(data.getNumberPeopleBefore()));
        positionEstimate2.setText(String.valueOf(data.getNumberPeopleInMyLevel() + data.getNumberPeopleBefore()));
    }

}
