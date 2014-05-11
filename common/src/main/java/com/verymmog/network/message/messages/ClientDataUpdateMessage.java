package com.verymmog.network.message.messages;

import com.verymmog.network.DataPlayer;

import java.util.SortedSet;

public class ClientDataUpdateMessage implements MessageInterface {

    private SortedSet<DataPlayer> players;

    public ClientDataUpdateMessage(SortedSet<DataPlayer> players) {
        this.players = players;
    }

    public SortedSet<DataPlayer> getPlayers() {
        return players;
    }
}
