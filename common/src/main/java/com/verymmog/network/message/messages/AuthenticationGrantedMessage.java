package com.verymmog.network.message.messages;

public class AuthenticationGrantedMessage implements MessageInterface {
    private String id;
    private String token;

    public AuthenticationGrantedMessage(String id, String token) {
        this.id = id;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public String getToken() {
        return token;
    }
}
