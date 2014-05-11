package com.verymmog.network.message.messages;

import com.verymmog.model.map.MapInterface;

public class MapTransferMessage implements MessageInterface {
    private MapInterface map;


    public MapTransferMessage(MapInterface map) {
        this.map = map;
    }

    public MapInterface getMap() {
        return map;
    }
}
