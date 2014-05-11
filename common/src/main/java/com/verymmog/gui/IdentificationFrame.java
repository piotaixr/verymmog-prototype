package com.verymmog.gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class IdentificationFrame extends JFrame {

    private JLabel loginLabel = new JLabel("Login");
    private JLabel passwordLabel = new JLabel("Password");
    private JTextField loginTextField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JButton connectButton = new JButton("Connection");
    private JButton cancelButton = new JButton("Quit");

    public IdentificationFrame(String title) {
        super(title);
        setLayout(new MigLayout(
                "",
                "[70!]10[160!]10[100!]",
                ""
        ));

        add(loginLabel);
        add(loginTextField, "span2, wrap, grow");
        add(passwordLabel);
        add(passwordField, "span2, wrap, grow");
        add(connectButton, "span2, grow");
        add(cancelButton, "grow");

        ActionListener connectListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onConnect(loginTextField.getText(), new String(passwordField.getPassword()));
            }
        };

        loginTextField.addActionListener(connectListener);
        passwordField.addActionListener(connectListener);
        connectButton.addActionListener(connectListener);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setResizable(false);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        pack();
    }


    protected void onCancel() {
        System.exit(0);
    }

    protected abstract void onConnect(String login, String password);
}
