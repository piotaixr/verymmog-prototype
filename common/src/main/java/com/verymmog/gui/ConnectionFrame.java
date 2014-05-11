package com.verymmog.gui;

import com.verymmog.nioengine.EngineInterface;
import com.verymmog.nioengine.extension.TryConnectionExtension;
import com.verymmog.nioengine.extension.TryConnectionListener;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public abstract class ConnectionFrame extends JFrame {

    private JLabel labelServer;
    private JTextField addressTextField;

    private JButton connectionButton;
    private JButton cancelButton;

    private EngineInterface engine;

    public ConnectionFrame(String title, EngineInterface engine) {
        super(title);
        this.engine = engine;

        setLayout(new MigLayout(
                "",
                "[70!]10[160!]10[100!]",
                ""
        ));

        setResizable(false);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        initAttributes();

        add(labelServer);
        add(addressTextField, "wrap, grow, span 2");
        add(connectionButton, "span 2, grow");
        add(cancelButton, "grow");

        pack();
    }

    private void initAttributes() {
        labelServer = new JLabel();
        labelServer.setText("Serveur");

        addressTextField = new JTextField();
        addressTextField.setText("localhost:42000");
        addressTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tryConnection(addressTextField.getText());
            }
        });
        connectionButton = new JButton();
        connectionButton.setText("Connection");
        connectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tryConnection(addressTextField.getText());
            }
        });

        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO améliorer, c'est moche les System.exit
                System.exit(0);
            }
        });

    }

    /**
     * Appelé par le thread de gestion d'évènements, ne doit pas effectuer de travail lourd.
     *
     * @param channel
     */
    protected abstract void onConnectionSuccess(SocketChannel channel);

    protected InetSocketAddress toAddress(String text) {
        String address = text;
        int port = 0;

        if (text.contains(":")) {
            String[] data = text.split(":");
            address = data[0];
            port = Integer.parseInt(data[1]);
        }

        if (port == 0) {
            port = 42000;
        }

        return new InetSocketAddress(address, port);
    }

    protected void tryConnection(String text) {

        TryConnectionExtension ext = new TryConnectionExtension(toAddress(text));
        ext.addListener(new TryConnectionListener() {
            @Override
            public void onError() {
                JOptionPane.showMessageDialog(ConnectionFrame.this,
                        "Erreur de connexion",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }

            @Override
            public void onSuccess(SocketChannel channel) {
                onConnectionSuccess(channel);
                dispose();
            }
        });

        engine.addExtension(ext);
    }

}
