package com.verymmog.network.player;

import com.verymmog.network.message.messages.ClientDataUpdateMessage;

public interface ClientDataUpdaterInterface {
    public void updatePlayers(ClientDataUpdateMessage message);
}
