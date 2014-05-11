package com.verymmog.network.player;

import com.verymmog.model.PlayerInterface;
import com.verymmog.network.message.messages.UpdatePositionMessage;
import com.verymmog.nioengine.output.SerializableOutput;
import org.apache.log4j.Logger;

import java.util.TimerTask;

public class UpdatePositionTask extends TimerTask {
    private Logger logger = Logger.getLogger(getClass());
    private SerializableOutput out;
    private PlayerInterface player;
    private UpdatePositionMessage previous = null;

    public UpdatePositionTask(PlayerInterface player, SerializableOutput out) {
        this.player = player;
        this.out = out;
    }

    @Override
    public void run() {
        logger.trace("UpdatePosition scheduled - begining");
        UpdatePositionMessage newMessage = new UpdatePositionMessage(
                player.getX(),
                player.getY(),
                player.getDx(),
                player.getDy()
        );

        if (previous != null && previous.equals(newMessage)) {
            logger.trace("UpdatePosition scheduled - end, same data");
            return;
        }

        out.send(newMessage);

        previous = newMessage;

        logger.trace("UpdatePosition scheduled - end, modified data");
    }
}
