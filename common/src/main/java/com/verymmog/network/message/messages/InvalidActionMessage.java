package com.verymmog.network.message.messages;

public class InvalidActionMessage extends CompositeMessage {
    private String detail;

    public InvalidActionMessage(MessageInterface message) {
        this(message, "");
    }

    public InvalidActionMessage(MessageInterface message, String detail) {
        super(message);

        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }
}
