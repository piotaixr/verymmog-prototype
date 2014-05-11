package com.verymmog.nioengine.processor;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class LoggingFrame extends JFrame {

    protected JTable table;
//    protected JScrollPane scrollPane = new JScrollPane();
    protected LoggingModel model;

    public LoggingFrame(LoggingModel model) {
        super("Network Monitor");
        this.model = model;
        table = new JTable(model);

        setLayout(new MigLayout(
                "",
                "[300]",
                "[][300]"

        ));

        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        add(table.getTableHeader(), "wrap");
        add(table, "grow");
//        scrollPane.add(table);

        pack();
    }

}
