package com.verymmog.nioengine.processor;

import com.verymmog.nioengine.exception.SelectableChannelException;

import javax.swing.*;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SwingKeyProcessor extends LoggedKeyProcessor {

    private LoggingModel model = new LoggingModel();
    private java.util.Timer t = new Timer();

    public SwingKeyProcessor() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoggingFrame frame = new LoggingFrame(model);
            }
        });
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                model.timeTick();
            }
        }, new Date(), 1000);
    }

    @Override
    protected void logRead(SocketChannel channel, int read) {
        model.addDataIn(channel, read);
    }

    @Override
    protected void logException(SocketChannel channel, SelectableChannelException ex) {

    }

    @Override
    protected void logWrite(SocketChannel channel, int written) {
        model.addDataOut(channel, written);
    }

}
