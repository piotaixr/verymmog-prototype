/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.verymmog.demo.gui;

import com.verymmog.model.PlayerInterface;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author marion : mariondalle@outlook.com
 */
public class KeyboardListener extends KeyAdapter {

    private PlayerInterface player;

    public KeyboardListener(PlayerInterface player) {
        this.player = player;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!player.isStunned()) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_RIGHT:
                    player.incrementSpeedVector(1, 0);
                    break;
                case KeyEvent.VK_LEFT:
                    player.incrementSpeedVector(-1, 0);
                    break;
                case KeyEvent.VK_UP:
                    player.incrementSpeedVector(0, -1);
                    break;
                case KeyEvent.VK_DOWN:
                    player.incrementSpeedVector(0, 1);
                    break;
            }
        }
    }

}
