package com.verymmog.network.message.messages;

public class CompositeMessage implements MessageInterface {
    private MessageInterface message;

    public CompositeMessage(MessageInterface message) {
        this.message = message;
    }

    public MessageInterface getMessage() {
        return message;
    }
}
